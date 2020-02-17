package com.github.kbnt.java14.fma;

import java.io.IOException;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SplittableRandom;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayout.PathElement;
import jdk.incubator.foreign.MemorySegment;

/**
 * A scenario that assumes we want to analyze the time (hours) when people go to
 * sleep and when they wake up (we introduce a bias for sleep time and wake
 * times - go to sleep between 20 and midnight, wake between 6 and 10). For this
 * we are going to use one byte for each individual:<br>
 * <ul>
 * <li>The most significant bit is going to represent the state (0 for waking up
 * and 1 for going to sleep).
 * <li>The 5 least significant bits are going to represent the hour (because the
 * hour is between 0 and 23 we need 5 bits to accommodate that information)
 * </ul>
 * The application will start two threads:
 * <ol>
 * <li>The first one will write continuously data to the memory. For
 * simplification purposes, I avoided any randomization.
 * <li>The second one will read continuously data from the memory. Since this is
 * statistics I sacrificed memory access safety for the sake of speed and
 * simplicity.
 * </ol>
 * 
 * Starting with the primary goal, we are going to focus on 2 main
 * sub-scenarios:
 * <ol>
 * <li><b>In Memory, Large Data.</b> In this case, we want to analyze 7.7
 * billion data points <i>in memory</i>. We are going to allocate A LOT of
 * memory for this case.
 * <li><b>Memory Mapped files (database simulation).</b> In this case, we are
 * going to present how memory segments can be used to construct databases. We
 * don't want to overload the system, so we decresed the population to 1
 * million.
 * </ol>
 * 
 * In order to flip between the scenarios, follow the <i>scenario 1</i> and
 * <i>scenario 2</i> comments at the beginning of the source file.
 * 
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class SleepAnalytics {
  private static enum MODE {
    MEMORY, MMF
  };

  // scenario 1 - in memory, large data (global population)
//  private static long POPULATION = 7700000000L;
//  private static final long RUN_PERIOD = 300 * 1000;
//  private static final MODE M = MODE.MEMORY;

  // scenario 2 - memory-mapped (smaller population)
  private static final long POPULATION = 10000000L;
  private static final MODE M = MODE.MMF;
  private static final long RUN_PERIOD = 10 * 1000;

  private SplittableRandom sr = new SplittableRandom(2342342);
  private static final byte SLEEP = (byte) 0b10000000;
  private static final byte AWAKE = 0;

  private VarHandle byteHandle;
  private MemorySegment mainSegment;
  private Thread dataGathering;
  private Thread statsGathering;

  public static void main(String[] args) {
    SleepAnalytics ima = new SleepAnalytics();
    try {
      ima.initialize();
      ima.prePopulate();
      ima.simulateDataGathering();
      ima.displayStatistics();
    } catch (Throwable e) {
      System.err.println("Oops, something went wrong with one of our examples:");
      e.printStackTrace();
    } finally {
      ima.destroy();
    }
  }

  private void destroy() {
    try {
      Thread.sleep(RUN_PERIOD);
    } catch (InterruptedException e) {
      System.err.println("The main thread was interrupted: " + e.getMessage());
      e.printStackTrace();
    }
    if (dataGathering != null) {
      dataGathering.interrupt();
    }
    if (statsGathering != null) {
      statsGathering.interrupt();
    }

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      System.err.println("The main thread was interrupted: " + e.getMessage());
      e.printStackTrace();
    }

    if (mainSegment != null)
      mainSegment.close();
  }

  public void initialize() throws IOException {
    MemoryLayout byteArrayLayout = MemoryLayout.ofSequence(POPULATION,
        MemoryLayout.ofValueBits(8, ByteOrder.nativeOrder()));
    byteHandle = byteArrayLayout.varHandle(byte.class, PathElement.sequenceElement());
    long start = System.currentTimeMillis();
    mainSegment = switch (M) {
    case MEMORY -> MemorySegment.allocateNative(POPULATION);
    case MMF -> {
      Path path = Paths.get("target", "runtime", "sleep-db.bin");
      Files.createDirectories(path.getParent());
      Files.createFile(path);

      yield MemorySegment.mapFromPath(path, POPULATION, MapMode.READ_WRITE);
    }
    };
    System.out.println("Memory segment allocated for " + POPULATION + " bytes in "
        + (System.currentTimeMillis() - start) + " milliseconds.");
  }

  private void prePopulate() {
    MemoryAddress base = mainSegment.baseAddress();
    // we pre-populate the memory assuming that half of the population is awake and
    // half is asleep
    long start = System.currentTimeMillis();
    for (long l = 0; l < POPULATION; l++) {
      byteHandle.set(base, l, l % 2 == 0 ? SLEEP : AWAKE);
    }
    System.out.println("Start data prepopulated in " + (System.currentTimeMillis() - start) + " milliseconds.");
  }

  private void simulateDataGathering() {
    dataGathering = new Thread() {

      @Override
      public void run() {
        try (MemorySegment gatheringSegment = mainSegment.acquire();) {
          MemoryAddress base = gatheringSegment.baseAddress();

          break_label: while (true) {
            long start = System.currentTimeMillis();
            for (long l = 0; l < POPULATION; l++) {
              byte current = (byte) byteHandle.get(base, l);
              byteHandle.set(base, l, flipState(current, l));
              if (interrupted())
                break break_label;
            }
            System.out.println(
                "One full memory traversal for writing completed in " + (System.currentTimeMillis() - start) + "!");

          }
          System.out.println("Finished data gathering thread...");
        } catch (Exception e) {
          System.err.println("Exception while running the data gathering simulation.");
          e.printStackTrace();
        }
      }
    };
    dataGathering.start();
  }

  private void displayStatistics() {
    statsGathering = new Thread() {

      @Override
      public void run() {
        try (MemorySegment statsSegment = mainSegment.acquire().asReadOnly();) {
          MemoryAddress base = statsSegment.baseAddress();
          break_label: while (true) {
            long[] statHoursForSleep = new long[24];
            long[] statHoursForWake = new long[24];

            long start = System.currentTimeMillis();
            for (long l = 0; l < POPULATION; l++) {
              byte current = (byte) byteHandle.get(base, l);
              if ((current & SLEEP) == SLEEP) {
                statHoursForSleep[current & 0b11111]++;
              } else {
                statHoursForWake[current & 0b11111]++;
              }

              if (interrupted())
                break break_label;
            }

            long asleep = 0;
            for (int i = 0; i < statHoursForSleep.length; i++)
              asleep += statHoursForSleep[i];

            long awake = 0;
            for (int i = 0; i < statHoursForWake.length; i++)
              awake += statHoursForWake[i];

            if (awake > 0 && asleep > 0) {

              System.out.println("============");
              System.out.println("These are approximations (stats gathered in " + (System.currentTimeMillis() - start)
                  + " milliseconds):");
              System.out.println("We have " + asleep + " people asleep and " + awake + " awake.");
              System.out.println("The hours distribution for going to sleep:");
              for (int i = 0; i < statHoursForSleep.length; i++)
                System.out.println("Hour " + i + ": " + (double) 100 * statHoursForSleep[i] / asleep + "%.");

              System.out.println("The hours distribution for waking up:");
              for (int i = 0; i < statHoursForWake.length; i++)
                System.out.println("Hour " + i + ": " + (double) 100 * statHoursForWake[i] / awake + "%.");
            }

            Thread.sleep(1000);
          }
        } catch (InterruptedException ie) {
          System.err.println("We got an interruption call ;-).");
        } catch (Exception e) {
          System.err.println("Exception while running the data gathering simulation.");
          e.printStackTrace();
        } finally {
          System.out.println("Finished stats gathering thread...");
        }
      }
    };
    statsGathering.start();
  }

  private byte flipState(byte input, long personNumber) {
    boolean isAsleep = (input & SLEEP) == SLEEP;
    int newHour;
    // common scenario - go to sleep between 20 and 23, wake between 6 and 9
    if (isAsleep)
      newHour = 6 + sr.nextInt(4);
    else
      newHour = 20 + sr.nextInt(4);

    // wake up or or go to sleep and record the hour
    return (byte) ((isAsleep ? AWAKE : SLEEP) | newHour);
  }
}
