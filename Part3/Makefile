all:
	jflex src/compiler/Scanner.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C bin .

testing: Force
	java -jar dist/part3.jar test/Factorial.fs -wt test.tex -v

Force:
