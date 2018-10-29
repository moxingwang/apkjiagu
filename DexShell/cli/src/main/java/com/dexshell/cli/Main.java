package com.dexshell.cli;

import com.dexshell.cli.cmd.Command;
import com.dexshell.cli.cmd.ShellCommand;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String... args) {

        Command shellCommand = new ShellCommand();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            Options options = copyOf(shellCommand.getOptions());
            Option helpOp = Option.builder("h").required(false).longOpt("help").desc("for help").build();
            options.addOption(helpOp);
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Syntax err");
            shellCommand.printHelp();
            return;
        }
        if (cmd.hasOption("h") || !shellCommand.validateSyntax(cmd)) {
            shellCommand.printHelp();
            return;
        }
        shellCommand.exec(cmd);
    }

    private static Options copyOf(Options src) {
        Options target = new Options();
        src.getOptions().stream().forEach(option -> target.addOption(option));
        return target;
    }

}
