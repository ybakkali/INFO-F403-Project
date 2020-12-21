@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

; Function Attrs: nounwind uwtable
define void @println(i32 %x) #0 {
	%1 = alloca i32, align 4
	store i32 %x, i32* %1, align 4
	%2 = load i32, i32* %1, align 4
	%3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
	ret void
}

declare i32 @printf(i8*, ...) #1

declare i32 @getchar()

define i32 @readInt() {
	entry:	; create variables
		%res   = alloca i32
		%digit = alloca i32
		store i32 0, i32* %res
		br label %read

	read:	; read a digit
		%0 = call i32 @getchar()
		%1 = sub i32 %0, 48
		store i32 %1, i32* %digit
		%2 = icmp ne i32 %0, 10	; is the char entered '\n'?
		br i1 %2, label %check, label %exit

	check:	; is the char entered a number?
		%3 = icmp sle i32 %1, 9
		%4 = icmp sge i32 %1, 0
		%5 = and i1 %3, %4
		br i1 %5, label %save, label %exit

	save:	; res<-res*10+digit
		%6 = load i32, i32* %res
		%7 = load i32, i32* %digit
		%8 = mul i32 %6, 10
		%9 = add i32 %8, %7
		store i32 %9, i32* %res
		br label %read

	exit:	; return res
		%10 = load i32, i32* %res
		ret i32 %10
}

define i32 @main() {
	%number1 = alloca i32
	%number2 = alloca i32
	%number3 = alloca i32
	%largest = alloca i32
	br label %entry

	entry:
		%1 = call i32 @readInt()
		store i32 %1 , i32 * %number1
		%2 = call i32 @readInt()
		store i32 %2 , i32 * %number2
		%3 = call i32 @readInt()
		store i32 %3 , i32 * %number3
		%4 = load i32 , i32 * %number1
		%5 = load i32 , i32 * %number2
		%6 = icmp sgt i32 %4 , %5
		br i1 %6 , label %if_1_true , label %if_1_false

	if_1_true:
		%7 = load i32 , i32 * %number1
		%8 = load i32 , i32 * %number3
		%9 = icmp sgt i32 %7 , %8
		br i1 %9 , label %if_2_true , label %if_2_false

	if_2_true:
		%10 = load i32 , i32 * %number1
		store i32 %10 , i32 * %largest
		br label %if_2_end

	if_2_false:
		%11 = load i32 , i32 * %number3
		store i32 %11 , i32 * %largest
		br label %if_2_end

	if_2_end:
		br label %if_1_end

	if_1_false:
		%12 = load i32 , i32 * %number2
		%13 = load i32 , i32 * %number1
		%14 = icmp sgt i32 %12 , %13
		br i1 %14 , label %if_3_true , label %if_3_false

	if_3_true:
		%15 = load i32 , i32 * %number2
		%16 = load i32 , i32 * %number3
		%17 = icmp sgt i32 %15 , %16
		br i1 %17 , label %if_4_true , label %if_4_false

	if_4_true:
		%18 = load i32 , i32 * %number2
		store i32 %18 , i32 * %largest
		br label %if_4_end

	if_4_false:
		%19 = load i32 , i32 * %number3
		store i32 %19 , i32 * %largest
		br label %if_4_end

	if_4_end:
		br label %if_3_end

	if_3_false:
		br label %if_3_end

	if_3_end:
		br label %if_1_end

	if_1_end:
		%20 = load i32 , i32 * %largest
		call void @println(i32 %20)
		ret i32 0

}
