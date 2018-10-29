package com.dexshell.cli.encrypter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Encrypter {
    long encrypt(InputStream in, OutputStream out) throws IOException;
}
