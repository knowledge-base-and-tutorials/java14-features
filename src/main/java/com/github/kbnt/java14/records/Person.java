package com.github.kbnt.java14.records;

/**
 * A simple person (we can get his/her first name, last name and age).
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public interface Person {
  public String firstName();

  public String lastName();

  public int age();
}
