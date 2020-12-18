package compiler.semantics;

import compiler.ParseTree;

/**
 * This class represents a program.
 */
public class Program {

    Code code;

    /**
     * Construct the program with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Program(ParseTree parseTree) {
        this.code = new Code(parseTree.getChildren().get(4));
    }

    /**
     * Return the code.
     *
     * @return The code
     */
    public Code getCode() {
        return this.code;
    }
}
