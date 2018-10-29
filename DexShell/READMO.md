## 命令执行流程


0 打包
    1. 将壳程序的apk放到[path]/unshell/unshellSource.apk (一旦放好不需要再次修改)

1. 清空[path]/temp文件夹。
2. apktool d [path]/data/源.apk放到[path]/temp/sourceapktool 文件下。
3. apktool d [path]/unshell/unshellSource.apk放到[path]/temp/unshellapktool
3. 修改壳程序原代码的[path]/temp/unshellapktool/AndroidManifest.xml（取[path]/temp/sourceapktool/AndroidManifest.xml必要信息写入）。
4. 使用源app反编译res替换壳程序反编译res。
5. apktool b [path]/temp/unshellapktool 还原 unshellapk.apk (得到结果的真实壳程序为：[path]/temp/unshellapk.apk)。


5. unzip壳程序apk([path]/temp/unshellapk.apk)到[path]/temp/unshellapk 目录下。
6. 将壳程序的[path]/temp/unshellapk/classes.dex和[path]/temp/sourceapk/源.apk作为输入参数执行加固程序得到新的classes.dex，放到[path]/temp/目录。



7. 使用[path]/temp/classes.dex替换[path]/temp/unshellapk/classes.dex文件。
8. unzip [path]/data/源.apk到[path]/temp/sourceunzip目录下。
-- 【去跳，被第四步替换了】9. 使用[path]/temp/sourceapk/res和[path]/temp/sourceapk/resources.arsc替换[path]/temp/unshellapk/res和[path]/temp/unshellapk/resources.arsc。
10. 使用zip压缩[path]/temp/unshellapk所有目录，结果为[path]/temp/result.zip。
11. 签名[path]/temp/result.zip生成[path]/temp/result.apk文件。




## 常见问题
* [Apktool回编译apk遇到的问题及解决方案](https://blog.csdn.net/github_39998545/article/details/81484122)