package com.dexshell.cli;

import java.io.File;

public final class Environment {

    public static File getAppHomeDir() {
        return new File(System.getProperty("app.home"));
    }
}
