import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main{
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("1 argument required");
      return;
    }
    File file = new File(args[0]);
    try {
      FileReader reader = new FileReader(file);
      Lexer analyser = new Lexer(reader);
      Symbol currentSymbol;
      List<Variable> variables = new ArrayList<>();

      do {
        currentSymbol = analyser.nextToken();
        if (currentSymbol != null && currentSymbol.getType() != LexicalUnit.EOS) {
          System.out.println(currentSymbol.toString());
          if (currentSymbol.getType() == LexicalUnit.VARNAME) {
            if (!variableAlreadyExist(variables,currentSymbol.getValue().toString())) {
              variables.add(new Variable(currentSymbol.getValue().toString(), currentSymbol.getLine()));
            }
          }
        }
      } while (currentSymbol == null || currentSymbol.getType() != LexicalUnit.EOS);

      System.out.println("\nVariables");
      Collections.sort(variables);
      for (Variable variable : variables) {
        System.out.println(variable.getName() + "\t" + variable.getLine());
      }

    } catch (IOException e) {
      e.printStackTrace();
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
    public int compareTo(Variable variable) {
      return this.getName().compareTo(variable.getName());
    }
  }

  private static boolean variableAlreadyExist(List<Variable> variables, String name) {
    for (Variable variable:variables) {
      if (variable.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
};
