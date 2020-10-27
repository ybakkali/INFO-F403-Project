import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("1 argument required");
      return;
    }
    try(FileReader fileReader = new FileReader(args[0])) {
      LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileReader);
      Symbol currentSymbol = lexicalAnalyzer.nextToken();
      List<Variable> variables = new ArrayList<>();

      while ((currentSymbol == null) || currentSymbol.getType() != LexicalUnit.EOS) {
        if (currentSymbol != null) {
          System.out.println(currentSymbol.toString());
          if (currentSymbol.getType() == LexicalUnit.VARNAME) {
            if (!variables.contains(new Variable(currentSymbol.getValue().toString(),-1))) {
              variables.add(new Variable(currentSymbol.getValue().toString(), currentSymbol.getLine()));
            }
          }
        }
        currentSymbol = lexicalAnalyzer.nextToken();
      }
      System.out.println("\nVariables");
      variables.sort(Comparator.naturalOrder());
      for (Variable variable : variables) {
        System.out.println(variable.getName() + "\t" + variable.getLine());
      }
    } catch (IOException | LexicalAnalyzer.InvalidCommentException | LexicalAnalyzer.SyntaxException e) {
      System.err.println(e);
    }
  }

  private static class Variable implements Comparable<Variable> {
    private final String name;
    private final int line;
    public Variable(String name, int line) {
      this.name = name;
      this.line = line;
    }

    public String getName() {
      return this.name;
    }

    public int getLine() {
      return this.line;
    }

    @Override
    public boolean equals(Object obj) {
      return this.getName().equals(((Variable) obj).getName());
    }

    @Override
    public int compareTo(Variable variable) {
      return this.getName().compareTo(variable.getName());
    }
  }
}
