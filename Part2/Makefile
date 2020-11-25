all:
	jflex src/compiler/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/Part2.jar Main -C bin .

testing:
		java -jar dist/Part1.jar test/Factorial.fs
