package com.dexshell.cli.util;

import com.dexshell.cli.cmd.ShellCommand;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ApkToolUtil {
    /**
     * test 项目
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
//        decode("/Users/mo/Desktop/app-debug.apk", ShellCommand.BASE_DIR + "/temp/sourceapktool");
        try {
            replaceAndroidManifest();
        } catch (Exception e) {

        }
    }

    /**
     * 替换壳程序AndroidManifest文件
     * https://blog.csdn.net/lz527657138/article/details/70673831
     */
    public static void replaceAndroidManifest() throws IOException, DocumentException {
        //替换为  <manifest package="com.mmall.www.dexshell"
        //获取源程序AndroidManifest替换application:android:name  ： android:name="com.dexshell.ProxyApplication"
        /**
         * application内部添加node
         * <meta-data
         *             android:name="APPLICATION_CLASS_NAME"
         *             android:value="sourceApplication文件目录"/>
         */
        String filePath = ShellCommand.BASE_DIR + "/temp/sourceapktool/AndroidManifest.xml";

        String sourceApplication = "";

        SAXReader reader = new SAXReader();

        Document document = (Document) reader.read(new File(filePath));
        Element manifest = document.getRootElement();
        List<Attribute> manifestAttributeList = manifest.attributes();
        for (Attribute attribute : manifestAttributeList) {
            if (attribute.getName().equals("package")) {
                sourceApplication += attribute.getValue();
                attribute.setValue("com.dexshell");
            }
        }
        Iterator it = manifest.elementIterator();
        while (it.hasNext()) {
            Element elementApplication = (Element) it.next();

            // 获取book的属性名以及属性值
            List<Attribute> applicationBookAttrs = elementApplication.attributes();

            boolean hashName = false;
            for (Attribute attr : applicationBookAttrs) {
                if (attr.getName().equals("name")) {
                    hashName = true;
                    //判断Android是否带有包名
                    if (attr.getValue().split("\\.").length > 1) {
                        sourceApplication = attr.getValue();
                    } else {
                        sourceApplication += "." + attr.getValue();
                    }
                    attr.setValue("com.dexshell.ProxyApplication");
                }
                System.out.println("属性名：" + attr.getName() + "--属性值：" + attr.getStringValue());
            }


            Element metaDataElement = elementApplication.addElement("meta-data");
            metaDataElement.addAttribute("android:name", "APPLICATION_CLASS_NAME");
            if (!hashName) {
                elementApplication.addAttribute(new QName("name", elementApplication.getNamespace()), "com.dexshell.ProxyApplication");
                metaDataElement.addAttribute("android:value", "android.app.Application");
            } else {
                metaDataElement.addAttribute("android:value", sourceApplication);
            }
        }

        OutputFormat opf = new OutputFormat("\t", true, "UTF-8");
        opf.setTrimText(true);
        //测试路径
        //XMLWriter writer = new XMLWriter(new FileOutputStream(ShellCommand.BASE_DIR + "/app/src/AndroidManifest.xml"), opf);
        //打包后的执行路径
        XMLWriter writer = new XMLWriter(new FileOutputStream(ShellCommand.BASE_DIR + "/temp/unshellapktool/AndroidManifest.xml"), opf);
        writer.write(document);
        writer.close();
    }

    public static void decode(String sourceFile, String resultFile) throws
            IOException, InterruptedException {
        String cmd = "java -jar " + ShellCommand.BASE_DIR + "/libs/apktool_2.3.4.jar apktool d " + sourceFile + " -o " + resultFile;

        ShellUtil.exec(cmd);
    }

    public static String encode(String sourceFile) throws
            IOException, InterruptedException {
//        ShellUtil.exec("rm -rf /Users/mo/Library/apktool/framework/1.apk");
        String resultFileDir = ShellCommand.BASE_DIR + "/temp/unshellapk.apk";
        String cmd = "java -jar " + ShellCommand.BASE_DIR + "/libs/apktool_2.3.4.jar  b " + sourceFile + " -o " + resultFileDir;

        ShellUtil.exec(cmd);

        return resultFileDir;
    }
}
