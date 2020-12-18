package compiler.semantics;

import compiler.LexicalUnit;
import compiler.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class Product {

    List<Atom> factors;
    List<LexicalUnit> operators;

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

    public List<Atom> getFactors() {
        return factors;
    }

    public List<LexicalUnit> getOperators() {
        return operators;
    }
}
