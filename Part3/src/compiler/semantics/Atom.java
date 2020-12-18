package compiler.semantics;

import compiler.ParseTree;
import compiler.Symbol;

public class Atom {

    Object value;

    int type;

    public Atom(ParseTree parseTree) {
        ParseTree child = parseTree.getChildren().get(0);
        switch (child.getLabel().getType()) {
            case MINUS:
                this.value = new Atom(parseTree.getChildren().get(1));
                this.type = 1;
                break;

            case NUMBER:
                this.value = child.getLabel().getValue();
                this.type = 2;
                break;

            case VARNAME:
                this.value = child.getLabel();
                this.type = 3;
                break;

            case LPAREN:
                this.value = new ArithmeticExpression(parseTree.getChildren().get(1));
                this.type = 4;
                break;
        }
    }

    public int getType() {
        return type;
    }

    public Atom getAtom() {
        return (Atom) this.value;
    }

    public ArithmeticExpression getExpr() {
        return (ArithmeticExpression) this.value;
    }

    public String getNumber() {
        return (String) this.value;
    }

    public Symbol getVariable() {
        return (Symbol) this.value;
    }
}
