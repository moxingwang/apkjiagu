package com.dexshell.common.util;

import com.google.common.io.Files;

import java.io.File;

public class FileUtils {

    /**
     * The end of file flag
     */
    public static final int EOF = -1;

    /**
     * strips the given extension from the given full path file reference; does nothing if extension is not present
     *
     * @param fullPath  full path reference to a file
     * @param extension the extension to strip
     * @return full path without the given extension
     */
    public static String stripFileExtension(String fullPath, String extension) {
        final String maybeExtension = Files.getFileExtension(fullPath);
        if (!maybeExtension.equalsIgnoreCase(extension)) {
            return fullPath;
        }
        return fullPath.substring(0, fullPath.length() - maybeExtension.length() - 1);
    }

    public static String getDecryptedName(String fileName) {
        // strip .dxp if exists
        fileName = stripFileExtension(fileName, "dxp");

        if (!new File(fileName).exists()) {
            return fileName;
        }
        final String actualExtension = Files.getFileExtension(fileName);
        if (actualExtension.isEmpty()) {
            return fileName + "_decrypted";
        } else {
            return fileName.substring(0, fileName.length() - actualExtension.length() - 1) + "_decrypted." + actualExtension;//todo maybe optimize with substring
        }
    }

    public static String getPathRelativeTo(String absolute, String base) {
        return getPathRelativeTo(new File(absolute), new File(base));
    }

    public static String getPathRelativeTo(File absolute, File base) {
        return base.toURI().relativize(absolute.toURI()).getPath();
    }
}
