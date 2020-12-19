all: build testAll

build:
	@echo Building the compiler
	jflex src/compiler/Scanner.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C bin .
	@echo Building completed
	@echo

testAll: test out exec

test: Force
	@echo Testing the base program :
	java -jar dist/part3.jar test/Factorial.fs
	@echo Testing completed
	@echo

out: Force
	@echo Testing the -o option :
	java -jar dist/part3.jar test/Factorial.fs -o Factorial.ll
	@echo Testing completed
	@echo

exec: Force
	@echo Testing the -exec option :
	java -jar dist/part3.jar -exec test/Factorial.fs
	@echo Testing completed
	@echo

Force:
