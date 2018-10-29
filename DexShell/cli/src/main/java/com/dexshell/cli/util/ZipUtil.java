package com.dexshell.cli.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
    public static void unzip(String zipPath) throws IOException {
        unzip(zipPath, null);
    }

    public static void unzip(String zipPath, String targetPath) throws IOException {
        File file = new File(zipPath);
        if (!file.isFile()) {
            throw new FileNotFoundException("file not exist!");
        }
        if (targetPath == null || "".equals(targetPath)) {
            targetPath = File.separator + "temp";
        }

        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> files = zipFile.entries();
        ZipEntry entry = null;
        File outFile = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        while (files.hasMoreElements()) {
            entry = files.nextElement();
            outFile = new File(targetPath + File.separator + entry.getName());
            // 如果条目为目录，则跳向下一个
            if (entry.isDirectory()) {
                outFile.mkdirs();
                continue;
            }
            // 创建目录
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 创建新文件
            outFile.createNewFile();
            // 如果不可写，则跳向下一个条目
            if (!outFile.canWrite()) {
                continue;
            }
            try {
                bin = new BufferedInputStream(zipFile.getInputStream(entry));
                bout = new BufferedOutputStream(new FileOutputStream(outFile));
                byte[] buffer = new byte[1024];
                int readCount = -1;
                while ((readCount = bin.read(buffer)) != -1) {
                    bout.write(buffer, 0, readCount);
                }
            } finally {
                try {
                    bin.close();
                    bout.flush();
                    bout.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            unzip("C:\\Users\\lenovo.lenovo-PC\\Desktop\\app\\app-debug.apk");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
