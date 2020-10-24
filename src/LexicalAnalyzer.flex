%%// Options of the scanner

%class LexicalAnalyzer	//Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%function nextToken

%yylexthrow InvalidCommentNestingException

//Declare exclusive states
%xstate YYINITIAL, COMMENT_STATE

//Declare variables
%{
    private int nestedCommentCounter = 0;
    public class InvalidCommentNestingException extends Exception {
        public InvalidCommentNestingException(String message) {
            super(message);
        }
    }
%}

// Return value of the program
%eofval{
    if(nestedCommentCounter == 0) {
            return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
        }
        else {
            throw new InvalidCommentNestingException("Comment not closed");
        }
%eofval}

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = [A-Za-z]
Numeric        = [0-9]
AlphaNumeric   = [A-Z a-z 0-9]

Progname 	= {AlphaUpperCase}+[a-z0-9]{AlphaNumeric}*
Varname 	= {AlphaLowerCase}[a-z0-9]*
Number		= [1-9] {Numeric}* | 0

String      = "\"".*"\""

ShortComment    = "//".*

OpenLongComment = "/*"
CloseLongComment= "*/"

%%// Identification of tokens

<YYINITIAL> {
    // String
    {String}    {return new Symbol(LexicalUnit.STRING,yyline, yycolumn, yytext());}

    // Comments
    {ShortComment}  {}
    {OpenLongComment}   {nestedCommentCounter++; yybegin(COMMENT_STATE);}
    {CloseLongComment}  {throw new InvalidCommentNestingException("Closing without opening comment");}

    // Keywords
    "BEGINPROG"	{return new Symbol(LexicalUnit.BEGINPROG,yyline, yycolumn, yytext());}
    "ENDPROG"	{return new Symbol(LexicalUnit.ENDPROG,yyline, yycolumn, yytext());}

    // Progname
    {Progname}	{return new Symbol(LexicalUnit.PROGNAME,yyline, yycolumn, yytext());}

    // Endline
    "\n"		{return new Symbol(LexicalUnit.ENDLINE,yyline, yycolumn, "\\n");}

    // Stuff
    ","		{return new Symbol(LexicalUnit.COMMA,yyline, yycolumn, yytext());}
    {Varname}	{return new Symbol(LexicalUnit.VARNAME,yyline, yycolumn, yytext());}
    ":="		{return new Symbol(LexicalUnit.ASSIGN,yyline, yycolumn, yytext());}
    {Number}	{return new Symbol(LexicalUnit.NUMBER,yyline, yycolumn, yytext());}
    "("		{return new Symbol(LexicalUnit.LPAREN,yyline, yycolumn, yytext());}
    ")"		{return new Symbol(LexicalUnit.RPAREN,yyline, yycolumn, yytext());}

    // idk
    "-"		{return new Symbol(LexicalUnit.MINUS,yyline, yycolumn, yytext());}
    "+"		{return new Symbol(LexicalUnit.PLUS,yyline, yycolumn, yytext());}
    "*"		{return new Symbol(LexicalUnit.TIMES,yyline, yycolumn, yytext());}
    "/"		{return new Symbol(LexicalUnit.DIVIDE,yyline, yycolumn, yytext());}

    // Relational operators
    "="		{return new Symbol(LexicalUnit.EQ,yyline, yycolumn, yytext());}
    ">"		{return new Symbol(LexicalUnit.GT,yyline, yycolumn, yytext());}

    //Condition keyword
    "IF"		{return new Symbol(LexicalUnit.IF,yyline, yycolumn, yytext());}
    "THEN"		{return new Symbol(LexicalUnit.THEN,yyline, yycolumn, yytext());}
    "ELSE"		{return new Symbol(LexicalUnit.ELSE,yyline, yycolumn, yytext());}
    "ENDIF"	{return new Symbol(LexicalUnit.ENDIF,yyline, yycolumn, yytext());}

    //Loop keyword
    "WHILE"	{return new Symbol(LexicalUnit.WHILE,yyline, yycolumn, yytext());}
    "DO"		{return new Symbol(LexicalUnit.DO,yyline, yycolumn, yytext());}
    "ENDWHILE"	{return new Symbol(LexicalUnit.ENDWHILE,yyline, yycolumn, yytext());}

    //Functions
    "PRINT"	{return new Symbol(LexicalUnit.PRINT,yyline, yycolumn, yytext());}
    "READ"		{return new Symbol(LexicalUnit.READ,yyline, yycolumn, yytext());}

    // Ignore other characters
    .             {}
}

<COMMENT_STATE> {
    {OpenLongComment}   {nestedCommentCounter++;}
    {CloseLongComment}  {
                                nestedCommentCounter--;
                                if(nestedCommentCounter == 0) {
                                    yybegin(YYINITIAL);
                                }
                        }
    [^]       {}
}