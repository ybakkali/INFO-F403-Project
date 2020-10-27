import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Compiler {
    public Compiler() {}
    public void compile(String filePath){
        try(FileReader fileReader = new FileReader(filePath)) {
            List<Symbol> tokens = lexicalAnalyse(fileReader);
            Map<String, Integer> variables = new TreeMap<>();

            for(Symbol token:tokens) {
                System.out.println(token.toString());
                if (token.getType() == LexicalUnit.VARNAME) {
                    if (!variables.containsKey(token.getValue().toString())) {
                        variables.put(token.getValue().toString(), token.getLine());
                    }
                }
            }

            System.out.println("\nVariables");
            for (Map.Entry<String, Integer> variable : variables.entrySet()) {
                System.out.println(variable.getKey() + "\t" + variable.getValue());
            }
        } catch (IOException | exceptions.SyntaxException | exceptions.LexicalException e) {
            e.printStackTrace();
        }
    }
    private List<Symbol> lexicalAnalyse(FileReader fileReader) throws exceptions.LexicalException, exceptions.SyntaxException, IOException {
        List<Symbol> tokens = new ArrayList<>();
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileReader);
        Symbol currentSymbol = lexicalAnalyzer.nextToken();
        while ((currentSymbol == null) || currentSymbol.getType() != LexicalUnit.EOS) {
            if (currentSymbol != null) {
                tokens.add(currentSymbol);
            }
            currentSymbol = lexicalAnalyzer.nextToken();
        }
        return tokens;
    }
}
