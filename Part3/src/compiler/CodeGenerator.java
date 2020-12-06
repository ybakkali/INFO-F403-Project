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

    private List<String> variablesList;
    private List<StringBuilder> basicBlocksList;
    private Integer tempVariable;

    public CodeGenerator() {

    }

    public String generate(ParseTree parseTree) {
        this.variablesList = new ArrayList<>();
        this.basicBlocksList = new ArrayList<>();
        this.tempVariable = 1;

        handleProgram(parseTree);

        StringBuilder variables = new StringBuilder();
        for (String variable : this.variablesList) {
            variables.append("\t%").append(variable).append(" = alloca i32\n");
        }

        StringBuilder basicBlocks = new StringBuilder();
        for (StringBuilder basicBlock : basicBlocksList) {
            basicBlocks.append(basicBlock).append("\n");
        }

        return CodeGenerator.functions +
                "define i32 @main() {\n" +
                variables +
                "\tbr label %entry\n" +
                basicBlocks +
                "}\n";
    }

    private String getNextTempVariable() {
        this.tempVariable++;
        return "_" + this.tempVariable.toString();
    }

    private StringBuilder getNewBasicBlock(String labelName) {
        StringBuilder basicBlock = new StringBuilder();
        basicBlocksList.add(basicBlock);
        basicBlock.append("\t").append(labelName).append(":\n");
        return basicBlock;
    }

    private void handleProgram(ParseTree parseTree) {
        StringBuilder basicBlock = getNewBasicBlock("entry");
        StringBuilder lastBasicBlock = handleCode(parseTree.getChildren().get(4), basicBlock);
        lastBasicBlock.append("\t\tret i32 0\n");
    }

    private StringBuilder handleCode(ParseTree parseTree, StringBuilder currentBasicBlock) {
        if (parseTree == null) {
            return currentBasicBlock;
        } else if (parseTree.getChildren().get(0).getLabel().getType() == LexicalUnit.ENDLINE) {
            return handleCode(parseTree.getChildren().get(1), currentBasicBlock);
        } else {
            StringBuilder lastBasicBloc = handleInstruction(parseTree.getChildren().get(0), currentBasicBlock);
            return handleCode(parseTree.getChildren().get(2), lastBasicBloc);
        }
    }

    private StringBuilder handleInstruction(ParseTree parseTree, StringBuilder currentBasicBlock) {
        ParseTree child = parseTree.getChildren().get(0);
        switch ((String) child.getLabel().getValue()) {
            case "Assign": return handleAssign(child, currentBasicBlock);
            case "If": return handleIf(child, currentBasicBlock);
            case "While": return handleWhile(child, currentBasicBlock);
            case "Print": return handlePrint(child, currentBasicBlock);
            /*case "Read": return handleRead(child, currentBasicBlock);*/
            default: return handleRead(child, currentBasicBlock);
        }
    }

    private StringBuilder handleAssign(ParseTree parseTree, StringBuilder currentBasicBlock) {
        String variable = (String) parseTree.getChildren().get(0).getLabel().getValue();
        if (!this.variablesList.contains(variable)) {
            this.variablesList.add(variable);
        }
        String tempVariable = getNextTempVariable();
        handleExpr(parseTree.getChildren().get(2), currentBasicBlock, tempVariable);

        currentBasicBlock.append("\t\tstore i32 %").append(tempVariable).append(" , i32 * %").append(variable).append("\n");

        return currentBasicBlock;
    }

    private StringBuilder handleIf(ParseTree parseTree, StringBuilder currentBasicBlock) {
        String trueLabel = "true" + getNextTempVariable();
        StringBuilder trueBasicBlock = getNewBasicBlock(trueLabel);
        String falseLabel = "false" + getNextTempVariable();
        StringBuilder falseBasicBlock = getNewBasicBlock(falseLabel);
        String endLabel = "end" + getNextTempVariable();
        StringBuilder endBasicBlock = getNewBasicBlock(endLabel);

        handleCond(parseTree.getChildren().get(2), currentBasicBlock, trueLabel, falseLabel);

        StringBuilder lastBasicBlock;

        lastBasicBlock = handleCode(parseTree.getChildren().get(6), trueBasicBlock);
        lastBasicBlock.append("\t\tbr label %").append(endLabel).append("\n");

        lastBasicBlock = falseBasicBlock;
        parseTree = parseTree.getChildren().get(7);
        if (parseTree.getChildren().get(0).getLabel().getType() == LexicalUnit.ELSE) {
            lastBasicBlock = handleCode(parseTree.getChildren().get(2), falseBasicBlock);
        }
        lastBasicBlock.append("\t\tbr label %").append(endLabel).append("\n");

        return endBasicBlock; // TODO verify
    }

    private StringBuilder handleWhile(ParseTree parseTree, StringBuilder currentBasicBlock) {
        String condLabel = "cond" + getNextTempVariable();
        StringBuilder condBasicBlock = getNewBasicBlock(condLabel);
        String trueLabel = "true" + getNextTempVariable();
        StringBuilder trueBasicBlock = getNewBasicBlock(trueLabel);
        String endLabel = "end" + getNextTempVariable();
        StringBuilder endBasicBlock = getNewBasicBlock(endLabel);

        currentBasicBlock.append("\t\t br label %").append(condLabel).append("\n");

        handleCond(parseTree.getChildren().get(2), condBasicBlock, trueLabel, endLabel);

        StringBuilder lastBasicBlock;

        lastBasicBlock = handleCode(parseTree.getChildren().get(6), trueBasicBlock);
        lastBasicBlock.append("\t\tbr label %").append(condLabel).append("\n");

        return endBasicBlock; // TODO verify
    }

    private StringBuilder handlePrint(ParseTree parseTree, StringBuilder currentBasicBlock) {
        /* TODO exception */
        String variable = (String) parseTree.getChildren().get(2).getLabel().getValue();
        String tempVariable = getNextTempVariable();

        currentBasicBlock.append("\t\t%").append(tempVariable).append(" = load i32 , i32* %").append(variable).append("\n");
        currentBasicBlock.append("\t\tcall void @println(i32 %").append(tempVariable).append(")\n");

        return currentBasicBlock;
    }

    private StringBuilder handleRead(ParseTree parseTree, StringBuilder currentBasicBlock) {
        String variable = (String) parseTree.getChildren().get(2).getLabel().getValue();
        if (!this.variablesList.contains(variable)) {
            this.variablesList.add(variable);
        }

        String tempVariable = getNextTempVariable();
        currentBasicBlock.append("\t\t%").append(tempVariable).append(" = call i32 @readInt()\n")
                .append("\t\tstore i32 %").append(tempVariable).append(" , i32 * %").append(variable).append("\n");

        return currentBasicBlock;
    }

    private void handleCond(ParseTree parseTree, StringBuilder currentBasicBlock, String labelTrue, String labelFalse) {
        List<ParseTree> children = parseTree.getChildren();

        String variableA = getNextTempVariable();
        String variableB = getNextTempVariable();

        handleExpr(children.get(0), currentBasicBlock, variableA);
        handleExpr(children.get(2), currentBasicBlock, variableB);

        String operator = (children.get(1).getLabel().getType() == LexicalUnit.EQ)? "eq" : "sgt";
        String condVariable = getNextTempVariable();

        currentBasicBlock.append("\t\t%").append(condVariable).append(" = icmp ").append(operator).append(" i32 %").append(variableA).append(" , %").append(variableB).append("\n"); // .append(" i32 %a, %b\n");
        currentBasicBlock.append("\t\tbr i1 %").append(condVariable).append(" , label %").append(labelTrue).append(" , label %").append(labelFalse).append("\n");
    }

    private void handleExpr(ParseTree parseTree, StringBuilder currentBasicBlock, String variableToStoreIn) {
        if (parseTree.getChildren().get(1) == null) {
            handleProd(parseTree.getChildren().get(0), currentBasicBlock, variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleProd(parseTree.getChildren().get(0), currentBasicBlock, variableA);
            handleExprPrime(parseTree.getChildren().get(1), currentBasicBlock, variableB);
            String operator = (parseTree.getChildren().get(1).getChildren().get(0).getLabel().getType() == LexicalUnit.PLUS) ? "add" : "sub";
            currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = ").append(operator).append(" i32 %").append(variableA).append(" , %").append(variableB).append("\n");
        }
    }

    private void handleExprPrime(ParseTree parseTree, StringBuilder currentBasicBlock, String variableToStoreIn) {
        if (parseTree.getChildren().get(2) == null) {
            handleProd(parseTree.getChildren().get(1), currentBasicBlock, variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleProd(parseTree.getChildren().get(1), currentBasicBlock, variableA);
            handleExprPrime(parseTree.getChildren().get(2), currentBasicBlock, variableB);
            String operator = (parseTree.getChildren().get(2).getChildren().get(0).getLabel().getType() == LexicalUnit.PLUS) ? "add" : "sub";
            currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = ").append(operator).append(" i32 %").append(variableA).append(" , %").append(variableB).append("\n");
        }
    }

    private void handleProd(ParseTree parseTree, StringBuilder currentBasicBlock, String variableToStoreIn) {
        if (parseTree.getChildren().get(1) == null) {
            handleAtom(parseTree.getChildren().get(0), currentBasicBlock, variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleAtom(parseTree.getChildren().get(0), currentBasicBlock, variableA);
            handleProdPrime(parseTree.getChildren().get(1), currentBasicBlock, variableB);
            String operator = (parseTree.getChildren().get(1).getChildren().get(0).getLabel().getType() == LexicalUnit.TIMES) ? "mul" : "sdiv";
            currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = ").append(operator).append(" i32 %").append(variableA).append(" , %").append(variableB).append("\n");
        }
    }

    private void handleProdPrime(ParseTree parseTree, StringBuilder currentBasicBlock, String variableToStoreIn) {
        if (parseTree.getChildren().get(2) == null) {
            handleAtom(parseTree.getChildren().get(1), currentBasicBlock, variableToStoreIn);
        } else {
            String variableA = getNextTempVariable();
            String variableB = getNextTempVariable();
            handleAtom(parseTree.getChildren().get(1), currentBasicBlock, variableA);
            handleProdPrime(parseTree.getChildren().get(2), currentBasicBlock, variableB);
            String operator = (parseTree.getChildren().get(2).getChildren().get(0).getLabel().getType() == LexicalUnit.TIMES) ? "mul" : "sdiv";
            currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = ").append(operator).append(" i32 %").append(variableA).append(" , %").append(variableB).append("\n");
        }
    }

    private void handleAtom(ParseTree parseTree, StringBuilder currentBasicBlock, String variableToStoreIn) {
        Symbol label = parseTree.getChildren().get(0).getLabel();
        switch (label.getType()) {
            case MINUS: // TODO LOL?
                String variableA = getNextTempVariable();
                handleAtom(parseTree.getChildren().get(1), currentBasicBlock, variableA);
                currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = mul i32 %").append(variableA).append(" , -1\n");
                break;
            case NUMBER: // TODO LOL
                String variable = (String) label.getValue();
                currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = add i32 0 , ").append(variable).append("\n");
                break;
            case VARNAME:
                currentBasicBlock.append("\t\t%").append(variableToStoreIn).append(" = load i32 , i32 * %").append(label.getValue()).append("\n");
                break;
            case LPAREN:
                handleExpr(parseTree.getChildren().get(1), currentBasicBlock, variableToStoreIn);
                break;
        }
    }
}
