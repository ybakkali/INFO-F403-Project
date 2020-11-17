/**
 * The Main class
 */
public class Main {

  /**
   * @param args The path of the file containing the code to compile
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("1 argument required");
      return;
    }
    Compiler compiler = new Compiler();
    compiler.compile(args[0]);
  }
}
