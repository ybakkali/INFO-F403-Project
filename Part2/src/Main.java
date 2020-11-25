import compiler.CommandLineParser;
import compiler.Compiler;
import compiler.exceptions.CommandLineException;

/**
 * The Main class
 */
public class Main {

  /**
   * @param args The path of the file containing the code to compile
   */
  public static void main(String[] args) {
    CommandLineParser parser;

    try {
      parser = new CommandLineParser(args);
    } catch (CommandLineException e) {
      e.printStackTrace();
      return;
    }
    Compiler compiler = new Compiler();
    compiler.compile(parser.getFilePath(), parser.getOptions());
  }
}
