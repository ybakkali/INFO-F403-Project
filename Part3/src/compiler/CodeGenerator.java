package compiler;

import compiler.exceptions.SemanticException;
import compiler.semantics.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a code generator that can generate code from a Fortr-S abstract syntax tree.
 */
public class CodeGenerator {

    private static final String functions = // It contains the println and read functions
            "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\n" +
            "\n" +
            "; Function Attrs: nounwind uwtable\n" +
            "define void @println(i32 %x) #0 {\n" +
            "\t%1 = alloca i32, align 4\n" +
            "\tstore i32 %x, i32* %1, align 4\n" +
            "\t%2 = load i32, i32* %1, align 4\n" +
            "\t%3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)\n" +
            "\tret void\n" +
            "}\n" +
            "\n" +
            "declare i32 @printf(i8*, ...) #1\n" +
            "\n" +
            "declare i32 @getchar()\n" +
            "\n" +
            "define i32 @readInt() {\n" +
            "\tentry:\t; create variables\n" +
            "\t\t%res   = alloca i32\n" +
            "\t\t%digit = alloca i32\n" +
            "\t\tstore i32 0, i32* %res\n" +
            "\t\tbr label %read\n" +
            "\tread:\t; read a digit\n" +
            "\t\t%0 = call i32 @getchar()\n" +
            "\t\t%1 = sub i32 %0, 48\n" +
            "\t\tstore i32 %1, i32* %digit\n" +
            "\t\t%2 = icmp ne i32 %0, 10\t; is the char entered '\\n'?\n" +
            "\t\tbr i1 %2, label %check, label %exit\n" +
            "\tcheck:\t; is the char entered a number?\n" +
            "\t\t%3 = icmp sle i32 %1, 9\n" +
            "\t\t%4 = icmp sge i32 %1, 0\n" +
            "\t\t%5 = and i1 %3, %4\n" +
            "\t\tbr i1 %5, label %save, label %exit\n" +
            "\tsave:\t; res<-res*10+digit\n" +
            "\t\t%6 = load i32, i32* %res\n" +
            "\t\t%7 = load i32, i32* %digit\n" +
            "\t\t%8 = mul i32 %6, 10\n" +
            "\t\t%9 = add i32 %8, %7\n" +
            "\t\tstore i32 %9, i32* %res\n" +
            "\t\tbr label %read\n" +
            "\texit:\t; return res\n" +
            "\t\t%10 = load i32, i32* %res\n" +
            "\t\tret i32 %10\n" +
            "}\n";

    private List<String> variablesList;
    private List<BasicBlock> basicBlocksList;

    private Integer tempVariableCounter;
    private Integer whileCounter;
    private Integer ifCounter;

    /**
     * Run the code generator while performing the semantic analysis.
     *
     * @param program The root of the abstract syntax tree
     * @return The generated code
     * @throws SemanticException When a semantic problem is encountered
     */
    public String generate(Program program) throws SemanticException {
        reset();

        StringBuilder code = new StringBuilder();

        code.append(CodeGenerator.functions);

        code.append("define i32 @main() {\n");

        handleProgram(program);

        for (String variable : this.variablesList) {
            code.append("\t%").append(variable).append(" = alloca i32\n");
        }

        code.append("\tbr label %entry\n\n");

        for (BasicBlock basicBlock : this.basicBlocksList) {
            code.append(basicBlock.toString()).append("\n");
        }

        code.append("}\n");

        return code.toString();
    }

    /**
     * Reset all the attributes.
     */
    private void reset() {
        this.variablesList = new ArrayList<>();
        this.basicBlocksList = new ArrayList<>();

        this.tempVariableCounter = 0;
        this.whileCounter = 0;
        this.ifCounter = 0;
    }

    /**
     * Create and return a new basic block with the specified label.
     *
     * @param label The label of the basic block
     * @return The created basic block
     */
    private BasicBlock getNewBasicBlock(String label) {
        BasicBlock basicBlock = new BasicBlock(label);
        this.basicBlocksList.add(basicBlock);
        return basicBlock;
    }

    /**
     * Return a free name for a new temporary variable.
     *
     * @return The name of the new temporary variable
     */
    private String getNewTempVariable() {
        this.tempVariableCounter++;
        return this.tempVariableCounter.toString();
    }

    /**
     * Return a free number for a new if.
     *
     * @return The free number
     */
    private String getNewIf() {
        this.ifCounter++;
        return this.ifCounter.toString();
    }

    /**
     * Return a free number for a new while.
     *
     * @return The free number
     */
    private String getNewWhile() {
        this.whileCounter++;
        return this.whileCounter.toString();
    }

