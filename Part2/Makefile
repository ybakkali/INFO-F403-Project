all:
	jflex src/compiler/Scanner.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/Part2.jar Main -C bin .

testing: Force
	java -jar dist/Part2.jar test/Factorial.fs -wt test.tex -v

Force: