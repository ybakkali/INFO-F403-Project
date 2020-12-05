package compiler;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private static final String functions =
            "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\n" +
            "\n" +
            "; Function Attrs: nounwind uwtable\n" +
            "define void @println(i32 %x) #0 {\n" +
            "  %1 = alloca i32, align 4\n" +
            "  store i32 %x, i32* %1, align 4\n" +
            "  %2 = load i32, i32* %1, align 4\n" +
            "  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)\n" +
            "  ret void\n" +
            "}\n" +
            "\n" +
            "declare i32 @printf(i8*, ...) #1\n" +
            "\n" +
            "declare i32 @getchar()\n" +
            "\n" +
            "define i32 @readInt() {\n" +
                    "   entry:                                 ; create variables\n" +
                    "      %res   = alloca i32\n" +
                    "      %digit = alloca i32\n" +
                    "      store i32 0, i32* %res\n" +
                    "      br label %read\n" +
                    "   read:\t                                  ; read a digit\n" +
                    "      %0 = call i32 @getchar()\n" +
                    "      %1 = sub i32 %0, 48\n" +
                    "      store i32 %1, i32* %digit\n" +
                    "      %2 = icmp ne i32 %0, 10               ; is the char entered '\\n'?\n" +
                    "      br i1 %2, label %check, label %exit\n" +
                    "   check:                                   ; is the char entered a number?\n" +
                    "      %3 = icmp sle i32 %1, 9\n" +
                    "      %4 = icmp sge i32 %1, 0\n" +
                    "      %5 = and i1 %3, %4\n" +
                    "      br i1 %5, label %save, label %exit\n" +
                    "   save:\t                                  ; res<-res*10+digit\n" +
                    "      %6 = load i32, i32* %res\n" +
                    "      %7 = load i32, i32* %digit\n" +
                    "      %8 = mul i32 %6, 10\n" +
                    "      %9 = add i32 %8, %7\n" +
                    "      store i32 %9, i32* %res\n" +
                    "      br label %read\n" +
                    "   exit:\t                                  ; return res\n" +
                    "      %10 = load i32, i32* %res\n" +
                    "      ret i32 %10\n" +
                    "}\n";

    private String code;
    private List<String> variablesList;
    private Integer tempVariable;

    public CodeGenerator() {

    }

    public String generate(ParseTree parseTree) {
        this.code = CodeGenerator.functions;
        this.variablesList = new ArrayList<>();
        this.tempVariable = 1;
        handleProgram(parseTree);

        /*this.code += "%x = alloca i32\n" +
                "store i32 90 , i32 * %x\n" +
                "%1 = load i32 , i32 * %x\n" +
                "call void @println(i32 %1)\n";
        this.code += "ret i32 0\n}";*/
        return this.code;
    }

    private String getNextTempVariable() {
        this.tempVariable++;
        return "_" + this.tempVariable.toString();
    }

    private void handleProgram(ParseTree parseTree) {
        this.code += "define i32 @main() {\n" +
                    "\tentry:\n";
        handleCode(parseTree.getChildren().get(4));
        this.code += "}\n";
    }

    private void handleCode(ParseTree parseTree) {
        if (parseTree != null) {
            List<ParseTree> children = parseTree.getChildren();
            if (children.get(0).getLabel().getValue() == "Instruction") {
                handleInstruction(children.get(0));
                handleCode(children.get(2));
            } else {
                handleCode(children.get(1));
            }
        }
    }

    private void handleInstruction(ParseTree parseTree) {
        ParseTree child = parseTree.getChildren().get(0);
        switch ((String) child.getLabel().getValue()) {
            case "Assign": handleAssign(child); break;
            case "If": handleIf(child); break;
            case "While": handleWhile(child); break;
            case "Print": handlePrint(child); break;
            case "Read": handleRead(child); break;
        }
    }

    private void handleAssign(ParseTree parseTree) {
        String variable = (String) parseTree.getChildren().get(0).getLabel().getValue();
        if (!this.variablesList.contains(variable)) {
            this.code += "\t\t%" + variable + " = alloca i32\n";
            this.variablesList.add(variable);
        }

        handleExpr(parseTree.getChildren().get(2), variable);
    }

    private void handleIf(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        String labelTrue = "True";
        String labelFalse = "False";
        String labelEnd = "End";
        handleCond(children.get(2), labelTrue, labelFalse);

        this.code += "\t" + labelTrue + ":\n";
        handleCode(children.get(6));
        this.code += "\t\tbr label %" + labelEnd + "\n";

        this.code += "\t" + labelFalse + ":\n";

        parseTree = children.get(7);
        if (parseTree.getChildren().get(0).getLabel().getType() == LexicalUnit.ELSE) {
            handleCode(parseTree.getChildren().get(2));
        }
        this.code += "\t" + labelEnd + ":\n";
    }

    private void handleWhile(ParseTree parseTree) {
        List<ParseTree> children = parseTree.getChildren();
        String labelStart = "Start";
        String labelTrue = "True";
        String labelEnd = "End";

        this.code += "\t" + labelStart + ":\n";

        handleCond(children.get(2), labelStart, labelEnd);
        this.code += "\t" + labelTrue + ":\n";

        handleCode(children.get(6));

        this.code += "\t\tbr label %" + labelStart + "\n";
        this.code += "\t" + labelEnd + ":\n";
    }

    private void handlePrint(ParseTree parseTree) {
        String variable = (String) parseTree.getChildren().get(2).getLabel().getValue();
        this.code += "\t\tcall void @println(i32 %" + variable + ")\n";
    }

    private void handleRead(ParseTree parseTree) {
        String variable = (String) parseTree.getChildren().get(2).getLabel().getValue();
        if (!this.variablesList.contains(variable)) {
            this.code += "\t\t%" + variable + " = alloca i32\n";
            this.variablesList.add(variable);
        }
        String tempVariable = getNextTempVariable();
        this.code += "\t\t%" + tempVariable + " = call i32 @readInt()\n" +
                    "\t\tstore i32 %" + tempVariable + " , i32 * %" + variable + "\n";
    }

    private void handleCond(ParseTree parseTree, String labelTrue, String labelFalse) {
        List<ParseTree> children = parseTree.getChildren();

        String variableA = getNextTempVariable();
        String variableB = getNextTempVariable();

        handleExpr(children.get(0), variableA);
        handleExpr(children.get(2), variableB);

        String operator = (children.get(1).getLabel().getType() == LexicalUnit.EQ)? "eq" : "sgt";

        this.code += "\t\t%cond = icmp " + operator + " i32 %a, %b\n";
        this.code += "\t\tbr i1 %cond , label %" + labelTrue + " , label %" + labelFalse + "\n";
    }

    private void handleExpr(ParseTree parseTree, String variableToStoreIn) {
        if (parseTree.getChildren().get(1) == null) {
            handleProd(parseTree.getChildren().get(0), variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleProd(parseTree.getChildren().get(0), variableA);
            handleExprPrime(parseTree.getChildren().get(1), variableB);
            String operator = (parseTree.getChildren().get(1).getChildren().get(0).getLabel().getType() == LexicalUnit.PLUS) ? "add" : "sub";
            this.code += "\t\t%" + variableToStoreIn + " = " + operator + " " + variableA + "," + variableB + "\n" ;
        }
    }

    private void handleExprPrime(ParseTree parseTree, String variableToStoreIn) {
        if (parseTree.getChildren().get(2) == null) {
            handleProd(parseTree.getChildren().get(1), variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleProd(parseTree.getChildren().get(1), variableA);
            handleExprPrime(parseTree.getChildren().get(2), variableB);
            String operator = (parseTree.getChildren().get(2).getChildren().get(0).getLabel().getType() == LexicalUnit.PLUS) ? "add" : "sub";
            this.code += "\t\t%" + variableToStoreIn + " = " + operator + " " + variableA + "," + variableB + "\n" ;
        }
    }

    private void handleProd(ParseTree parseTree, String variableToStoreIn) {
        if (parseTree.getChildren().get(1) == null) {
            handleAtom(parseTree.getChildren().get(0), variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleAtom(parseTree.getChildren().get(0), variableA);
            handleProdPrime(parseTree.getChildren().get(1), variableB);
            String operator = (parseTree.getChildren().get(1).getChildren().get(0).getLabel().getType() == LexicalUnit.TIMES) ? "mul" : "sdiv";
            this.code += "\t\t%" + variableToStoreIn + " = " + operator + " " + variableA + "," + variableB + "\n" ;
        }
    }

    private void handleProdPrime(ParseTree parseTree, String variableToStoreIn) {
        if (parseTree.getChildren().get(2) == null) {
            handleAtom(parseTree.getChildren().get(1), variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleAtom(parseTree.getChildren().get(1), variableA);
            handleProdPrime(parseTree.getChildren().get(2), variableB);
            String operator = (parseTree.getChildren().get(2).getChildren().get(0).getLabel().getType() == LexicalUnit.TIMES) ? "mul" : "sdiv";
            this.code += "\t\t%" + variableToStoreIn + " = " + operator + " " + variableA + "," + variableB + "\n" ;
        }
    }

    private void handleAtom(ParseTree parseTree, String variableToStoreIn) {
       Symbol label = parseTree.getChildren().get(0).getLabel();
        switch (label.getType()) {
            case MINUS:
                handleAtom(parseTree.getChildren().get(1), "a");
                break;
            case NUMBER:
                // label.getValue()
                break;
            case VARNAME: // %1 = load i32 , i32 * % a
                this.code += "\t\t%" + variableToStoreIn + " = load i32 , i32 * %" + label.getValue() + "\n";
                break;
            case LPAREN:
                handleExpr(parseTree.getChildren().get(1), "a");
                break;
        }
    }
}