    /**
     * Handle the code generation of the program.
     *
     * @param program The program
     * @throws SemanticException When a semantic problem is encountered
     */
    public void handleProgram(Program program) throws SemanticException {
        BasicBlock basicBlock = getNewBasicBlock("entry");

        BasicBlock lastBasicBlock = handleCode(program.getCode(), basicBlock);
        lastBasicBlock.add("ret i32 0");
    }

    /**
     * Handle the code generation of the code.
     *
     * @param code The code
     * @param currentBasicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    public BasicBlock handleCode(Code code, BasicBlock currentBasicBlock) throws SemanticException {
        for (Instruction instruction : code.getInstructions()) {
            currentBasicBlock = instruction.dispatch(this, currentBasicBlock);
        }

        return currentBasicBlock;
    }

    /**
     * Handle the code generation of the assign.
     *
     * @param assign The assign
     * @param currentBasicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    public BasicBlock handleAssign(Assign assign, BasicBlock currentBasicBlock) throws SemanticException {
        String tempVariable = handleExpression(assign.getArithmeticExpression(), currentBasicBlock);
        currentBasicBlock.add("store i32 %" + tempVariable + " , i32 * %" + assign.getVariable());

        if (!this.variablesList.contains(assign.getVariable())) {
            this.variablesList.add(assign.getVariable());
        }

        return currentBasicBlock;
    }

    /**
     * Handle the code generation of the if.
     *
     * @param if_ The if
     * @param currentBasicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    public BasicBlock handleIf(If if_, BasicBlock currentBasicBlock) throws SemanticException {
        String n = getNewIf();
        String trueLabel = "if_" + n + "_true";
        BasicBlock trueBasicBlock = getNewBasicBlock(trueLabel);
        String falseLabel = "if_" + n + "_false";
        String endLabel = "if_" + n + "_end";


        handleCondition(if_.getCondition(), currentBasicBlock, trueLabel, falseLabel);

        BasicBlock lastBasicBlock;
        lastBasicBlock = handleCode(if_.getCodeTrue(), trueBasicBlock);
        lastBasicBlock.add("br label %" + endLabel);

        BasicBlock falseBasicBlock = getNewBasicBlock(falseLabel);
        lastBasicBlock = falseBasicBlock;
        if (if_.getCodeFalse() != null) {
            lastBasicBlock = handleCode(if_.getCodeFalse(), falseBasicBlock);
        }
        lastBasicBlock.add("br label %" + endLabel);

        return getNewBasicBlock(endLabel);
    }

    /**
     * Handle the code generation of the while.
     *
     * @param while_ The while
     * @param currentBasicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    public BasicBlock handleWhile(While while_, BasicBlock currentBasicBlock) throws SemanticException {
        String n = getNewWhile();
        String condLabel = "while_" + n + "_cond";
        BasicBlock condBasicBlock = getNewBasicBlock(condLabel);
        String loopLabel = "while_" + n + "_loop";
        BasicBlock loopBasicBlock = getNewBasicBlock(loopLabel);
        String endLabel = "while_" + n + "_end";
        BasicBlock endBasicBlock = getNewBasicBlock(endLabel);

        currentBasicBlock.add("br label %" + condLabel);

        handleCondition(while_.getCondition(), condBasicBlock, loopLabel, endLabel);

        BasicBlock lastBasicBlock = handleCode(while_.getCode(), loopBasicBlock);
        lastBasicBlock.add("br label %" + condLabel);

        return endBasicBlock;
    }

    /**
     * Handle the code generation of the print.
     *
     * @param print The print
     * @param currentBasicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    public BasicBlock handlePrint(Print print, BasicBlock currentBasicBlock) throws SemanticException {
        String variableName = (String) print.getVariable().getValue();
        if (!this.variablesList.contains(variableName)) {
            throw new SemanticException("Semantic error at " +
                    "line " + print.getVariable().getLine() + " column " + print.getVariable().getColumn() + " :\n" +
                    "\tVariable \"" + print.getVariable().getValue() + "\" used before assignment");
        }
        String tempVariable = getNewTempVariable();

        currentBasicBlock.add("%" + tempVariable + " = load i32 , i32 * %" + variableName);
        currentBasicBlock.add("call void @println(i32 %" + tempVariable + ")");
        return currentBasicBlock;
    }

    /**
     * Handle the code generation of the read.
     *
     * @param read The read
     * @param currentBasicBlock The current basic block
     * @return The last basic block
     */
    public BasicBlock handleRead(Read read, BasicBlock currentBasicBlock) {
        if (!this.variablesList.contains(read.getVariable())) {
            this.variablesList.add(read.getVariable());
        }

        String tempVariable = getNewTempVariable();
        currentBasicBlock.add("%" + tempVariable + " = call i32 @readInt()");
        currentBasicBlock.add("store i32 %" + tempVariable + " , i32 * %" + read.getVariable());

        return currentBasicBlock;
    }


