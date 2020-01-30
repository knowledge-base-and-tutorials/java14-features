package com.github.kbnt.java14.tb;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class dives into the <a href="https://openjdk.java.net/jeps/368">JEP 368
 * (Text Blocks)</a> enhancement proposal.<br>
 * <br>
 * A comprehensive material on this feature is this <a href="
 * http://cr.openjdk.java.net/~jlaskey/Strings/TextBlocksGuide_v10.html">programmer's
 * guide</a>.
 * 
 * Note that Java 14 sees this JEP as a second preview, so this is not entirely
 * new to Java 14.
 * 
 * @author <a href="mailto:tech.meshter@gmail.com">Chris T</a>
 *
 */
public class TextBlocks {

  public static void main(String[] args) {
    try {
      TextBlocks tb = new TextBlocks();
      tb.example00OldWayVersusNewWay();
      tb.example01Description();
      tb.example03StringStripIndent();
      tb.example04Inception();
      tb.example05Java14NewEscapeSequences();
    } catch (Throwable e) {
      System.err.println("Oops, something went wrong with one of our examples:");
      e.printStackTrace();
    }
  }

  private void insertExampleSeparator() {
    System.err.println("==============================================");
  }

  /**
   * A basic comparison between the old way of declaring long text fields and the
   * way they can be.
   */
  public void example00OldWayVersusNewWay() {
    insertExampleSeparator();

    // I am sure you must have seen this at least once in your lifetime as a
    // developer (some
    // hardcoded SQL statement) - (source: https://openjdk.java.net/jeps/368)
    String oldSchoolSQLStatement = "SELECT `EMP_ID`, `LAST_NAME` FROM `EMPLOYEE_TB`\n"
        + "WHERE `CITY` = 'INDIANAPOLIS'\n" + "ORDER BY `EMP_ID`, `LAST_NAME`;\n";

    System.out.println("# Old way to write the SQL statement:");
    System.out.println(oldSchoolSQLStatement);

    // introduced in Java 13 as a preview and revisited based on the community's
    // feedback
    String newWaySQLStatement = """
        SELECT `EMP_ID`, `LAST_NAME` FROM `EMPLOYEE_TB`
        WHERE `CITY` = 'INDIANAPOLIS'
        ORDER BY `EMP_ID`, `LAST_NAME`;
        """;
    System.out.println();
    System.out.println("# New way to write the SQL statement (same output as previous one):");
    System.out.println(newWaySQLStatement);
  }

  /**
   * This method follows the most important aspects in the "Description" paragraph
   * from the JEP documentation.<br>
   * <br>
   * 
   * Note that the <b>trailing line policy</b> requires a longer conversation and
   * it is a little bit harder to document without proper visual support.
   * Therefore I recommend watching the video that actually shows how it works:
   * 
   * TODO insert the video link here...
   * 
   */
  public void example01Description() {
    insertExampleSeparator();

    String aTextBlock = """
        ! <- The content begins at the first character after the line terminator of the opening delimiter ('!' is the first character).

        * Three " are used so you don't need to escape anymore the quotation mark.

        * If somehow you need three " one after another, you need to escape them to produce this output: \"""

        * The use of \\n in a text block is permitted, but not necessary or recommended. Here is the next line: \nNext line :-).
        """;

    System.out.println("# Some key points from the description of the feature:");
    System.out.println(aTextBlock);
    System.out.println();

    String determiningLineEndTerminator = """
           * The additional space is filled by the fact that the end terminator is moved to the left side (if you count, there are exactly 3 characters)!
           <- count here to the left: 1, 2, 3 :-)
        """;

    System.out.println("# End terminator's position might define the determining line:");
    System.out.println(determiningLineEndTerminator);
    System.out.println();

    String determiningLineLeftmostContent = """
           If there are multiple lines, the left-most decides the exact positioning!
        >- I wonder who the leftmost content line is?!
              Well,guess who?!
           """;

    System.out.println("# The leftmost content line's position defines the determining line:");
    System.out.println(determiningLineLeftmostContent);
    System.out.println();

    String escapeSequences = """
        Old escape sequences are supported. We will * as delimiters to make the point:
        Here is a tab: *\t*
        Single quotes escaped: \'
        Single quotes un-escaped: '
        Double quotes escaped: \"
        Double quotes un-escaped: "
        Backslash escaped: \\ (un-escaped backslash has a new meaning in Java 14 - check the example02Java14NewEscapeSequences)
        New line: *\n*
        """;

    System.out.println("# Old escape sequences are supported:");
    System.out.println(escapeSequences);
    System.out.println();
  }

