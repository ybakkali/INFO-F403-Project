%%// Options of the scanner

%class LexicalAnalyzer	//Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%function nextToken

%yylexthrow LexicalException, SyntaxException

//Declare exclusive states
%xstate YYINITIAL, COMMENT_STATE

//Declare variables
%{
    private int nestedCommentCounter = 0;
%}

// Return value of the program
%eofval{
    if(yystate() == COMMENT_STATE) {
            throw new SyntaxException("Comment not closed");
        } else {
            return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
        }
%eofval}

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Numeric        = [0-9]
AlphaNumeric   = [A-Za-z0-9]

Progname 	= {AlphaUpperCase}+[a-z0-9]{AlphaNumeric}*
Varname 	= {AlphaLowerCase}[a-z0-9]*
Number		= [1-9]{Numeric}*|0

ShortComment    = "//".*

OpenLongComment = "/*"
CloseLongComment= "*/"

Spacing = " "|\t

EndOfLine = \n|\r\n|\r

%%// Identification of tokens

<YYINITIAL> {

    {Progname}	    {return new Symbol(LexicalUnit.PROGNAME,yyline, yycolumn, yytext());}
    {Varname}	    {return new Symbol(LexicalUnit.VARNAME,yyline, yycolumn, yytext());}
    {Number}	    {return new Symbol(LexicalUnit.NUMBER,yyline, yycolumn, yytext());}
    {ShortComment}  {}

    // Keywords
    "BEGINPROG"	{return new Symbol(LexicalUnit.BEGINPROG,yyline, yycolumn, yytext());}
    "ENDPROG"	{return new Symbol(LexicalUnit.ENDPROG,yyline, yycolumn, yytext());}

    "IF"		{return new Symbol(LexicalUnit.IF,yyline, yycolumn, yytext());}
    "THEN"		{return new Symbol(LexicalUnit.THEN,yyline, yycolumn, yytext());}
    "ELSE"		{return new Symbol(LexicalUnit.ELSE,yyline, yycolumn, yytext());}
    "ENDIF"	    {return new Symbol(LexicalUnit.ENDIF,yyline, yycolumn, yytext());}

    "WHILE"	    {return new Symbol(LexicalUnit.WHILE,yyline, yycolumn, yytext());}
    "DO"		{return new Symbol(LexicalUnit.DO,yyline, yycolumn, yytext());}
    "ENDWHILE"	{return new Symbol(LexicalUnit.ENDWHILE,yyline, yycolumn, yytext());}

    "PRINT"	    {return new Symbol(LexicalUnit.PRINT,yyline, yycolumn, yytext());}
    "READ"		{return new Symbol(LexicalUnit.READ,yyline, yycolumn, yytext());}

    // Arithmetical operators
    "-"		{return new Symbol(LexicalUnit.MINUS,yyline, yycolumn, yytext());}
    "+"		{return new Symbol(LexicalUnit.PLUS,yyline, yycolumn, yytext());}
    "*"		{return new Symbol(LexicalUnit.TIMES,yyline, yycolumn, yytext());}
    "/"		{return new Symbol(LexicalUnit.DIVIDE,yyline, yycolumn, yytext());}

    // Utility operators
    ":="	{return new Symbol(LexicalUnit.ASSIGN,yyline, yycolumn, yytext());}
    "("		{return new Symbol(LexicalUnit.LPAREN,yyline, yycolumn, yytext());}
    ")"		{return new Symbol(LexicalUnit.RPAREN,yyline, yycolumn, yytext());}
    "="		{return new Symbol(LexicalUnit.EQ,yyline, yycolumn, yytext());}
    ">"		{return new Symbol(LexicalUnit.GT,yyline, yycolumn, yytext());}

    // End of line
    {EndOfLine} {return new Symbol(LexicalUnit.ENDLINE,yyline, yycolumn, "\\n");}

    // Comments
    {OpenLongComment}   {nestedCommentCounter++; yybegin(COMMENT_STATE);}
    {CloseLongComment}  {throw new SyntaxException("Closing without opening comment at line " + yyline + " column " + yycolumn);}

    // Ignore Spacing Characters
    {Spacing}   {}

    // Syntax Error
    [^]         {throw new LexicalException("Syntax error at line " + yyline + " column " + yycolumn);}
}

<COMMENT_STATE> {
    {OpenLongComment}   {nestedCommentCounter++;}
    {CloseLongComment}  {nestedCommentCounter--;
                        if(nestedCommentCounter == 0) {yybegin(YYINITIAL);}
                        }

    // Ignore comment content
    [^]       {}
}
