package com.dexshell.cli.cmd;

import com.dexshell.cli.encrypter.SimpleEncrypter;
import com.dexshell.cli.exception.UnknownException;
import com.dexshell.cli.util.ApkToolUtil;
import com.dexshell.cli.util.ShellUtil;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ShellCommand implements Command {

    public static final String BASE_DIR = System.getProperty("app.home");

    private Options options;

    public ShellCommand() {
        Options options = new Options();
//        Option shellOp = Option.builder("u").required(true).hasArg().longOpt("unshell").argName("file").desc("The shell app file").build();
        Option algorithmOp = Option.builder("a").required(false).hasArg().longOpt("algorithm").argName("algorithm").desc("The algorithm for encrypting apk. The available algorithms are as follows: simple").build();
//        options.addOption(shellOp);
        options.addOption(algorithmOp);
        this.options = options;
    }

    @Override
    public boolean validateSyntax(CommandLine commandLine) {
        String[] args0 = commandLine.getArgs();
        return args0.length == 1;
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public void exec(CommandLine commandLine) {
        String[] args0 = commandLine.getArgs();

        String input = args0[0];

        File payloadFile = new File(input);
        if (!payloadFile.exists()) {
            System.err.println("input file not exists");
            return;
        }
        if (!payloadFile.isFile()) {
            System.err.println("The type of input file is not a file");
            return;
        }

//        String unShellFilePath = commandLine.getOptionValue("u");
//        File unShellDexFile = new File(unShellFilePath);
//        if (!unShellDexFile.exists()) {
//            System.err.println("unshell dex file not exists");
//            return;
//        }

        try {
            //删除temp目录
            delFolder(BASE_DIR + "/temp");
            //生成脱壳程序
            File unShellApkFile = generateShellDex(commandLine, payloadFile);
            //生成加密后的dex
//            File encryptDexFile = generateEncryptDex(commandLine, payloadFile, unShellApkFile);
            File encryptDexFile = Jiagu.jiagu(commandLine, payloadFile, unShellApkFile);
            //打包生成apk
            File apkFile = generateAPK(commandLine, payloadFile, encryptDexFile);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private File generateAPK(CommandLine commandLine, File payloadFile, File encryptDexFile) throws IOException {
        try {
            //解压壳apk到 temp/data/apk/unshell.apk
//            unZipFiles(BASE_DIR + "/temp/unshellapk.apk", BASE_DIR + "/temp/unshellapk/");

            // 实现temp/classes.dex 替换 temp/unshellapk/classes.dex
            writeFile(BASE_DIR + "/temp/classes.dex", BASE_DIR + "/temp/unshellapk/classes.dex");

            // 解压源apk到 temp/sourceapk目录下
            unZipFiles(payloadFile, BASE_DIR + "/temp/sourceapk/");

            // 替换res文件夹和resources.arsc文件
            // 删除/temp/unshellapk/目录下的res文件夹并复制./temp/sourceapk/res到该目录下
            // 实现resources.arsc文件替换
//            delFolder(BASE_DIR + "/temp/unshellapk/res");
//            copyFolder(BASE_DIR + "/temp/sourceapk/res", BASE_DIR + "/temp/unshellapk/res");
//            writeFile(BASE_DIR + "/temp/sourceapk/resources.arsc", BASE_DIR + "/temp/unshellapk/resources.arsc");

            // 压缩temp/unshellapk文件夹在统计目录下生成result.zip文件
            zipFiles(new File(BASE_DIR + "/temp/result.zip"), new File(BASE_DIR + "/temp/unshellapk"));

            // 签名
            sign(BASE_DIR + "/temp/result.apk", BASE_DIR + "/temp/result.zip");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new File(BASE_DIR + "/temp/unshellapk/result.apk");
    }

    private File generateShellDex(CommandLine commandLine, File payloadFile) throws IOException, InterruptedException, DocumentException {
        //使用apktool反编译源程序
        ApkToolUtil.decode(payloadFile.getAbsolutePath(), BASE_DIR + "/temp/sourceapktool");

        //反编译源壳程序
        ApkToolUtil.decode(BASE_DIR + "/unshell/unshellSource.apk", BASE_DIR + "/temp/unshellapktool");

        //copy资源
        delFolder(BASE_DIR + "/temp/unshellapktool/res");
        copyFolder(BASE_DIR + "/temp/sourceapktool/res", BASE_DIR + "/temp/unshellapktool/res");

        //替换AndroidManifest文件   /temp/unshellapk
        ApkToolUtil.replaceAndroidManifest();

        //编译壳程序
        String resultFileDir = ApkToolUtil.encode(BASE_DIR + "/temp/unshellapktool");


        return new File(resultFileDir);
    }

    private File generateEncryptDex(CommandLine commandLine, File payloadFile, File unShellDexFile) throws IOException {
        //输出的加密classes.dex
//        File outDex = new File(payloadFile.getParentFile(), FilenameUtils.getBaseName(payloadFile.getName()) + "-" + System.currentTimeMillis() + ".dex");
        File outDex = new File(BASE_DIR +"/temp/classes.dex");
        System.out.println("generate encrypted dex file: " + outDex.getAbsolutePath());
        if (!outDex.createNewFile()) {
            throw new UnknownException("can not create encrypted dex file");
        }
        try (
                RandomAccessFile outFile = new RandomAccessFile(outDex, "rw");
                OutputStream outputStream = Channels.newOutputStream(outFile.getChannel());
                InputStream unShellDexInput = new FileInputStream(unShellDexFile);
                InputStream payloadiuput = new FileInputStream(payloadFile);
        ) {
            // 添加脱壳程序代码
            long unShellDexFileSize = IOUtils.copyLarge(unShellDexInput, outputStream);
            System.out.println("unshell dex size: " + unShellDexFileSize);
            // 添加加密数据
            long length = new SimpleEncrypter().encrypt(payloadiuput, outputStream);
            System.out.println("encrypted payload size: " + length);
            //添加加密数据长度
            outFile.writeInt((int) length);
            long fileSize = unShellDexFileSize + length + 4;
            System.out.println("new dex size: " + fileSize);
            // 修改DEX file size文件头
            outFile.seek(32);
            outFile.writeInt((int) fileSize);
            // 修改DEX SHA1 文件头
            fixSHA1Header(outFile);
            // 修改DEX CheckSum文件头
            fixCheckSumHeader(outFile);

            return outDex;
        }
    }

    /**
     * 修改dex头 sha1值
     *
     * @param outFile
     * @throws IOException
     */
    private void fixSHA1Header(RandomAccessFile outFile) throws IOException {
        Hasher sha1Hasher = Hashing.sha1().newHasher();
        byte[] buf = new byte[64];
        int n = 0;
        outFile.seek(32);
        if ((n = outFile.read(buf)) > 0) {
            sha1Hasher.putBytes(buf, 0, n);
        }
        HashCode hashCode = sha1Hasher.hash();
        System.out.println("new dex hash: " + hashCode.toString());
        outFile.seek(12);
        outFile.write(hashCode.asBytes());
    }

    /**
     * 修改dex头，CheckSum 校验码
     *
     * @param outFile
     */
    private static void fixCheckSumHeader(RandomAccessFile outFile) throws IOException {
        Hasher adler32Hasher = Hashing.adler32().newHasher();
        byte[] buf = new byte[64];
        int n = 0;
        outFile.seek(12);
        if ((n = outFile.read(buf)) > 0) {
            adler32Hasher.putBytes(buf, 0, n);
        }
        HashCode hashCode = adler32Hasher.hash();
        System.out.println("new dex checkSum: " + hashCode.toString());
        outFile.seek(8);
        outFile.writeInt(hashCode.asInt());
    }

    /**
     * 实现单文件替换
     * sourceFile 源文件 targetFile 目标文件
     */
    public static void writeFile(String sourceFile, String targetFile) throws IOException {
//        File file1 = new File("/temp/classes.dex");
        File file1 = new File(sourceFile);
        FileInputStream in = new FileInputStream(file1);
//        File file2 = new File("/temp/unshellapk/classes.dex");
        File file2 = new File(targetFile);
        FileOutputStream out = new FileOutputStream(file2);
        try {

            byte[] bytes = new byte[1024];
            int n = -1;
            while ((n = in.read(bytes, 0, bytes.length)) != -1) {
                out.write(bytes, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("******************源文件+" + sourceFile + " 目标文件" + targetFile + "********************");
        System.out.println("******************文件内容替换完成********************");
    }

    public static void unZipFiles(String zipFile, String descDir) throws IOException {
        File unShellFile = new File(zipFile);
        if (!unShellFile.exists()) {
            throw new FileNotFoundException("unShell.apk file not exists");
        }
        unZipFiles(unShellFile, descDir);
    }

    /**
     * 实现将文件解压到指定目录
     */
    public static void unZipFiles(File zipFile, String descDir) throws IOException {
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
//        String name = zip.getName().substring(zip.getName().lastIndexOf('\\') + 1, zip.getName().lastIndexOf('.'));
        String name = "";

        File pathFile = new File(descDir + name);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + name + "/" + zipEntryName).replaceAll("\\*", "/");

            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            // 输出文件路径信息
//          System.out.println(outPath);

            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************解压文件：" + zipFile + "目标目录：" + descDir + "********************");
        System.out.println("******************解压完毕********************");
        return;
    }

    /**
     * 实现将文件夹复制到指定目录下
     * oldPath 源文件夹目录 newPath 目标目录
     */
    public static void copyFolder(String oldPath, String newPath) throws IOException {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream in = new FileInputStream(temp);
                    FileOutputStream out = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = in.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    out.flush();
                    out.close();
                    in.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }
        System.out.println("复制整个文件夹内容成功！");
    }

    /**
     * 实现将目录下的文件或文件夹全部删除
     * 删除文件夹前要清空
     */
    public static void delFolder(String folderPath) throws IOException {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("删除文件夹成功！");
    }

    public static boolean delAllFile(String path) throws IOException {
        try {
            boolean flag = false;
            File file = new File(path);
            if (!file.exists()) {
                return flag;
            }
            if (!file.isDirectory()) {
                return flag;
            }
            String[] tempList = file.list();
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                    delFolder(path + "/" + tempList[i]);//再删除空文件夹
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            System.out.println("删除文件夹内容出错！");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 压缩文件
     *
     * @param srcfile
     */
    public static void zipFiles(File targetFile, File srcfile) throws IOException {

        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(targetFile));

            if (srcfile.isFile()) {
                zipFile(srcfile, out, "");
            } else {
                File[] list = srcfile.listFiles();
                for (int i = 0; i < list.length; i++) {
                    compress(list[i], out, "");
                }
            }

            System.out.println("压缩完毕");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩文件夹里的文件
     * 起初不知道是文件还是文件夹--- 统一调用该方法
     *
     * @param file
     * @param out
     * @param basedir
     */
    private static void compress(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            zipDirectory(file, out, basedir);
        } else {
            zipFile(file, out, basedir);
        }
    }

    /**
     * 压缩单个文件
     *
     * @param srcfile
     */
    public static void zipFile(File srcfile, ZipOutputStream out, String basedir) {
        if (!srcfile.exists())
            return;

        byte[] buf = new byte[1024];
        FileInputStream in = null;

        try {
            int len;
            in = new FileInputStream(srcfile);
            out.putNextEntry(new ZipEntry(basedir + srcfile.getName()));

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.closeEntry();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩文件夹
     *
     * @param dir
     * @param out
     * @param basedir
     */
    public static void zipDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /**
     * 执行签名操作
     *
     * @param source
     * @param target
     * @throws IOException
     */
    public static void sign(String target, String source) throws IOException, InterruptedException {
        StringBuffer buffer = new StringBuffer("");
        buffer.append("jarsigner -verbose -keystore ").append(BASE_DIR).append("/unshell/qianming ").append(" -storepass 123456 -keypass " +
                "123456 -sigfile CERT -digestalg SHA1 -sigalg MD5withRSA -signedjar ").append(target).append(" ").append(source).append(" qianmingkey");
        /*Process process = Runtime.getRuntime().exec(buffer.toString());
        //关闭流释放资源
        if (process != null) {
            process.getOutputStream().close();
        }*/
        ShellUtil.exec(buffer.toString());
    }

    @Override
    public void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("[OPTIONS] input", "Options:", options, "");
    }
}
