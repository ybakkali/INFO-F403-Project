all:
	jflex src/compiler/Scanner.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C bin .

testing: Force
	java -jar dist/part2.jar test/Factorial.fs -wt test.tex -v

Force: