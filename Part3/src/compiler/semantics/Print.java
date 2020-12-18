package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.Symbol;
import compiler.exceptions.SemanticException;

public class Print implements Instruction {
    Symbol variable;

    public Print(ParseTree parseTree) {
        this.variable = parseTree.getChildren().get(2).getLabel();
    }

    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException {
        return codeGenerator.handlePrint(this, basicBlock);
    }

    public Symbol getVariable() {
        return variable;
    }
}
