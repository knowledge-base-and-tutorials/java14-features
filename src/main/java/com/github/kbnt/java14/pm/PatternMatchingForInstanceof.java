package com.github.kbnt.java14.pm;

/**
 * The <b>pattern matching for <i>instanceof</i></b>
 * (<a href="https://openjdk.java.net/jeps/305">JEP 305</a>) feature allows us
 * to test if a reference is an instance of a class and cast it in "one shot"
 * eliminating one line of boilerplate cast. <br>
 * <br>
 * This is a typical example where the impact might seem insignificant - if
 * applied at a small scale, but think about all the lines of Java code out
 * there, it keeps adding up.<br>
 * <br>
 * 
 * Without trying to compare apples to pears, let me make an electrical analogy:
 * accordingly to <a href=
 * "https://www.energy.gov/energysaver/save-electricity-and-fuel/lighting-choices-save-you-money">www.energy.gov</a>,
 * "by replacing your home's five most frequently used light fixtures or bulbs
 * with models that have earned the ENERGY STAR, you can save $45 each year.".
 * At the moment of writing this, in US (where electricity is <i>not that
 * expensive</i>) there were are around <a href=
 * "https://www.statista.com/statistics/183635/number-of-households-in-the-us/">130
 * million households</a>. With a very rough aproximation, that means savings of
 * <i>$5.8 billion</i> per year (larger than
 * <a href="https://en.wikipedia.org/wiki/Montenegro">Montenegro's</a> GDP,
 * which is <a href="https://www.worldometers.info/gdp/gdp-by-country/">$4.8
 * billion</a> per year).<br>
 * <br>
 * 
 * Back from the analogy, we are 9+ million Java developers... Imagine how many
 * times during the latest 20+ years we created redundant lines of code like
 * this..
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class PatternMatchingForInstanceof {
  private String someIrrelevantState;

  public static void main(String[] args) {
    try {
      PatternMatchingForInstanceof pm = new PatternMatchingForInstanceof();
      pm.example00OldSchool();
      pm.example01NewSimpleWay();
    } catch (Throwable e) {
      System.err.println("Oops, something went wrong with one of our examples:");
      e.printStackTrace();
    }

  }

  private void insertExampleSeparator() {
    System.err.println("==============================================");
  }

  /**
   * This example shows how we used to test for instances of and assign variables
   * before Java 14. Note the feeling of redundancy when we do the cast...
   */
  public void example00OldSchool() {
    insertExampleSeparator();

    Object mysteriousObject = "This is a string but our code was written to treat it as an object...";

    // here we want to treat it as a string
    if (mysteriousObject instanceof String) {
      // somehow writing this is redundant as at this point we already know the object
      // is a string...
      String stringObject = (String) mysteriousObject;
      System.out
          .println("The type of the mysterious object is String and the length is " + stringObject.length() + ".");
    }
  }

  /**
   * This shows how the <b>pattern matching for instanceof</b> can help us get rid of one line of the code.
   */
  public void example01NewSimpleWay() {
    insertExampleSeparator();

    Object mysteriousObject = "This is a string but our code was written to treat it as an object...";

    // what if we can test and assign at the same time, while eliminating one line of code?!
    if (mysteriousObject instanceof String stringObject) {
      System.out.println("The matched string's length is " + stringObject.length() + ".");
    }
  }

  /**
   * At the beginning the scope of the newly introduced variable can be confusing.
   * As a simple rule of thumb keep this in mind: you can use the variable if and
   * only if you can figure (and so does the compiler) that your object can be successfully
   * casted to the new type! This method provides some examples.
   */
  public void example02Scope() {
    insertExampleSeparator();

    Object mysteriousObject = "This is a string but our code was written to treat it as an object...";

    // only the block where the test is positive accepts the variable    
    if (mysteriousObject instanceof String stringObject) {
      stringObject.length();
    } else {
      // uncommenting the next line will lead to compilation error...
      // System.out.println("The matched string's length is " + stringObject.length() + ".");      
    }
    
    // only the block where the test is positive accepts the variable    
    if (!(mysteriousObject instanceof String stringObject)) {
      // uncommenting the next line will lead to compilation error...
      // stringObject.length();
    } else {
      stringObject.length();      
    }
    
    // this will work because when we call the length() method we are sure that the mysteriousObject (converted to stringObject) was a string
    boolean is69InLength = (mysteriousObject instanceof String stringObject) && stringObject.length() == 69;
    
    // this cannot work because we can't be sure that the mysteriousObject is a string at the time of getting the lenght
    // boolean isStringOr69InLength = (mysteriousObject instanceof String stringObject) || stringObject.length() == 69;
  }

  @Override
  public boolean equals(Object o) {
    // an old school method to do object comparison was
    boolean oldSchoolResult = (o instanceof PatternMatchingForInstanceof) && 
        ((PatternMatchingForInstanceof) o).someIrrelevantState.equalsIgnoreCase(this.someIrrelevantState);
    
    // the new, more elegant way to do the comparison
    boolean newWayResult = (o instanceof PatternMatchingForInstanceof pm) && pm.someIrrelevantState.equals(this.someIrrelevantState); 
    return newWayResult;
  }

}
