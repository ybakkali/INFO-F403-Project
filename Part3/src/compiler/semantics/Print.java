package compiler.semantics;

import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.Symbol;
import compiler.exceptions.SemanticException;

/**
 * This class represent a print instruction.
 */
public class Print implements Instruction {

    final private Symbol variable;

    /**
     * Construct the print instruction with specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Print(ParseTree parseTree) {
        this.variable = parseTree.getChildren().get(2).getLabel();
    }

    /**
     * Dispatch to the right function of the code generator.
     *
     * @param codeGenerator The code generator
     * @throws SemanticException When a semantic problem is encountered
     */
    @Override
    public void dispatch(CodeGenerator codeGenerator) throws SemanticException {
        codeGenerator.handlePrint(this);
    }

    /**
     * Return the variable.
     *
     * @return The variable
     */
    public Symbol getVariable() {
        return variable;
    }
}
