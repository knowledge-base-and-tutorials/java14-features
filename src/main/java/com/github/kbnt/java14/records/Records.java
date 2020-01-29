package com.github.kbnt.java14.records;

/**
 * 
 * Record properties (as per <a href="https://openjdk.java.net/jeps/359">JEP
 * 359</a>):
 * <ol>
 * <li><b>private final field</b> for each component;
 * <li><b>public read accessor</b> method for each component of the state
 * description, with the <b>same name and type as the component</b>;
 * <li>a public constructor, whose <b>signature is the same as the state
 * description, which initializes each field from the corresponding
 * argument</b>;
 * <li>implementations of <b>equals</b> and <b>hashCode</b> that say two records
 * are equal if they are of the <b>same type and contain the same state</b>;
 * <li>implementation of <b>toString</b> that includes the string representation
 * of all the record components, with their names;
 * <li><b>cannot extend</b> any other class;
 * <li><b>cannot declare instance fields</b> other than the private final fields
 * which correspond to components of the state description;
 * <li>any other <b>fields which are declared must be static</b>;
 * <li><b>implicitly final</b>;
 * <li><b>cannot be abstract</b>;
 * <li><b>are shallowly immutable</b>.
 * </ol>
 * 
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class Records {

  public static void main(String[] args) {
    try {
      Records re = new Records();
      re.example00SimplePerson();
    } catch (Throwable e) {
      System.err.println("Ooops, something went wrong with one of our examples:");
      e.printStackTrace();
    }
  }

  private void insertExampleSeparator() {
    System.err.println("==============================================");
  }

  private Person getPerson() {
    // this line shows a person defined from a class - pre Java 14
    return new PersonClass("Mickey", "Mouse", 10);
    // this line shows a person instantiated from a record - Java 14+ (note the
    // presence of the generated constructor)
//    return new PersonRecord("Mickey", "O'Record", 10);
  }

  public void example00SimplePerson() {
    insertExampleSeparator();
    Person p = getPerson();

    // accessing the components
    System.out.println("My first name is " + p.firstName() + ".");
    System.out.println("My last name is " + p.lastName() + ".");
    System.out.println("I am " + p.age() + " years old.");

    // hashcode and toString
    System.out.println("hashCode() returns: " + p.hashCode());
    System.out.println("toString() returns: " + p);

    // equals
    Person toCompareTo = getPerson();
    System.out.println("Are the two persons equal?: " + p.equals(toCompareTo));
  }

}
