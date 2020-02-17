package com.github.kbnt.java14.fma;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is a quick recap of the predecessor of Foreign Memory Access capability:
 * {@link java.nio.ByteBuffer}. The focus of this recap is to focus on the
 * motivation for the new feature. Thus, we will focus on two major problems
 * with {@code ByteBuffers}:
 * <ol>
 * <li>Cannot allocate more than 2GB (the number of bytes represented by
 * {@code Integer.MAX_VALUE}). In this example set we discuss a scenario of
 * global population in-memory analytics, where 2GB will not be sufficient.
 * While it is perfectly possible for a developer to define more bytebuffers to
 * accomodate the need for more than 2GB, the code would be more complex and
 * without any value ('workaround' code).
 * 
 * <li>The lack of deallocation control. While even this is not a huge issue
 * (deallocation can happen through the GC) there are scenarios where developers
 * want to exercise more control. Additionally, after Java 7, a lot of
 * developers got used with {@link java.lang.AutoCloseable} resources and by
 * now, whenever we talk about allocation/deallocation, I/O interaction, we
 * prefer to have tis convenience.
 * </ol>
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 */
public class ByteBufferExample {

  /**
   * In this case we simply come up with an imaginlary case where we actually need
   * to store in memory a byte for every person on this planet. That means we will
   * need more than 7700000000 bytes. A generous approximation is that we would
   * need 8GB to accommodate this information, so we will need to allocate 4
   * ByteBuffers, max size.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try (Scanner in = new Scanner(System.in)) {
      System.out.println("Allocating buffers...");
      // we keep references to the bytebuffers to use them later on.
      ArrayList<ByteBuffer> buffers = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        ByteBuffer bb = ByteBuffer.allocateDirect(Integer.MAX_VALUE);
        buffers.add(bb);
      }
      System.out.println("Buffers allocated. Now type 'Enter' to deallocate and GC.");

      // wait for some user input...
      in.nextLine();

      System.out.println("Clearing buffer references...");
      buffers.clear();
      // Important: the buffers can be deallocated ONLY after there are no more
      // references to them so they can be GCed!
      System.out.println("Forcing garbage collection...");
      System.gc();

      System.out.println("Type 'Enter' to finish the application...");
      in.nextLine();
    }
  }

}
