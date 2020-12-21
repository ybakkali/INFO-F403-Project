package compiler;

import compiler.exceptions.SyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class is a parser that can parse Fortr-S code.
 */
public class Parser {
    private final Stack<Symbol> tokens;
    private ParseTree parseTree;

    /**
     * Create a new parser.
     * @param tokens the list of tokens of the code
     */
    public Parser(List<Symbol> tokens) {
        this.tokens = new Stack<>();
        this.tokens.push(new Symbol(LexicalUnit.EOS));
        for (int i = tokens.size() - 1; i >= 0; i--) {
            this.tokens.push(tokens.get(i));
        }
    }

    /**
     * Run the parsing process.
     *
     * @throws SyntaxException When a syntax problem is encountered
     */
    public void parse() throws SyntaxException {
        this.parseTree = program();
    }

    /**
     * Pop the head of the stack and verify that
     * its type is the same as the specified one.
     *
     * @param expectedType The expected type
     * @throws SyntaxException When a syntax problem is encountered
     */
    private void match(LexicalUnit expectedType) throws SyntaxException {
        Symbol currentToken = this.tokens.pop();
        if (currentToken.getType() != expectedType) {
            throw new SyntaxException("Syntax error at line " +
                    currentToken.getLine() + " column " + currentToken.getColumn() + " : " +
                    "\n\tExpected token : " + expectedType.toString() +
                    "\n\tGiven token : " + currentToken.getType().toString());
        }
    }

    /**
     * Return the head of the stack.
     *
     * @return The head of the stack
     * @throws SyntaxException When a syntax problem is encountered
     */
    private Symbol getCurrentToken() throws SyntaxException {
        if (!this.tokens.isEmpty())
            return this.tokens.peek();
        else {
            throw new SyntaxException("Error not enough token");
        }
    }

    /**
     * Return the type of the head of the stack.
     *
     * @return The type of the head of the stack
     * @throws SyntaxException When a syntax problem is encountered
     */
    private LexicalUnit getCurrentTokenType() throws SyntaxException {
        return getCurrentToken().getType();
    }

    /**
     * Throws a SyntaxException with a message
     * concerning the token on the top of the stack.
     *
     * @throws SyntaxException The SyntaxException with a message
     */
    private void throwSyntaxError() throws SyntaxException {
        Symbol currentToken = getCurrentToken();
        throw new SyntaxException("Syntax error at line " +
                currentToken.getLine() + " column " + currentToken.getColumn() + " : " +
                "\n\tUnexpected token : " + currentToken.getType().toString());
    }

