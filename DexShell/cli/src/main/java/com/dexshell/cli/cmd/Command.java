package com.dexshell.cli.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface Command {
    boolean validateSyntax(CommandLine commandLine);

    Options getOptions();

    void exec(CommandLine commandLine);

    void printHelp();
}
