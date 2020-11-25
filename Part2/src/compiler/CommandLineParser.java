package compiler;

import compiler.exceptions.CommandLineException;

import java.util.ArrayList;
import java.util.List;

public class CommandLineParser {

    String filePath;
    List<Option> options = new ArrayList<>();

    public  CommandLineParser(String[] args) throws CommandLineException {

        if (args.length > 0) {
            int i = 0;
            while (i < args.length) {
                if (args[0].charAt(0) == '-') {
                    throw new CommandLineException("The first argument cannot be an option");
                } else {
                    this.filePath = args[0];
                }
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
                }
                i++;
            }
        } else {
            throw new CommandLineException("1 argument required");
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public List<Option> getOptions() {
        return options;
    }
}
