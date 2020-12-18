package compiler.semantics;

import compiler.LexicalUnit;
import compiler.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an arithmetic expression.
 */
public class ArithmeticExpression {

    List<Product> terms;
    List<LexicalUnit> operators;

    /**
     * Construct the arithmetic expression with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public ArithmeticExpression(ParseTree parseTree) {
        this.terms = new ArrayList<>();
        this.operators = new ArrayList<>();
        this.terms.add(new Product(parseTree.getChildren().get(0)));

        parseTree = parseTree.getChildren().get(1);

        while (parseTree != null) {
            this.terms.add(new Product(parseTree.getChildren().get(1)));
            this.operators.add(parseTree.getChildren().get(0).getLabel().getType());
            parseTree = parseTree.getChildren().get(2);
        }
    }

    /**
     * Return all the terms.
     *
     * @return The terms
     */
    public List<Product> getTerms() {
        return terms;
    }

    /**
     * Return the operators.
     *
     * @return The operators
     */
    public List<LexicalUnit> getOperators() {
        return operators;
    }
}
