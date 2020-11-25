package compiler;

import compiler.exceptions.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The compiler class that can compile Fortr-S code
 */
public class Compiler {
    /**
     * Create a new Compiler
     */

    private SyntaxAnalyser syntaxAnalyser;

    public Compiler() {}

    /**
     * Compiles the code of a specified file
     *
     * @param filePath The path of the file containing the code
     * @param options
     */
    public void compile(String filePath, List<Option> options){
        try(FileReader fileReader = new FileReader(filePath)) {
            List<Symbol> tokens = lexicalAnalyse(fileReader);
            syntaxAnalyse(tokens);
            optionsHandler(options);
            // printTokens(tokens);
            // printSymbolTable(tokens);
        } catch (IOException | SyntaxException | LexicalException e) {
            e.printStackTrace();
        }
    }

    private void optionsHandler(List<Option> options) {
        boolean vFlag = false;
        for (Option option : options) {
            switch (option.getLabel()) {
                case "-v":
                    printLeftMostDerivation(true);
                    vFlag = true;
                    break;
                case "-wt":
                    generateLatexFile(option.getArgument());
                    break;
            }
        }

        if (!vFlag) {
            printLeftMostDerivation(false);
        }
    }

    private void generateLatexFile(String outputFilename) {
        File file = new File(outputFilename);
        try {
            if (!file.createNewFile()) {
                throw new IOException();
            }

            try (FileWriter fileWriter = new FileWriter(outputFilename)) {
                fileWriter.write(this.syntaxAnalyser.getParseTree().toLaTeX());
            }
        } catch (IOException e) {
            //throw new IOException();
        }
    }

    /**
     * Analyses lexically the code specified
     *
     * @param fileReader The FileReader objet that gives the code of the file specified
     * @return The list of tokens that the code contains
     * @throws LexicalException When a lexical problem is encountered
     * @throws SyntaxException When a syntax problem is encountered
     * @throws IOException When another problem is encountered
     */
    public List<Symbol> lexicalAnalyse(FileReader fileReader) throws LexicalException, SyntaxException, IOException {
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

    public void syntaxAnalyse(List<Symbol> tokens) throws SyntaxException {
        this.syntaxAnalyser = new SyntaxAnalyser(tokens);
        syntaxAnalyser.analyse();
    }

    /**
     * Prints the specified list of tokens
     *
     * @param tokens The list of tokens to print
     */
    private void printTokens(List<Symbol> tokens) {
        for(Symbol token: tokens) {
            System.out.println(token.toString());
        }
    }

    /**
     * Prints the symbol table with all the variables contained in the tokens
     *
     * @param tokens The list of tokens containing the variables
     */
    private void printSymbolTable(List<Symbol> tokens) {
        Map<String, Integer> variables = new TreeMap<>();

        for(Symbol token: tokens) {
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
    }

    private void printLeftMostDerivation(boolean flag) {
        if (flag) {
            String s = "<Program>";
            System.out.println(s);
            for (Integer integer : this.syntaxAnalyser.getLeftMostDerivation()) {
                s = s.replaceFirst(this.syntaxAnalyser.getVariable(integer), this.syntaxAnalyser.getRule(integer));
                System.out.printf("%d -> %s%n", integer, s);
            }
        } else {
            StringJoiner joiner = new StringJoiner(" ");
            for (Integer integer : this.syntaxAnalyser.getLeftMostDerivation()) {
                String s = Objects.toString(integer);
                joiner.add(s);
            }
            System.out.println(joiner.toString() + ".");
        }
    }
}