    /**
     * The program variable producing rules
     *
     * @return the program variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree program() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(multiEndLines());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.BEGINPROG);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.PROGNAME);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDLINE);

        list.add(code());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDPROG);

        list.add(multiEndLines());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.EOS);

        return new ParseTree(new Symbol("Program"), list);
    }

    /**
     * The code variable producing rules
     *
     * @return the code variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree code() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ENDLINE:

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(code());
                break;

            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                list.add(instruction());

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(code());
                break;

            case ENDPROG: // epsilon
            case ENDIF:
            case ELSE:
            case ENDWHILE:
                return null;

            default:
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("Code"), list);
    }

    /**
     * The instruction variable producing rules
     *
     * @return the instruction variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree instruction() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

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
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("Instruction"), list);
    }

    /**
     * The assign variable producing rules
     *
     * @return the assign variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree assign() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.VARNAME);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ASSIGN);

        list.add(expr());

        return new ParseTree(new Symbol("Assign"), list);
    }

    /**
     * The expr variable producing rules
     *
     * @return the expr variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree expr() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(prod());

        list.add(exprPrime());

        return new ParseTree(new Symbol("Expr"), list);
    }

    /**
     * The exprPrime variable producing rules
     *
     * @return the exprPrime variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree exprPrime() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case PLUS:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.PLUS);

                list.add(prod());

                list.add(exprPrime());

                break;

            case MINUS:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.MINUS);

                list.add(prod());

                list.add(exprPrime());

                break;

            case ENDLINE: // Epsilon
            case RPAREN:
            case EQ:
            case NEQ:
            case GT:
            case GTE:
            case LT:
            case LTE:
                return null;

            default:
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("Expr'"), list);
    }

    /**
     * The prod variable producing rules
     *
     * @return the prod variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree prod() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(atom());

        list.add(prodPrime());

        return new ParseTree(new Symbol("Prod"), list);
    }

    /**
     * The prodPrime variable producing rules
     *
     * @return the prodPrime variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree prodPrime() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case TIMES:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.TIMES);

                list.add(atom());

                list.add(prodPrime());

                break;

            case DIVIDE:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.DIVIDE);

                list.add(atom());

                list.add(prodPrime());

                break;
            case ENDLINE: // Epsilon
            case RPAREN:
            case MINUS:
            case PLUS:
            case EQ:
            case NEQ:
            case GT:
            case GTE:
            case LT:
            case LTE:
                return null;

            default:
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("Prod'"), list);
    }

    /**
     * The atom variable producing rules
     *
     * @return the atom variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree atom() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case MINUS:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.MINUS);

                list.add(atom());

                break;

            case NUMBER:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.NUMBER);
                break;

            case VARNAME:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.VARNAME);
                break;
            case LPAREN:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.LPAREN);

                list.add(expr());

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.RPAREN);

                break;

            default:
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("Atom"), list);
    }

    /**
     * The if variable producing rules
     *
     * @return the if variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree if_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.IF);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.LPAREN);

        list.add(cond());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.RPAREN);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.THEN);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDLINE);

        list.add(code());

        list.add(ifTail());

        return new ParseTree(new Symbol("If"), list);
    }

    /**
     * The ifTail variable producing rules
     *
     * @return the ifTail variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree ifTail() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ELSE:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ELSE);

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(code());

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDIF);

                break;

            case ENDIF:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDIF);
                break;

            default:
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("IfTail"), list);
    }

    /**
     * The cond variable producing rules
     *
     * @return the cond variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree cond() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(expr());

        list.add(comp());

        list.add(expr());

        return new ParseTree(new Symbol("Cond"), list);
    }

    /**
     * The comp variable producing rules
     *
     * @return the comp variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree comp() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case EQ:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.EQ);
                break;

            case NEQ:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.NEQ);
                break;

            case GT:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.GT);
                break;

            case GTE:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.GTE);
                break;

            case LT:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.LT);
                break;

            case LTE:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.LTE);
                break;

            default:
                throwSyntaxError();
        }
        return new ParseTree(new Symbol("Comp"), list);
    }

    /**
     * The while variable producing rules
     *
     * @return the while variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree while_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.WHILE);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.LPAREN);

        list.add(cond());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.RPAREN);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.DO);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDLINE);

        list.add(code());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDWHILE);

        return new ParseTree(new Symbol("While"), list);
    }

    /**
     * The print variable producing rules
     *
     * @return the print variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree print() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.PRINT);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.LPAREN);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.VARNAME);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.RPAREN);

        return new ParseTree(new Symbol("Print"), list);
    }

    /**
     * The read variable producing rules
     *
     * @return the read variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree read() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.READ);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.LPAREN);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.VARNAME);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.RPAREN);

        return new ParseTree(new Symbol("Read"), list);
    }

    /**
     * The multiEndLines variable producing rules
     *
     * @return the multiEndLines variable parse tree
     * @throws SyntaxException the syntax exception
     */
    private ParseTree multiEndLines() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ENDLINE:
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(multiEndLines());

                break;

            case BEGINPROG: // epsilon
            case EOS:
                return null;

            default:
                throwSyntaxError();
        }

        return new ParseTree(new Symbol("MultiEndLines"), list);
    }

    /**
     * Getter for the syntax analyser parse tree
     *
     * @return syntax analyser parse tree
     */
    public ParseTree getParseTree() {
        return parseTree;
    }

}
