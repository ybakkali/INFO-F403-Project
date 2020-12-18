package compiler.semantics;

import compiler.ParseTree;
import compiler.Symbol;

/**
 * This class represents an atom.
 */
public class Atom {

    Object value;
    int type;

    /**
     * Construct the atom with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
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

    /**
     * Return the type of the atom
     *
     * @return The type of the atom
     */
    public int getType() {
        return type;
    }

    /**
     * Return the atom.
     *
     * @return The atom
     */
    public Atom getAtom() {
        return (Atom) this.value;
    }

    /**
     * Return the arithmetic expression
     *
     * @return The arithmetic expression
     */
    public ArithmeticExpression getExpr() {
        return (ArithmeticExpression) this.value;
    }

    /**
     * Return the number.
     *
     * @return The number
     */
    public String getNumber() {
        return (String) this.value;
    }

    /**
     * Return the variable
     *
     * @return The the variable
     */
    public Symbol getVariable() {
        return (Symbol) this.value;
    }
}
