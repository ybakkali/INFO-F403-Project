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
	%number = alloca i32
	%true = alloca i32
	%false = alloca i32
	br label %entry

	entry:
		%1 = add i32 0 , 10
		store i32 %1 , i32 * %number
		%2 = add i32 0 , 1
		store i32 %2 , i32 * %true
		%3 = add i32 0 , 0
		store i32 %3 , i32 * %false
		%4 = load i32 , i32 * %number
		%5 = add i32 0 , 10
		%6 = icmp eq i32 %4 , %5
		br i1 %6 , label %if_1_true , label %if_1_false

	if_1_true:
		%7 = load i32 , i32 * %true
		call void @println(i32 %7)
		br label %if_1_end

	if_1_false:
		%8 = load i32 , i32 * %false
		call void @println(i32 %8)
		br label %if_1_end

	if_1_end:
		%9 = load i32 , i32 * %number
		%10 = add i32 0 , 100
		%11 = icmp ne i32 %9 , %10
		br i1 %11 , label %if_2_true , label %if_2_false

	if_2_true:
		%12 = load i32 , i32 * %true
		call void @println(i32 %12)
		br label %if_2_end

	if_2_false:
		%13 = load i32 , i32 * %false
		call void @println(i32 %13)
		br label %if_2_end

	if_2_end:
		%14 = load i32 , i32 * %number
		%15 = add i32 0 , 10
		%16 = icmp sgt i32 %14 , %15
		br i1 %16 , label %if_3_true , label %if_3_false

	if_3_true:
		%17 = load i32 , i32 * %true
		call void @println(i32 %17)
		br label %if_3_end

	if_3_false:
		%18 = load i32 , i32 * %false
		call void @println(i32 %18)
		br label %if_3_end

	if_3_end:
		%19 = load i32 , i32 * %number
		%20 = add i32 0 , 10
		%21 = icmp sge i32 %19 , %20
		br i1 %21 , label %if_4_true , label %if_4_false

	if_4_true:
		%22 = load i32 , i32 * %true
		call void @println(i32 %22)
		br label %if_4_end

	if_4_false:
		%23 = load i32 , i32 * %false
		call void @println(i32 %23)
		br label %if_4_end

	if_4_end:
		%24 = load i32 , i32 * %number
		%25 = add i32 0 , 10
		%26 = icmp slt i32 %24 , %25
		br i1 %26 , label %if_5_true , label %if_5_false

	if_5_true:
		%27 = load i32 , i32 * %true
		call void @println(i32 %27)
		br label %if_5_end

	if_5_false:
		%28 = load i32 , i32 * %false
		call void @println(i32 %28)
		br label %if_5_end

	if_5_end:
		%29 = load i32 , i32 * %number
		%30 = add i32 0 , 10
		%31 = icmp sle i32 %29 , %30
		br i1 %31 , label %if_6_true , label %if_6_false

	if_6_true:
		%32 = load i32 , i32 * %true
		call void @println(i32 %32)
		br label %if_6_end

	if_6_false:
		%33 = load i32 , i32 * %false
		call void @println(i32 %33)
		br label %if_6_end

	if_6_end:
		ret i32 0

}
