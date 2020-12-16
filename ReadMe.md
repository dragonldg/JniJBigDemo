###JNI实现步骤
（如果直接创建包含JNI的工程，可以不用看下面的步骤，修改一下即可）
#前提:已经下载NDK库，并配置到工程AndroidNDKLocation，CMakeLists.txt已经配置完成
（1）创建Kotlin class文件，书写external方法（对应Java中的native方法）及component object（对应Java中的静态代码块）加载so文件
（2）编译工程，生成.class文件，然后执行Javah生成对应的头文件，可以配置AS生成可视化工具。
AndroidStudio Settings->Tools->External Tools->EditTools
Program
$JDKPath$\bin\javah.exe

Parameters
-d src/main/cpp/ -cp "$Classpath$" $FileFQPackage$.$FileNameWithoutAllExtensions$

Working directory
$ModuleFileDir$
（3）local.properties中配置ndk.dir=D\:\\DevelopTools\\android-studio\\sdk\\ndk-bundle
app的 build.gradle中配置参考源代码
    defaultConfig {
        applicationId "com.ldg.jbigjni"
        minSdkVersion 21
        targetSdkVersion 29
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {//配置生成的so库类型
            cmake {
                cppFlags ""
//                abiFilters "armeabi-v7a", "x86_64", "arm64-v8a", "x86"
            }
        }
    }

    externalNativeBuild {
        cmake {
            path "src/main/cpp/CMakeLists.txt"
            version "3.10.2"
        }
    }


（4）使用Java的本地代码，实现external方法（如果Kotlin需要与C/C++交互，那么就用C/C++实现external方法。）
（5）编译工程生成动态库.so。
build->intermediates->cmake->debug/release->obj->x86/x86_64/armeabi-v7a->libJBigConvert.so


###JBIG算法（即二值图像压缩算法 binary image compression algorithm）
该算法在项目中是用在8583报文上送签名操作，可以节省大量的空间
工程中Android6.0及以上访问外存权限没有添加。
主要代码参考CharUtils.kt文件和CPP文件夹中的文件。




