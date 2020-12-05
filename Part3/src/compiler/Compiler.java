package compiler;

import compiler.exceptions.LexicalException;
import compiler.exceptions.SyntaxException;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * This class is a compiler that can compile Fortr-S code.
 */
public class Compiler {

    private Parser parser;

    /**
     * Create a new compiler.
     */
    public Compiler() {}

    /**
     * Compile the code of a specified file.
     *
     * @param filePath The path of the file containing the code
     * @param options The options
     */
    public void compile(String filePath, List<Option> options){
        try(FileReader fileReader = new FileReader(filePath)) {
            Scanner scanner = new Scanner(fileReader);
            List<Symbol> tokens = scanner.scan();
            this.parser = new Parser(tokens);
            parser.parse();
            // optionsHandler(options);
            CodeGenerator codeGenerator = new CodeGenerator();
            debug(codeGenerator.generate(parser.getParseTree()));
        } catch (IOException | SyntaxException | LexicalException e) {
            System.err.println(e.getMessage());
        }
    }

    private void debug(String code) {
        try {
        BufferedWriter writer = new BufferedWriter(new FileWriter("debug.ll"));
        writer.write(code);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the options.
     *
     * @param options The options
     */
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

    /**
     * Generate a latex file containing the parse tree.
     *
     * @param outputFilename The output filename
     */
    private void generateLatexFile(String outputFilename) {
        try (FileWriter fileWriter = new FileWriter(outputFilename)) {
            fileWriter.write(this.parser.getParseTree().toLaTeX());
        } catch (IOException ignored) {
            System.err.println("The file cannot be created or written on !");
        }
    }

    /**
     * Print the left most derivation.
     *
     * @param flag Indicate the verbose
     */
    private void printLeftMostDerivation(boolean flag) {
        if (flag) {
            String s = "<Program>";
            System.out.println(s);
            for (Integer integer : this.parser.getLeftMostDerivation()) {
                s = s.replaceFirst(this.parser.getVariable(integer), this.parser.getRule(integer));
                System.out.printf("%d -> %s%n", integer, s);
            }
        } else {
            StringJoiner joiner = new StringJoiner(" ");
            for (Integer integer : this.parser.getLeftMostDerivation()) {
                String s = Objects.toString(integer);
                joiner.add(s);
            }
            System.out.println(joiner.toString());
        }
    }
}
