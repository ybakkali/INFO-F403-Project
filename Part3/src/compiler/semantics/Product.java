package compiler.semantics;

import compiler.LexicalUnit;
import compiler.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * THis class represents a product.
 */
public class Product {

    List<Atom> factors;
    List<LexicalUnit> operators;

    /**
     * Construct the product with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Product(ParseTree parseTree) {
        this.factors = new ArrayList<>();
        this.operators = new ArrayList<>();
        this.factors.add(new Atom(parseTree.getChildren().get(0)));

        parseTree = parseTree.getChildren().get(1);

        while (parseTree != null) {
            this.factors.add(new Atom(parseTree.getChildren().get(1)));
            this.operators.add(parseTree.getChildren().get(0).getLabel().getType());
            parseTree = parseTree.getChildren().get(2);
        }
    }

    /**
     * Return all the factors.
     *
     * @return The factors
     */
    public List<Atom> getFactors() {
        return factors;
    }

    /**
     * Return all the operators.
     *
     * @return The operators
     */
    public List<LexicalUnit> getOperators() {
        return operators;
    }
}
