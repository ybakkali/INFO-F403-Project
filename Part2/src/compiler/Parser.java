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
    private List<Integer> leftMostDerivation;
    private ParseTree parseTree;

    /**
     * Create a new parser.
     * @param tokens the list of tokens of the code
     */
    public Parser(List<Symbol> tokens) {
        this.tokens = new Stack<>();
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
        this.leftMostDerivation = new ArrayList<>();

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
            throw new SyntaxException("Grammar error at line " + currentToken.getLine() + " column " + currentToken.getColumn());
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

    private ParseTree program() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(1);

        list.add(start());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.BEGINPROG);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.PROGNAME);


        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDLINE);


        list.add(code());

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ENDPROG);

        list.add(end());

        return new ParseTree(new Symbol("Program"), list);
    }

    private ParseTree code() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ENDLINE:
                leftMostDerivation.add(3);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(code());
                break;

            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                leftMostDerivation.add(2);
                list.add(instruction());

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(code());
                break;

            case ENDPROG: // epsilon
            case ENDIF:
            case ELSE:
            case ENDWHILE:
                leftMostDerivation.add(4);
                return null;

            default:
                throw new SyntaxException("Error Code");
        }
        return new ParseTree(new Symbol("Code"), list);
    }

    private ParseTree instruction() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case VARNAME:
                leftMostDerivation.add(5);
                list.add(assign());
                break;

            case IF:
                leftMostDerivation.add(6);
                list.add(if_());
                break;
            case WHILE:
                leftMostDerivation.add(7);
                list.add(while_());
                break;

            case PRINT:
                leftMostDerivation.add(8);
                list.add(print());
                break;

            case READ:
                leftMostDerivation.add(9);
                list.add(read());
                break;

            default:
                throw new SyntaxException("Error Instruction");
        }
        return new ParseTree(new Symbol("Instruction"), list);
    }

    private ParseTree assign() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(10);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.VARNAME);

        list.add(new ParseTree(getCurrentToken()));
        match(LexicalUnit.ASSIGN);

        list.add(expr());

        return new ParseTree(new Symbol("Assign"), list);
    }

    private ParseTree expr() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(11);

        list.add(prod());

        list.add(exprPrime());

        return new ParseTree(new Symbol("Expr"), list);
    }

    private ParseTree exprPrime() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case PLUS:
                leftMostDerivation.add(12);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.PLUS);

                list.add(prod());

                list.add(exprPrime());

                break;

            case MINUS:
                leftMostDerivation.add(13);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.MINUS);

                list.add(prod());

                list.add(exprPrime());

                break;

            case ENDLINE: // Epsilon
            case RPAREN:
            case EQ:
            case GT:
                leftMostDerivation.add(14);
                return null;

            default:
                throw new SyntaxException("Error Expr'");
        }
        return new ParseTree(new Symbol("Expr'"), list);
    }

    private ParseTree prod() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(15);

        list.add(atom());

        list.add(prodPrime());

        return new ParseTree(new Symbol("Prod"), list);
    }

    private ParseTree prodPrime() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case TIMES:
                leftMostDerivation.add(16);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.TIMES);

                list.add(atom());

                list.add(prodPrime());

                break;

            case DIVIDE:
                leftMostDerivation.add(17);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.DIVIDE);

                list.add(atom());

                list.add(prodPrime());

                break;
            case ENDLINE: // Epsilon 18
            case RPAREN:
            case MINUS:
            case PLUS:
            case EQ:
            case GT:
                leftMostDerivation.add(18);
                return null;

            default:
                throw new SyntaxException("Error Prod'");
        }
        return new ParseTree(new Symbol("Prod'"), list);
    }

    private ParseTree atom() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case MINUS:
                leftMostDerivation.add(19);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.MINUS);

                list.add(atom());

                break;

            case NUMBER:
                leftMostDerivation.add(20);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.NUMBER);
                break;

            case VARNAME:
                leftMostDerivation.add(21);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.VARNAME);
                break;
            case LPAREN:
                leftMostDerivation.add(22);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.LPAREN);

                list.add(expr());

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.RPAREN);

                break;

            default:
                throw new SyntaxException("Error Atom");
        }
        return new ParseTree(new Symbol("Atom"), list);
    }

    private ParseTree if_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(23);

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

    private ParseTree ifTail() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ELSE:
                leftMostDerivation.add(24);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ELSE);

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(code());

                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDIF);

                break;

            case ENDIF:
                leftMostDerivation.add(25);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDIF);
                break;

            default:
                throw new SyntaxException("Error IfTail");
        }
        return new ParseTree(new Symbol("IfTail"), list);
    }

    private ParseTree cond() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(26);

        list.add(expr());

        list.add(comp());

        list.add(expr());

        return new ParseTree(new Symbol("Cond"), list);
    }

    private ParseTree comp() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case EQ:
                leftMostDerivation.add(27);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.EQ);
                break;

            case GT:
                leftMostDerivation.add(28);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.GT);
                break;

            default:
                throw new SyntaxException("Error Comp");
        }
        return new ParseTree(new Symbol("Comp"), list);
    }

    private ParseTree while_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(29);

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

    private ParseTree print() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(30);

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

    private ParseTree read() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(31);

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

    private ParseTree start() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ENDLINE:
                leftMostDerivation.add(32);
                list.add(new ParseTree(getCurrentToken()));
                match(LexicalUnit.ENDLINE);

                list.add(start());

                break;

            case BEGINPROG: // epsilon
                leftMostDerivation.add(33);
                return null;

            default:
                throw new SyntaxException("Error Start");
        }

        return new ParseTree(new Symbol("Start"), list);
    }

    private ParseTree end() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        if (this.tokens.isEmpty()) {
            leftMostDerivation.add(35);
            return null;
        }

        if (getCurrentTokenType() == LexicalUnit.ENDLINE) {
            leftMostDerivation.add(34);
            list.add(new ParseTree(getCurrentToken()));
            match(LexicalUnit.ENDLINE);

            list.add(end());
        } else {
            throw new SyntaxException("Error End");
        }

        return new ParseTree(new Symbol("End"), list);
    }

    public List<Integer> getLeftMostDerivation() {
        return leftMostDerivation;
    }

    public ParseTree getParseTree() {
        return parseTree;
    }

    public String getVariable(int variableNumber) {
        switch (variableNumber) {
            case 1:
                return "<Program>";
            case 2:
            case 3:
            case 4:
                return "<Code>";
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return "<Instruction>";
            case 10:
                return "<Assign>";
            case 11:
                return "<Expr>";
            case 12:
            case 13:
            case 14:
                return "<Expr'>";
            case 15:
                return "<Prod>";
            case 16:
            case 17:
            case 18:
                return "<Prod'>";
            case 19:
            case 20:
            case 21:
            case 22:
                return "<Atom>";
            case 23:
                return "<If>";
            case 24:
            case 25:
                return "<IfTail>";
            case 26:
                return "<Cond>";
            case 27:
            case 28:
                return "<Comp>";
            case 29:
                return "<While>";
            case 30:
                return "<Print>";
            case 31:
                return "<Read>";
            case 32:
            case 33:
                return "<Start>";
            case 34:
            case 35:
                return "<End>";
        }
        return null;
    }

    public String getRule(int ruleNumber) {
        switch (ruleNumber) {
            case 1:
                return "<Start> BEGINPROG [ProgName] [EndLine] <Code> ENDPROG <End>";
            case 2:
                return "<Instruction> [EndLine] <Code>";
            case 3:
                return "[EndLine] <Code>";
            case 5:
                return "<Assign>";
            case 6:
                return "<If>";
            case 7:
                return "<While>";
            case 8:
                return "<Print>";
            case 9:
                return "<Read>";
            case 10:
                return "[VarName]:=<Expr>";
            case 11:
                return "<Prod> <Expr'>";
            case 12:
                return "+<Prod> <Expr'>";
            case 13:
                return "-<Prod> <Expr'>";
            case 15:
                return "<Atom> <Prod'>";
            case 16:
                return "*<Atom> <Prod'>";
            case 17:
                return "/<Atom> <Prod'>";
            case 19:
                return "-<Atom>";
            case 20:
                return "[Number]";
            case 21:
                return "[VarName]";
            case 22:
                return "(<Expr>)";
            case 23:
                return "IF (<Cond>) THEN [EndLine] <Code> <IfTail>";
            case 24:
                return "ELSE [EndLine] <Code> ENDIF";
            case 25:
                return "ENDIF";
            case 26:
                return "<Expr> <Comp> <Expr>";
            case 27:
                return "=";
            case 28:
                return ">";
            case 29:
                return "WHILE (<Cond>) DO[EndLine] <Code>ENDWHILE";
            case 30:
                return "PRINT([VarName])";
            case 31:
                return "READ([VarName])";
            case 32:
                return "[EndLine] <Start>";
            case 34:
                return "[EndLine] <End>";
            case 4:
            case 14:
            case 18:
            case 33:
            case 35:
                return "";
        }
        return null;
    }
}
