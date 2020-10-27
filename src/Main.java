public class Main {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("1 argument required");
      return;
    }
    Compiler compiler = new Compiler();
    compiler.compile(args[0]);
  }
}
