package com.github.kbnt.java14.se;

import java.util.Date;

/**
 * In this example set we assume that we are working on a calendar application,
 * following the same thought process as the snippets in
 * <a href="https://openjdk.java.net/jeps/361">JEP 361 (Switch Expressions)</a>.
 * The main idea is that we want to have meetings every other day, starting
 * Mondays. With that idea in mind, we do some variations.<br>
 * 
 * <b>Note</b>: at the moment of writing this material, as a developer you still
 * had to use the {@code --enable-preview} JVM option.
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class SwitchExpressions {

  public static void main(String[] args) {
    try {
      SwitchExpressions se = new SwitchExpressions();
      se.example00PreJava12();
      se.example01MultiCase();
      se.example02ArrowLabels();
      se.example03SwitchAsExpressionAndYield();
      se.example04SwitchAsExpressionExhaustiveness();
    } catch (Throwable e) {
      System.err.println("Oops, something went wrong with one of our examples:");
      e.printStackTrace();
    }
  }

  private void insertExampleSeparator() {
    System.err.println("==============================================");
  }

  /**
   * This example shows a classic switch statement. It is provided just to have a
   * starting point for comparison (don't expect anything surprising here!).
   */
  public void example00PreJava12() {
    insertExampleSeparator();

    System.out.println("This is how we would meet every other day before Java 14( 13 and 12):");
    WeekDay day = WeekDay.MONDAY;
    switch (day) {
    // note the ":" for now
    case MONDAY:
      System.out.println("Let's meet!");
      // we have to break, otherwise everything after this will be executed if we
      // forget to!
      break;
    case TUESDAY:
      break;
    case WEDNESDAY:
      System.out.println("Let's meet!");
      break;
    case THURSDAY:
      break;
    case FRIDAY:
      System.out.println("Let's meet!");
      break;
    case SATURDAY:
      break;
    case SUNDAY:
      System.out.println("Let's meet (although Sunday should be a free day, man!)!");
      break;
    default:
      System.out.println("Do I really need this?!");
      break;
    }
  }

  /**
   * With the new syntax, we can "condense" some of the case statements.
   */
  public void example01MultiCase() {
    insertExampleSeparator();
    WeekDay day = WeekDay.SUNDAY;
    System.out.println(
        "This is how we would meet every other day after Java 14( 13 and 12), using more compact case clauses:");
    switch (day) {
    // more compact code
    case MONDAY, WEDNESDAY, FRIDAY:
      System.out.println("Let's meet!");
      // we have to break, otherwise everything after this will be executed if we
      // forget to!
      break;
    case TUESDAY, THURSDAY, SATURDAY:
      break;
    case SUNDAY:
      System.out.println("Let's meet (although Sunday should be a free day, man!)!");
      // we have to break, otherwise everything after this will be executed if we
      // forget to!
      break;
    }
  }

  /**
   * Arrow labels open the capability for execution blocks, scope, no fall-through
   * and expressions.
   */
  public void example02ArrowLabels() {
    insertExampleSeparator();
    WeekDay day = WeekDay.SUNDAY;
    System.out.println(
        "This is how we would meet every other day after Java 14( 13 and 12), using more compact case clauses and arrow labels:");
    switch (day) {
    // even more compact code, with the arrow labels (no more breaks needed,
    // execution implicitly doesn't fall through stop after the break)!
    case MONDAY, WEDNESDAY, FRIDAY -> System.out.println("Let's meet!");
    // we have to break, otherwise everything after this will be executed if we
    // forget to!
    case TUESDAY, THURSDAY, SATURDAY -> {// empty block, not really needed
    }
    case SUNDAY -> System.out.println("Let's meet (although Sunday should be a free day, man!)!");
    // we have to break, otherwise everything after this will be executed if we
    // forget to!
    }
  }

  /**
   * Since now we get access to block codes, let's collect the result of the
   * execution in that block.
   */
  public void example03SwitchAsExpressionAndYield() {
    insertExampleSeparator();
    WeekDay day = WeekDay.SUNDAY;

    String message = switch (day) {
    // simple syntax that shows an implicit yield
    case MONDAY, WEDNESDAY, FRIDAY -> "Let's meet!";
    case TUESDAY, THURSDAY, SATURDAY -> {
      // syntax showing an implicit yield and variable scoping.
      Date time = new Date(System.currentTimeMillis());
      yield "No meeting today because it is " + day + ". To be more precise, it is " + time + ".";
    }
    case SUNDAY -> {
      Date time = new Date(System.currentTimeMillis());
      yield "Take some rest today because it is " + time + ".";
    }
    };

    System.out.println("The message for the upcoming meeting is: " + message);

  }

  /**
   * When we leverage the <i>switch</i> statement as an expression to collect the
   * result, we need to be <b>exhaustive</b>. In plain words, that means that we
   * need to show to the compiler that we covered all the cases - otherwise the
   * variable we declare might remain uninitialized and the compiler will generate
   * an error.
   */
  public void example04SwitchAsExpressionExhaustiveness() {
    insertExampleSeparator();
    int day = 6;

    String message = switch (day) {
    case 1, 3, 5 -> "Let's meet!";
    case 2, 4, 6 -> {
      Date time = new Date(System.currentTimeMillis());
      yield "No meeting today because it is " + day + ". To be more precise, it is " + time + ".";
    }
    case 7 -> {
      Date time = new Date(System.currentTimeMillis());
      yield "Take some rest today because it is " + time + ".";
    }
    default -> throw new IllegalArgumentException("The day must be between 1 and 7");
    };

    System.out.println("The message for the upcoming meeting is: " + message);
  }
}
