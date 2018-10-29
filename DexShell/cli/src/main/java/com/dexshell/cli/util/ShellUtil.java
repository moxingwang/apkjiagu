package com.dexshell.cli.util;

import com.dexshell.cli.exception.UnknownException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ShellUtil {
    public static void exec(String cmd) throws IOException, InterruptedException {
        System.out.println("执行命令"+cmd);
        Process process = Runtime.getRuntime().exec(cmd);

        InputStream errorStream = process.getErrorStream();
        InputStream inputStream = process.getInputStream();

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        BufferedReader successReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuilder = new StringBuilder();

        boolean error = false;
        String line;

        while((line = successReader.readLine())!= null){
            stringBuilder.append(line +"\n");
            System.out.println(line);
        }
        while((line = errorReader.readLine())!= null){
            error = true;
            stringBuilder.append(line +"\n");
        }

        process.waitFor();

        inputStream.close();
        errorStream.close();
        successReader.close();
        errorReader.close();
        process.destroy();

        if(error){
            throw new UnknownException(stringBuilder.toString());
        }
    }
}
