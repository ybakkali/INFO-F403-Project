/**
 * The Main class
 */
public class Main {

  /**
   * @param args The path of the file containing the code to compile
   */
  public static void main(String[] args) {
    String filePath = null, LatexFilename = null;
    boolean vOptionActivated = false, wtOptionActivated = false;
    if (args.length > 0) {
      int i = 0;
      while (i < args.length) {
        if (args[0].charAt(0) == '-') {
          System.err.println("The first argument cannot be an option");
          return;
        } else {
          filePath = args[0];
        }
        if (args[i].charAt(0) == '-') {
          if (args[i].equals("-v")) {
            vOptionActivated = true;
          } else if (args[i].equals("-wt")) {
            wtOptionActivated = true;
            if (i+1 < args.length && args[i+1].charAt(0) != '-') {
              LatexFilename = args[i+1];
              i++;
            } else {
              System.err.println("After -wt option an argument required");
              return;
            }
          } else {
            System.err.println("Unrecognised option detected");
            return;
          }
        }
        i++;
      }
    } else {
      System.err.println("1 argument required");
      return;
    }
    Compiler compiler = new Compiler();
    compiler.compile(filePath, vOptionActivated);
  }
}
