package compiler;

import compiler.exceptions.LexicalException;
import compiler.exceptions.SemanticException;
import compiler.exceptions.SyntaxException;
import compiler.semantics.Program;

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
            optionsHandler(options);
        } catch (IOException | SyntaxException | LexicalException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Handle the options.
     *
     * @param options The options
     */
    private void optionsHandler(List<Option> options) {

        CodeGenerator codeGenerator = new CodeGenerator();
        String llvmcode = null;
        try {
            llvmcode = codeGenerator.generate(new Program(parser.getParseTree()));
        } catch (SemanticException e) {
            e.printStackTrace();
        }

        boolean vFlag = false;
        for (Option option : options) {
            switch (option.getLabel()) {
                case "-o":
                    saveLLVMCode(llvmcode, option.getArgument());
                    vFlag = true;
                    break;
                case "-exec":
                    executeLLVMCode(llvmcode);
                    break;
            }
        }

        if (!vFlag) {
            System.out.println(llvmcode);
        }
    }

    /**
     * Create a file containing the generated LLVM code.
     *
     * @param llvmCode The LLVM code
     * @param outputFilename The output filename
     */
    private void saveLLVMCode(String llvmCode, String outputFilename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
            writer.write(llvmCode);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the generated LLVM code
     *
     * @param llvmCode The LLVM code
     */
    private void executeLLVMCode(String llvmCode) {

    }
}
