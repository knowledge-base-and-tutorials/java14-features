package com.github.kbnt.java14.npe;

import java.util.ArrayList;

/**
 * A container for methods showing common cases where NPE happens.
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class HelpfulNPEMessages {
  class Address {
    String streetName;
    String city;
    String state;
    String zipCode;
  }

  class Person {
    String firstName;
    String lastName;
    Integer age;
    Address address;

    public String getFirstName() {
      return firstName;
    }
  }

  public static void main(String[] args) {
    try {
      HelpfulNPEMessages pm = new HelpfulNPEMessages();
//      pm.example00SimpleNullVariable();
//      pm.example01ComplexObjectNesting();
//      pm.example02MethodReturnsNull();
//      pm.example03ListWithNullElements();
      pm.example04Autoboxing();
    } catch (Throwable e) {
      System.err.println("Ooops, something went wrong with one of our examples:");
      e.printStackTrace();
    }
  }

  private void insertExampleSeparator() {
    System.err.println("==============================================");
  }

  public void example00SimpleNullVariable() {
    insertExampleSeparator();

    try {
      // this line will give us a null person
      Person p = "just something to trick the compiler".length() > 0 ? null : new Person();
      System.out.println("My first name is " + p.firstName);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  public void example01ComplexObjectNesting() {
    insertExampleSeparator();

    try {
      Person p = new Person();

      System.out.println("My city is " + p.address.city);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

  }

  public void example02MethodReturnsNull() {
    insertExampleSeparator();

    try {
      Person p = new Person();

      System.out.println("The lenght of first name is " + p.getFirstName().length());
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  public void example03ListWithNullElements() {
    insertExampleSeparator();

    ArrayList<Person> persons = new ArrayList<>(10);
    // we insert some persons to avoid an java.lang.IndexOutOfBoundsException
    persons.add(new Person());
    persons.add(new Person());
    persons.add(new Person());

    // we nullify the first person
    persons.set(0, null);
    try {
      System.out.println("The name of the first person is " + persons.get(0).firstName);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    // if we drain the list into an array, the NPE message is more explicit about
    // the index where the problem is
    Person[] arrayOfPersons = persons.toArray(new Person[0]);
    try {
      System.out.println("The name of the first person is " + arrayOfPersons[0].firstName);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  public void example04Autoboxing() {
    insertExampleSeparator();
    try {
      Person person = new Person();
      int personsAge = person.age;
      System.out.println("Person's age is: " + personsAge);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

}
