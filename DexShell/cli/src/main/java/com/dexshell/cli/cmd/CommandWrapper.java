package com.dexshell.cli.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class CommandWrapper extends CommandLine {
    private CommandLine cmd;

    public CommandWrapper(CommandLine cmd) {
        this.cmd = cmd;
    }

    @Override
    public boolean hasOption(String opt) {
        return cmd.hasOption(opt);
    }

    @Override
    public boolean hasOption(char opt) {
        return cmd.hasOption(opt);
    }

    @Override
    @Deprecated
    public Object getOptionObject(String opt) {
        return cmd.getOptionObject(opt);
    }

    @Override
    public Object getParsedOptionValue(String opt) throws ParseException {
        return cmd.getParsedOptionValue(opt);
    }

    @Override
    public Object getOptionObject(char opt) {
        return cmd.getOptionObject(opt);
    }

    @Override
    public String getOptionValue(String opt) {
        return cmd.getOptionValue(opt);
    }

    @Override
    public String getOptionValue(char opt) {
        return cmd.getOptionValue(opt);
    }

    @Override
    public String[] getOptionValues(String opt) {
        return cmd.getOptionValues(opt);
    }

    @Override
    public String[] getOptionValues(char opt) {
        return cmd.getOptionValues(opt);
    }

    @Override
    public String getOptionValue(String opt, String defaultValue) {
        return cmd.getOptionValue(opt, defaultValue);
    }

    @Override
    public String getOptionValue(char opt, String defaultValue) {
        return cmd.getOptionValue(opt, defaultValue);
    }

    @Override
    public Properties getOptionProperties(String opt) {
        return cmd.getOptionProperties(opt);
    }

    @Override
    public String[] getArgs() {
        List<String> argList = getArgList();
        String[] answer = new String[argList.size()];
        argList.toArray(answer);
        return answer;

    }

    @Override
    public List<String> getArgList() {
        List<String> argList = cmd.getArgList();
        if (argList.size() > 0) {
            argList.remove(0);
        }
        return argList;
    }

    @Override
    public Iterator<Option> iterator() {
        return cmd.iterator();
    }

    @Override
    public Option[] getOptions() {
        return cmd.getOptions();
    }
}
