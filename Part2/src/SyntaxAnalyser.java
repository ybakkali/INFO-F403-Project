import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SyntaxAnalyser {
    private final List<Symbol> tokens;
    private int index;
    private List<Integer> leftMostDerivation;

    public SyntaxAnalyser(List<Symbol> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public ParseTree analyse() throws SyntaxException {
        this.index = 0;
        this.leftMostDerivation = new LinkedList<>();

        return program();
    }

    private void match(LexicalUnit expectedType) throws SyntaxException {
        Symbol currentToken = this.tokens.get(this.index);
        if (currentToken.getType() != expectedType) {
            throw new SyntaxException("Grammar error at line " + currentToken.getLine() + " column " + currentToken.getColumn());
        }
    }

    private Symbol getCurrentToken() throws SyntaxException {
        if (this.index < this.tokens.size())
            return this.tokens.get(this.index);
        else {
            throw new SyntaxException("Error not enough token");
        }
    }

    private LexicalUnit getCurrentTokenType() throws SyntaxException {
        return getCurrentToken().getType();
    }

    private void nextToken() {
        this.index++;
    }

    private ParseTree program() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Program"), list);

        match(LexicalUnit.BEGINPROG);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.PROGNAME);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.ENDLINE);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        list.add(code());

        match(LexicalUnit.ENDPROG);
        list.add(new ParseTree(getCurrentToken()));

        return parseTree;
    }

    private ParseTree code() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Code"), list);

        switch (getCurrentTokenType()) {
            case ENDLINE:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(code());
                break;

            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                list.add(instruction());

                match(LexicalUnit.ENDLINE);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(code());
                break;

            case ENDPROG: // epsilon
            case ENDIF:
            case ELSE:
            case ENDWHILE:
                return null;

            default:
                throw new SyntaxException("Error Code");
        }
        return parseTree;
    }

    private ParseTree instruction() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Instruction"), list);

        switch (getCurrentTokenType()) {
            case VARNAME:
                list.add(assign());
                break;

            case IF:
                list.add(if_());
                break;
            case WHILE:
                list.add(while_());
                break;

            case PRINT:
                list.add(print());
                break;

            case READ:
                list.add(read());
                break;

            default:
                throw new SyntaxException("Error Instruction");
        }
        return parseTree;
    }

    private ParseTree assign() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Assign"), list);

        match(LexicalUnit.VARNAME);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.ASSIGN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        list.add(expr());

        return parseTree;
    }

    private ParseTree expr() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Expr"), list);

        list.add(prod());

        list.add(exprPrime());

        return parseTree;
    }

    private ParseTree exprPrime() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Expr'"), list);
        switch (getCurrentTokenType()) {
            case PLUS:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(prod());

                list.add(exprPrime());

                break;

            case MINUS:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(prod());

                list.add(exprPrime());

                break;

            case ENDLINE: // Epsilon
            case RPAREN:
            case EQ:
            case GT:
                return null;

            default:
                throw new SyntaxException("Error Expr'");
        }
        return parseTree;
    }

    private ParseTree prod() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Prod"), list);

        list.add(atom());

        list.add(prodPrime());

        return parseTree;
    }

    private ParseTree prodPrime() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Prod'"), list);

        switch (getCurrentTokenType()) {
            case TIMES:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(atom());

                list.add(prodPrime());

                break;

            case DIVIDE:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(atom());

                list.add(prodPrime());

                break;
            case ENDLINE: // Epsilon 18
            case RPAREN:
            case MINUS:
            case PLUS:
            case EQ:
            case GT:
                return null;

            default:
                throw new SyntaxException("Error Prod'");
        }
        return parseTree;
    }

    private ParseTree atom() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Atom"), list);

        switch (getCurrentTokenType()) {
            case MINUS:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(atom());

                break;

            case NUMBER:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;

            case VARNAME:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;
            case LPAREN:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(expr());

                match(LexicalUnit.RPAREN);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                break;

            default:
                throw new SyntaxException("Error Atom");
        }
        return parseTree;
    }

    private ParseTree if_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("If"), list);

        match(LexicalUnit.IF);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.LPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        list.add(cond());

        match(LexicalUnit.RPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.THEN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.ENDLINE);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        list.add(code());

        list.add(ifTail());

        return parseTree;
    }

    private ParseTree ifTail() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("IfTail"), list);

        switch (getCurrentTokenType()) {
            case ELSE:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                match(LexicalUnit.ENDLINE);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(code());

                match(LexicalUnit.ENDIF);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                break;

            case ENDIF:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                break;

            default:
                throw new SyntaxException("Error IfTail");
        }
        return parseTree;
    }

    private ParseTree cond() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Cond"), list);

        list.add(expr());

        list.add(comp());

        list.add(expr());

        return parseTree;
    }

    private ParseTree comp() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Comp"), list);

        switch (getCurrentTokenType()) {
            case EQ:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;

            case GT:
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;

            default:
                throw new SyntaxException("Error Comp");
        }
        return parseTree;
    }

    private ParseTree while_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("While"), list);

        match(LexicalUnit.WHILE);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.LPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        list.add(cond());

        match(LexicalUnit.RPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.DO);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.ENDLINE);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        list.add(code());

        match(LexicalUnit.ENDWHILE);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        return parseTree;
    }

    private ParseTree print() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Print"), list);

        match(LexicalUnit.PRINT);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.LPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.VARNAME);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.RPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        return parseTree;
    }

    private ParseTree read() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        ParseTree parseTree = new ParseTree(new Symbol("Read"), list);

        match(LexicalUnit.READ);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.LPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.VARNAME);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.RPAREN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        return parseTree;
    }

}
