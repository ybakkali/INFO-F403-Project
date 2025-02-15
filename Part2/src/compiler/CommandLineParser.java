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

        if (args.length > 0) {
            int i = 0;
            while (i < args.length) {
                if (args[i].charAt(0) == '-') {
                    if (args[i].equals("-v")) {
                        this.options.add(new Option("-v"));
                    } else if (args[i].equals("-wt")) {
                        if (i+1 < args.length && args[i+1].charAt(0) != '-') {
                            options.add(new Option("-wt", args[i+1]));
                            i++;
                        } else {
                            throw new CommandLineException("After -wt option an argument required");
                        }
                    } else {
                        throw new CommandLineException("Unrecognised option detected");
                    }
                } else {
                    this.filePath = args[i];
                }
                i++;
            }
        } else {
            throw new CommandLineException("1 argument required");
        }

        if (this.filePath == null) {
            throw new CommandLineException("The source file argument required");
        }
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
