package compiler.semantics;

import compiler.ParseTree;

public class Program {

    Code code;

    public Program(ParseTree parseTree) {
        this.code = new Code(parseTree.getChildren().get(4));
    }

    public Code getCode() {
        return this.code;
    }
}