  /**
   * The <b>text blocks</b> feature is processed by the compiler in a particular
   * way (full description in the mentioned JEP document section called
   * {@code Compile-time processing}).<br>
   * 
   * Several APIs have been added that mimic this behavior and one of them is called
   * stripIndent(). This examples tries to show how the compiler processes the
   * string.
   * 
   * @see String#stripIndent()
   */
  public void example03StringStripIndent() {
    insertExampleSeparator();
    String endsWithNewLine = """
        \s        <- this line starts with 5 blank spaces.
        \s\s\s\s\s<- count again: 1, 2, 3, 4, 5
       """;
    System.out.println("The string ending empty trailing line:");
    System.out.println(endsWithNewLine);
    
    System.out.println();
    System.out.println("If we call stripIndent, the white spaces are not eliminated because the compiler 'moved' the trailing line at the beginning, eliminating all the incidental white space:");
    System.out.println(endsWithNewLine.stripIndent());
    
    String endsWithAsterisk = """
        \s        <- this line starts with 5 blank spaces.
        \s\s\s\s\s<- count again: 1, 2, 3, 4, 5
                  *<- here we will have 10 spaces till the beginning of the line (because we have 5 times the new escape character for space)""";
    System.out.println("Similarly as above, but we added one char to the trailing line (the incidental white space stops at the first '\\' character):");
    System.out.println(endsWithAsterisk);
    
    System.out.println();
    System.out.println("If we call stripIndent on the previous string, the white spaces we introduced are all now considered incidental whitespace:");
    System.out.println(endsWithAsterisk.replaceAll("10 spaces till the beginning of the line \\(because we have 5 times the new escape character for space\\)", "5 spaces till the beginning of the line (because stripIndent removed those spaces that we introduced)").stripIndent());    
  }

  /**
   * This is an ironical example making a reference to the
   * <a href="https://www.imdb.com/title/tt1375666/">Inception</a> movie and the
   * idea of a "dream in a dream".<br>
   * <br>
   * 
   * The example builds a SQL statement using the JS build in engine leveraging
   * JavaScript's ES6 template literals all defined as text blocks in Java.<br>
   * <br>
   * 
   * The irony and analogy to the mentioned movie comes from the fact that:
   * <ul>
   * <li>First, we demo the <b>text blocks</b> as a Java feature.
   * <li>Secondly, we use <b>text blocks</b> from Java to define JavaScript (ES6)
   * that leverages <a href=
   * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals">Template
   * literals</a>.
   * <li>Thirdly, we use the <a href=
   * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Template_literals">template
   * literals</a> to produce a SQL statement.
   * </ul>
   * 
   * <b>Notes</b>:
   * <ul>
   * <li>it is uncertain what will happen with JS support in the future Java
   * versions (JEP 335: Deprecate the Nashorn JavaScript Engine). Still, this is -
   * at least for Java 14 - a good example for text blocks.
   * <li>in order to enable the ES6 support in Nashorn, use the
   * {@code -Dnashorn.args=--language=es6}.
   * </ul>
   * 
   * @throws javax.script.ScriptException when there was a problem running the
   *                                      script.
   * @throws NoSuchMethodException        when performing dynamic invocation
   * 
   */
  public void example04Inception() throws ScriptException, NoSuchMethodException {
    insertExampleSeparator();

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
    engine.eval("""
        function getSelectHoursForCityAndTemperature(city, temperatureAsCelsius) {
            let sqlGeneratedInJS = `select hours from wheather_data where
              city='${city}' and
              temperature > ${temperatureAsCelsius*1.8 + 32}`
            return sqlGeneratedInJS
        }
        """);
    Invocable invocable = (Invocable) engine;

    Object sqlFromJSTemplateLiterals = invocable.invokeFunction("getSelectHoursForCityAndTemperature", "New York", 33);
    System.out.println("Here is the select that would give us the hours when New York city was really hot:");
    System.out.println(sqlFromJSTemplateLiterals);
  }

  /**
   * The <b>text blocks</b> feature was in the preview mode since Java 13.<br>
   * <br>
   * Given the fact that the feature eliminates incidental white space and
   * leverages multilines, two concerns occurred:
   * <ol>
   * <li>What happens if a developer intentionally wants spaces at the beginning
   * and at the end of the line? For this \s was introduced as an escape sequence
   * to act "as a fence".
   * <li>It is a common practice to split long lines using the "\" character if
   * you use a linux shell. Probably inspired by this capability, a similar
   * functionality has been introduced for text blocks.
   * </ol>
   * <b>Attribution</b>:
   * <ul>
   * <li>artwork by Joan Stark and published at <a href=
   * "https://www.asciiart.eu/computers/smileys">https://www.asciiart.eu/computers/smileys</a>.
   * <li>oneliner joke from unknown author published at <a href=
   * "https://short-funny.com/one-liners.php">https://short-funny.com/one-liners.php</a>
   * </ul>
   * 
   */
  public void example05Java14NewEscapeSequences() {
    insertExampleSeparator();
    String aSmileyFace="""
                                            _.-'''''-._
                                          .'  _     _  '.
                                         /   (_)   (_)   \\
                                        |  ,           ,  |
                                        |  \\`.       .`/  |
                                         \\  '.`'""'"`.'  /
                                          '.  `'---'`  .'
                                            '-._____.-'        
                                           """;
    System.out.println("A smiley face, but - sadly - it is not position where I wanted because the incidental white space has been eliminated :-(: ");
    System.out.println(aSmileyFace);

    String centeredSmileyFace="""
\s                                              _.-'''''-._
\s                                            .'  _     _  '.
\s                                           /   (_)   (_)   \\
\s                                          |  ,           ,  |
\s                                          |  \\`.       .`/  |
\s                                           \\  '.`'""'"`.'  /
\s                                            '.  `'---'`  .'
\s                                              '-._____.-'        
                                          """;
    System.out.println("Same smiley face but centered where I really wanted:");
    System.out.println(centeredSmileyFace);
    
    
    insertExampleSeparator();
    
    String aOneLiner = """
        What are a shark's two most favorite words? \
                        - \
                  Man overboard! \
        """;
    System.out.println("A one-liner won't be a one-liner if across multiple lines :-):");
    System.out.println(aOneLiner);    
  }
}
