import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalyser {
    private final List<Symbol> tokens;
    private int index;
    private List<Integer> leftMostDerivation;
    private ParseTree parseTree;

    public SyntaxAnalyser(List<Symbol> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public void analyse() throws SyntaxException {
        this.index = 0;
        this.leftMostDerivation = new ArrayList<>();

        parseTree = program();
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
        leftMostDerivation.add(1);

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

        return new ParseTree(new Symbol("Program"), list);
    }

    private ParseTree code() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ENDLINE:
                leftMostDerivation.add(3);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

                list.add(code());
                break;

            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                leftMostDerivation.add(2);
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

        match(LexicalUnit.VARNAME);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

        match(LexicalUnit.ASSIGN);
        list.add(new ParseTree(getCurrentToken()));
        nextToken();

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
                nextToken();

                list.add(prod());

                list.add(exprPrime());

                break;

            case MINUS:
                leftMostDerivation.add(13);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

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
                nextToken();

                list.add(atom());

                list.add(prodPrime());

                break;

            case DIVIDE:
                leftMostDerivation.add(17);
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
                nextToken();

                list.add(atom());

                break;

            case NUMBER:
                leftMostDerivation.add(20);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;

            case VARNAME:
                leftMostDerivation.add(21);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;
            case LPAREN:
                leftMostDerivation.add(22);
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
        return new ParseTree(new Symbol("Atom"), list);
    }

    private ParseTree if_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(23);

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

        return new ParseTree(new Symbol("If"), list);
    }

    private ParseTree ifTail() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();

        switch (getCurrentTokenType()) {
            case ELSE:
                leftMostDerivation.add(24);
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
                leftMostDerivation.add(25);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();

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
                nextToken();
                break;

            case GT:
                leftMostDerivation.add(28);
                list.add(new ParseTree(getCurrentToken()));
                nextToken();
                break;

            default:
                throw new SyntaxException("Error Comp");
        }
        return new ParseTree(new Symbol("Comp"), list);
    }

    private ParseTree while_() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(29);

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

        return new ParseTree(new Symbol("While"), list);
    }

    private ParseTree print() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(30);

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

        return new ParseTree(new Symbol("Print"), list);
    }

    private ParseTree read() throws SyntaxException {
        List<ParseTree> list = new ArrayList<>();
        leftMostDerivation.add(31);

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

        return new ParseTree(new Symbol("Read"), list);
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
        }
        return null;
    }

    public String getRule(int ruleNumber) {
        switch (ruleNumber) {
            case 1:
                return "BEGINPROG [ProgName] [EndLine] <Code> ENDPROG";
            case 2:
                return "<Instruction> [EndLine] <Code>";
            case 3:
                return "[EndLine] <Code>";
            case 4:
                return "";
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
            case 14:
                return "";
            case 15:
                return "<Atom> <Prod'>";
            case 16:
                return "*<Atom> <Prod'>";
            case 17:
                return "/<Atom> <Prod'>";
            case 18:
                return "";
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
                return "WHILE (<Cond>) DO[EndLIne] <Code>ENDWHILE";
            case 30:
                return "PRINT([VarName])";
            case 31:
                return "READ([VarName])";
        }
        return null;
    }
}
