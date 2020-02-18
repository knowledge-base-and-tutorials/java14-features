package com.github.kbnt.java14.fma;

import java.io.IOException;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayout.PathElement;
import jdk.incubator.foreign.MemorySegment;

/**
 * A set of examples primarily inspired from the
 * <a href="https://openjdk.java.net/jeps/370">JEP 370</a>.<br>
 * <br>
 * Note that the JEP's owner gave a talk on 2/1/2020 at
 * <a href="https://fosdem.org/2020/schedule/event/bytebuffers/">fosdem.org</a>
 * conference. I strongly recommend that, before watching my video or reading
 * this code, you see that material first.
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class ForeignMemoryAccessExamples {

  public static void main(String[] args) {
    try {
      ForeignMemoryAccessExamples fmae = new ForeignMemoryAccessExamples();
      fmae.example00Basic();
      fmae.example01Strides();
      fmae.example02Layout();
      fmae.example03SSNToCreditScore();
      fmae.example04SSNToCreditScoreWithPadding();
    } catch (Throwable e) {
      System.err.println("Oops, something went wrong with one of our examples:");
      e.printStackTrace();
    }
  }

  /**
   * Show the basics of the API: covers {@code MemorySegment} and
   * {@code MemoryAddress}. Also writes he memory content to a file...
   * 
   * @throws IOException
   */
  public void example00Basic() throws IOException {
    insertExampleSeparator();

    ByteOrder order = ByteOrder.nativeOrder();
    System.out.println("Using native order: " + order);
    VarHandle intHandle = MemoryHandles.varHandle(int.class, order);

    // allocate an in-memory segment of 100 bytes (this can actually accommodate 25
    // int values). Note a subtle but important feature: the segment is
    // AutoClosable!
    try (MemorySegment segment = MemorySegment.allocateNative(100)) {
      MemoryAddress base = segment.baseAddress();
      // we do the offset computations by ourselves (not very nice)
      for (int i = 0; i < 25; i++) {
        intHandle.set(base.addOffset(i * 4), i);
      }

      // take the same segment and flush it into a file to analyze further what
      // happened
      Path path = Paths.get("target", "runtime", "fmae-basic.bin");
      Files.createDirectories(path.getParent());
      Files.write(path, segment.toByteArray());
      System.out.println("Memory flushed to file...");
    }
  }

  /**
   * An improvement of the example above but with two modifications:
   * <ol>
   * <li>Usage of a memory mapped file instead of grabbing the buffer as a byte
   * array and flushing it to disk.
   * <li>Leveraging higher level constructs that will allow us to avoid doing
   * offset calculations.
   * </ol>
   * 
   * @throws IOException
   */
  public void example01Strides() throws IOException {
    insertExampleSeparator();

    ByteOrder order = ByteOrder.nativeOrder();
    VarHandle intHandle = MemoryHandles.varHandle(int.class, order);
    // this handle will use two coordinates instead of 1. 4 (the size of an Java
    // integer) represents the constant
    // stride that will get multiplied with the actual index.
    VarHandle intElemHandle = MemoryHandles.withStride(intHandle, 4);

    // take the same segment and dump it into a file to analyze further what
    // happened
    Path path = Paths.get("target", "runtime", "fmae-strides.bin");
    Files.createDirectories(path.getParent());
    Files.createFile(path);

    // note difference (1) - leveraging the memory mapped file. Note that closing
    // the segment closes the file as well.
    try (MemorySegment segment = MemorySegment.mapFromPath(path, 100, MapMode.READ_WRITE)) {
      MemoryAddress base = segment.baseAddress();
      for (int i = 0; i < 25; i++) {
        // observe difference (2) from the previous example - i also play the role of a
        // positioning index (the second coordinate)
        intElemHandle.set(base, (long) i, i);
      }
    }
  }

  /**
   * An example from the JEP looking at the basics of memory layouts.
   * 
   * @throws IOException
   */
  public void example02Layout() throws IOException {
    insertExampleSeparator();

    MemoryLayout intArrayLayout = MemoryLayout.ofSequence(25, MemoryLayout.ofValueBits(32, ByteOrder.nativeOrder()));
    VarHandle intElemHandle = intArrayLayout.varHandle(int.class, PathElement.sequenceElement());

    // take the same segment and dump it into a file to analyze further what
    // happened
    Path path = Paths.get("target", "runtime", "fmae-layout.bin");
    Files.createDirectories(path.getParent());
    Files.createFile(path);

    try (MemorySegment segment = MemorySegment.mapFromPath(path, 100, MapMode.READ_WRITE)) {
      MemoryAddress base = segment.baseAddress();
      for (int i = 0; i < 25; i++) {
        intElemHandle.set(base, (long) i, i);
      }

      System.out.println("Memory flushed to file...");
    }
  }

  /**
   * An example where we look at a slightly more complex layout. We want to mimic
   * the association between you social security number and the credit score. The
   * examples associates 100 SSN numbers from 123456000 onwards to credit scores
   * 700 upwards.
   * 
   * @throws IOException
   */
  public void example03SSNToCreditScore() throws IOException {
    insertExampleSeparator();

    ByteOrder order = ByteOrder.nativeOrder();
    MemoryLayout ssnAndCreditStruct = MemoryLayout.ofStruct(
        MemoryLayout.ofSequence(9, MemoryLayout.ofValueBits(Character.SIZE, order)).withName("ssn"),
        MemoryLayout.ofValueBits(Integer.SIZE, order).withName("creditScore").withBitAlignment(16));

    int numberOfElements = 100;
    MemoryLayout ssnAndCredit = MemoryLayout.ofSequence(numberOfElements, ssnAndCreditStruct);

    System.out.println("Layout: " + ssnAndCredit);

    VarHandle ssnHandle = ssnAndCredit.varHandle(char.class, PathElement.sequenceElement(),
        PathElement.groupElement("ssn"), PathElement.sequenceElement());
    VarHandle creditScoreHandle = ssnAndCredit.varHandle(int.class, PathElement.sequenceElement(),
        PathElement.groupElement("creditScore"));

    // take the same segment and dump it into a file to analyze further what
    // happened
    Path path = Paths.get("target", "runtime", "fmae-memory-layouts.bin");
    Files.createDirectories(path.getParent());
    Files.createFile(path);

    try (MemorySegment segment = MemorySegment.mapFromPath(path, ssnAndCredit.byteSize(), MapMode.READ_WRITE)) {
      MemoryAddress base = segment.baseAddress();
      for (int i = 0; i < numberOfElements; i++) {
        char[] ssn = ((123456000 + i) + "").toCharArray();
        for (int j = 0; j < ssn.length; j++) {
          ssnHandle.set(base, (long) i, (long) j, ssn[j]);
        }

        creditScoreHandle.set(base, (long) i, 700 + i);
      }

      System.out.println("Memory flushed to file...");
    }

  }

  /**
   * The same example as above, but instead of using a different alignment, we
   * will use padding.<br>
   * <br>
   * 
   * <b>Credit:</b> this is not original work - Maurizio Cimadamore suggessted
   * using padding as alternative to changing the alignment. <br>
   * Other coworkers suggested that because of architectures considerents (CPU's
   * register's structure), it would be best to pad the structures so they end up
   * as multiples of 8 bytes.<br>
   * In this particular case, if we use a compact memory, for one stucture we will
   * use 9x2+4=22 bytes. A padding to get to 24 bytes would be recommended. The
   * example below doesn't need the change for bit alignment.
   * 
   * @throws IOException
   */
  public void example04SSNToCreditScoreWithPadding() throws IOException {
    insertExampleSeparator();

    ByteOrder order = ByteOrder.nativeOrder();
    MemoryLayout ssnAndCreditStruct = MemoryLayout.ofStruct(
        MemoryLayout.ofSequence(9, MemoryLayout.ofValueBits(Character.SIZE, order)).withName("ssn"),
        MemoryLayout.ofPaddingBits(16), MemoryLayout.ofValueBits(Integer.SIZE, order).withName("creditScore"));

    int numberOfElements = 100;
    MemoryLayout ssnAndCredit = MemoryLayout.ofSequence(numberOfElements, ssnAndCreditStruct);

    System.out.println("Layout: " + ssnAndCredit);

    VarHandle ssnHandle = ssnAndCredit.varHandle(char.class, PathElement.sequenceElement(),
        PathElement.groupElement("ssn"), PathElement.sequenceElement());
    VarHandle creditScoreHandle = ssnAndCredit.varHandle(int.class, PathElement.sequenceElement(),
        PathElement.groupElement("creditScore"));

    // take the same segment and dump it into a file to analyze further what
    // happened
    Path path = Paths.get("target", "runtime", "fmae-memory-layouts-padding.bin");
    Files.createDirectories(path.getParent());
    Files.createFile(path);

    try (MemorySegment segment = MemorySegment.mapFromPath(path, ssnAndCredit.byteSize(), MapMode.READ_WRITE)) {
      MemoryAddress base = segment.baseAddress();
      for (int i = 0; i < numberOfElements; i++) {
        char[] ssn = ((123456000 + i) + "").toCharArray();
        for (int j = 0; j < ssn.length; j++) {
          ssnHandle.set(base, (long) i, (long) j, ssn[j]);
        }

        creditScoreHandle.set(base, (long) i, 700 + i);
      }

      System.out.println("Memory flushed to file...");
    }

  }

  private void insertExampleSeparator() {
    System.err.println("==============================================");
  }

}
