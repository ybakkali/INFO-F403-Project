package compiler;

import compiler.exceptions.CommandLineException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a parser that can parse a command line.
 */
public class CommandLineParser {

    private final String filePath;
    final private List<Option> options = new ArrayList<>();

    /**
     * Parse the specified command line.
     *
     * @param args The arguments
     * @throws CommandLineException When a problem is encountered
     */
    public CommandLineParser(String[] args) throws CommandLineException {

        switch (args.length) {
            case 1:
                this.filePath = args[0];
                break;
            case 2:
                int index = (args[0].charAt(0) == '-') ? 0 : 1;
                this.filePath = (index == 0) ? args[args.length - 1] : args[0];

                if (args[index].equals("-exec")) {
                    this.options.add(new Option("-exec"));
                } else {
                    throw new CommandLineException("Unrecognised option detected");
                }

                break;

            case 3:
                int i = (args[0].charAt(0) == '-') ? 0 : 1;
                this.filePath = (i == 0) ? args[args.length - 1] : args[0];

                if (args[i].equals("-o")) {
                    if (args[i + 1].charAt(0) != '-') {
                        options.add(new Option("-o", args[i + 1]));
                    } else {
                        throw new CommandLineException("After -o option an argument required");
                    }
                } else {
                    throw new CommandLineException("Unrecognised option detected");
                }

                break;
            default:
                throw new CommandLineException("Unrecognised command line detected");
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