    /**
     * Handle the code generation of the condition.
     *
     * @param condition The condition
     * @param currentBasicBlock The current basic block
     * @param trueLabel The label to jump to when the condition is true
     * @param falseLabel The label to jump to when the condition is false
     * @throws SemanticException When a semantic problem is encountered
     */
    public void handleCondition(Condition condition, BasicBlock currentBasicBlock, String trueLabel, String falseLabel) throws SemanticException {
        String variableA = handleExpression(condition.getLeftMember(), currentBasicBlock);
        String variableB = handleExpression(condition.getRightMember(), currentBasicBlock);

        String operator = (condition.getOperator().equals("="))? "eq" : "sgt";
        String condVariable = getNewTempVariable();
        currentBasicBlock.add("%" + condVariable + " = icmp " + operator + " i32 %" + variableA + " , %" + variableB);
        currentBasicBlock.add("br i1 %" + condVariable + " , label %" + trueLabel + " , label %" + falseLabel);

    }

    /**
     * Handle the code generation of the arithmetic expression.
     *
     * @param arithmeticExpression The arithmetic expression
     * @param currentBasicBlock The current basic block
     * @return The variable where the result was stored
     * @throws SemanticException When a semantic problem is encountered
     */
    public String handleExpression(ArithmeticExpression arithmeticExpression, BasicBlock currentBasicBlock) throws SemanticException {
        String lastVariable = handleProduct(arithmeticExpression.getTerms().get(0), currentBasicBlock);

        for (int i = 0; i < arithmeticExpression.getOperators().size(); i++) {
            String tempVariable = handleProduct(arithmeticExpression.getTerms().get(i + 1), currentBasicBlock);
            String operator = (arithmeticExpression.getOperators().get(i) == LexicalUnit.PLUS) ? "add" : "sub";
            String newVariable = getNewTempVariable();
            currentBasicBlock.add("%" + newVariable + " = " + operator + " i32 %" + lastVariable + " , %" + tempVariable);
            lastVariable = newVariable;
        }

        return lastVariable;
    }

    /**
     * Handle the code generation of the product.
     *
     * @param product The product
     * @param currentBasicBlock The current basic block
     * @return The variable where the result was stored
     * @throws SemanticException When a semantic problem is encountered
     */
    public String handleProduct(Product product, BasicBlock currentBasicBlock) throws SemanticException {
        String lastVariable = handleAtom(product.getFactors().get(0), currentBasicBlock);

        for (int i = 0; i < product.getOperators().size(); i++) {
            String tempVariable = handleAtom(product.getFactors().get(i + 1), currentBasicBlock);
            String operator = (product.getOperators().get(i) == LexicalUnit.TIMES) ? "mul" : "sdiv";
            String newVariable = getNewTempVariable();
            currentBasicBlock.add("%" + newVariable + " = " + operator + " i32 %" + lastVariable + " , %" + tempVariable);
            lastVariable = newVariable;
        }

        return lastVariable;
    }

    /**
     * Handle the code generation of the atom.
     *
     * @param atom The atom
     * @param currentBasicBlock The current basic block
     * @return The variable where the result was stored
     * @throws SemanticException When a semantic problem is encountered
     */
    public String handleAtom(Atom atom, BasicBlock currentBasicBlock) throws SemanticException {
        String var = null;
        switch (atom.getType()) {
            case 1:
                String tempVariable = handleAtom(atom.getAtom(), currentBasicBlock);
                var = getNewTempVariable();
                currentBasicBlock.add("%" + var + " = mul i32 %" + tempVariable + " , -1");
                break;

            case 2:
                var = getNewTempVariable();
                currentBasicBlock.add("%" + var + " = add i32 0 , " + atom.getNumber());
                break;

            case 3:
                Symbol label = atom.getVariable();
                String variableName = (String) label.getValue();
                if (!this.variablesList.contains(variableName)) {
                    throw new SemanticException("Semantic error at " +
                    "line " + label.getLine() + " column " + label.getColumn() + " :\n" +
                    "\tVariable \"" + label.getValue() + "\" used before assignment");
                }
                var = getNewTempVariable();
                currentBasicBlock.add("%" + var + " = load i32 , i32 * %" + label.getValue());
                break;

            case 4:
                var = handleExpression(atom.getExpr(), currentBasicBlock);
                break;
        }
        return var;
    }
}
