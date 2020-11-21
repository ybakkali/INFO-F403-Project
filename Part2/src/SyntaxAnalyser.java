import java.util.List;

public class SyntaxAnalyser {
    private final List<Symbol> tokens;
    private int index;
    private ParseTree parseTree;

    public SyntaxAnalyser(List<Symbol> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public ParseTree analyse() throws SyntaxException {
        program();
        this.parseTree = new ParseTree(this.tokens.get(this.index));
        return this.parseTree;
    }

    private void match(LexicalUnit expectedType) throws SyntaxException {
        Symbol currentToken = this.tokens.get(this.index);
        if (currentToken.getType() != expectedType) {
            throw new SyntaxException("Grammar error at line " + currentToken.getLine() + " column " + currentToken.getColumn() + '\n' + currentToken.toString());
        }
    }

    private LexicalUnit getCurrentTokenType() throws SyntaxException {
        if (this.index < this.tokens.size())
        return this.tokens.get(this.index).getType();
        else {
            throw new SyntaxException("Error not enough token");
        }
    }

    private void nextToken() {
        this.index++;
    }

    private void program() throws SyntaxException {
        System.out.println("Program");
        match(LexicalUnit.BEGINPROG);
        nextToken();
        match(LexicalUnit.PROGNAME);
        nextToken();
        match(LexicalUnit.ENDLINE);
        nextToken();
        code();
        match(LexicalUnit.ENDPROG);
    }

    private void code() throws SyntaxException {
        System.out.println("Code");
        switch (getCurrentTokenType()) {
            case ENDPROG:
            case ENDIF:
            case ELSE:
            case ENDWHILE:
                return;

            case ENDLINE:
                nextToken();
                code();
                return;

            case VARNAME:
            case IF:
            case WHILE:
            case PRINT:
            case READ:
                instruction();
                match(LexicalUnit.ENDLINE);
                nextToken();
                code();
                return;
            default:
                throw new SyntaxException("Error Code");
        }
    }

    private void instruction() throws SyntaxException {
        System.out.println("Instruction");
        switch (getCurrentTokenType()) {
            case VARNAME:
                assign(); return;
            case IF:
                if_(); return;
            case WHILE:
                while_(); return;
            case PRINT:
                print(); return;
            case READ:
                read(); return;
            default:
                throw new SyntaxException("Error Instruction");
        }
    }

    private void assign() throws SyntaxException {
        System.out.println("Assign");
        match(LexicalUnit.VARNAME);
        nextToken();
        match(LexicalUnit.ASSIGN);
        nextToken();
        expr();
    }

    private void expr() throws SyntaxException {
        System.out.println("Expr");
        prod();
        exprPrime();
    }

    private void exprPrime() throws SyntaxException {
        System.out.println("ExprPrime");
        switch (getCurrentTokenType()) {
            case PLUS:
                nextToken();
                prod();
                exprPrime();
                return;
            case MINUS:
                nextToken();
                prod();
                exprPrime();
                return;
            default: // TODO
                return;
        }
    }

    private void prod() throws SyntaxException {
        System.out.println("Prod");
        atom();
        prodPrime();
    }

    private void prodPrime() throws SyntaxException {
        System.out.println("ProdPrime");
        switch (getCurrentTokenType()) {
            case TIMES:
                nextToken();
                atom();
                prodPrime();
                return;
            case DIVIDE:
                nextToken();
                atom();
                prodPrime();
                return;
            default: // TODO
                return;
        }
    }

    private void atom() throws SyntaxException {
        System.out.println("Atom");
        switch (getCurrentTokenType()) {
            case MINUS:
                nextToken();
                atom();
                return;
            case NUMBER:
                nextToken();
                return;
            case VARNAME:
                nextToken();
                return;
            case LPAREN:
                nextToken();
                expr();
                match(LexicalUnit.RPAREN);
                nextToken();
                return;
            default: // TODO
        }
    }

    private void if_() throws SyntaxException {
        System.out.println("If");
        match(LexicalUnit.IF);
        nextToken();
        match(LexicalUnit.LPAREN);
        nextToken();
        cond();
        match(LexicalUnit.RPAREN);
        nextToken();
        match(LexicalUnit.THEN);
        nextToken();
        match(LexicalUnit.ENDLINE);
        nextToken();
        code();
        ifTail();
    }

    private void ifTail() throws SyntaxException {
        System.out.println("IfTail");
        switch (getCurrentTokenType()) {
            case ELSE:
                nextToken();
                match(LexicalUnit.ENDLINE);
                nextToken();
                code();
                match(LexicalUnit.ENDIF);
                nextToken();
                return;
            case ENDIF:
                nextToken();
                return;
            default:
                throw new SyntaxException("Error IfTail");
        }
    }

    private void cond() throws SyntaxException {
        System.out.println("Cond");
        expr();
        comp();
        expr();
    }

    private void comp() throws SyntaxException {
        System.out.println("Comp");
        switch (getCurrentTokenType()) {
            case EQ:
                nextToken();
                return;
            case GT:
                nextToken();
                return;
            default:
                throw new SyntaxException("Error Comp");
        }
    }

    private void while_() throws SyntaxException {
        System.out.println("While");
        match(LexicalUnit.WHILE);
        nextToken();
        match(LexicalUnit.LPAREN);
        nextToken();
        cond();
        match(LexicalUnit.RPAREN);
        nextToken();
        match(LexicalUnit.DO);
        nextToken();
        match(LexicalUnit.ENDLINE);
        nextToken();
        code();
        match(LexicalUnit.ENDWHILE);
        nextToken();
    }

    private void print() throws SyntaxException {
        System.out.println("Print");
        match(LexicalUnit.PRINT);
        nextToken();
        match(LexicalUnit.LPAREN);
        nextToken();
        match(LexicalUnit.VARNAME);
        nextToken();
        match(LexicalUnit.RPAREN);
        nextToken();
    }

    private void read() throws SyntaxException {
        System.out.println("Read");
        match(LexicalUnit.READ);
        nextToken();
        match(LexicalUnit.LPAREN);
        nextToken();
        match(LexicalUnit.VARNAME);
        nextToken();
        match(LexicalUnit.RPAREN);
        nextToken();
    }

}
