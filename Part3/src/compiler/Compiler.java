package compiler;

import compiler.exceptions.LexicalException;
import compiler.exceptions.SemanticException;
import compiler.exceptions.SyntaxException;
import compiler.semantics.Program;

import java.io.*;
import java.util.List;

/**
 * This class is a compiler that can compile Fortr-S code.
 */
public class Compiler {

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
    public void compile(String filePath, List<Option> options) {
        try(FileReader fileReader = new FileReader(filePath)) {
            Scanner scanner = new Scanner(fileReader);
            List<Symbol> tokens = scanner.scan();

            Parser parser = new Parser(tokens);
            parser.parse();
            Program program = new Program(parser.getParseTree());

            CodeGenerator codeGenerator = new CodeGenerator();
            String llvmCode = codeGenerator.generate(program);

            optionsHandler(options, llvmCode);
        } catch (IOException | SyntaxException | LexicalException | SemanticException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Handle the options.
     *
     * @param options The options
     * @param llvmCode The llvm code
     */
    private void optionsHandler(List<Option> options, String llvmCode) {

        boolean flag = false;
        for (Option option : options) {
            switch (option.getLabel()) {
                case "-o":
                    saveLLVMCode(llvmCode, option.getArgument());
                    flag = true;
                    break;
                case "-exec":
                    executeLLVMCode(llvmCode);
                    flag = true;
                    break;
            }
        }

        if (!flag) {
            System.out.println(llvmCode);
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

        try {
            File tempFile = File.createTempFile("llvmExecFile", ".ll");
            tempFile.deleteOnExit();
            saveLLVMCode(llvmCode, tempFile.getAbsolutePath());

            Process process = new ProcessBuilder().inheritIO().command("lli", tempFile.getAbsolutePath()).start();
            System.out.println("Start of the program");
            process.waitFor();
            System.out.println("End of the program");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
