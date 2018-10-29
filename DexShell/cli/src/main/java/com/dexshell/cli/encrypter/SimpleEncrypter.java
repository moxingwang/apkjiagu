package com.dexshell.cli.encrypter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleEncrypter implements Encrypter {
    @Override
    public long encrypt(InputStream in, OutputStream out) throws IOException {
        int n = 0;
        long len = 0;
        while ((n = in.read()) != -1) {
            out.write(0xFFFF^n);
            len++;
        }
        return len;
    }
}
