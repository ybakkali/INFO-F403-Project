package compiler;

import compiler.exceptions.CommandLineException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a parser that can parse a command line.
 */
public class CommandLineParser {

    String filePath;
    List<Option> options = new ArrayList<>();

    /**
     * Parse the specified command line.
     *
     * @param args The arguments
     * @throws CommandLineException When a problem is encountered
     */
    public CommandLineParser(String[] args) throws CommandLineException {

        if (args.length == 0) { throw new CommandLineException("1 argument required"); }

        else if (args.length == 1) { this.filePath = args[0]; }

        else if (args.length == 2) {
            if (args[0].charAt(0) == '-') {
                if (args[0].equals("-exec")) {
                    this.options.add(new Option("-exec"));
                    this.filePath = args[1];
                }
            } else { throw new CommandLineException("Unrecognised option detected"); }

        } else if (args.length == 3) {
            int index = (args[0].charAt(0) == '-') ? 0 : 1;
            this.filePath = (index == 0) ? args[args.length - 1] : args[0];

            if (args[index].equals("-o")) {
                if (args[index + 1].charAt(0) != '-') {
                    options.add(new Option("-o", args[index + 1]));
                } else {
                    throw new CommandLineException("After -o option an argument required");
                }
            }
        } else { throw new CommandLineException("Unrecognised command line detected"); }
    }

    /**
     * Get the file path.
     *
     * @return The file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Get the options.
     *
     * @return The options
     */
    public List<Option> getOptions() {
        return options;
    }
}
