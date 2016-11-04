# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Bill\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#指定压缩级别
-optimizationpasses 5

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification




