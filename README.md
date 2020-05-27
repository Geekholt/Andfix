# 官网

https://github.com/alibaba/AndFix

# 版本更新状况

> Latest commit[0351a4b](https://github.com/alibaba/AndFix/commit/0351a4bc38a7d30bc61a6d3e74777d4eff4ad5e9)on 26 Dec 2016

**重要：AndFix支持Android版本从2.3到7.0，ARM和X86体系结构，Dalvik和ART运行时（32位和64位）**

# 使用场景

这里借助官方中的一张图，Andfix使用场景比较有限，**只能用于方法替换，不能进行类替换**。主要原理是通过方法替换，使得有bug的代码不能被执行到

![https://upload-images.jianshu.io/upload_images/10992781-ef6224ff56a35f96.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240](https://upload-images.jianshu.io/upload_images/10992781-ef6224ff56a35f96.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 主要原理

> AndFix judges the methods should be replaced by java custom annotation and replaces it by hooking it. AndFix has a native method `art_replaceMethod` in ART or `dalvik_replaceMethod` in Dalvik.

比较前后两个apk包的不同，将存在不同的方法打上自定义注解，通过注解在运行时判断哪个方法需要被替换，这个是通过native方法进行替换的，所以从这个角度来说，Andfix的兼容性并不强。比如从过去的Dalvik变成ART，以后也有可能有新的虚拟机，就需要针对新的虚拟机运行时机制进行适配

# 集成步骤

## 初始化Andfix

1. 添加依赖

```java
dependencies {
    api 'com.alipay.euler:andfix:0.5.0@aar'
}
```

2. 创建`Andfix`管理类，主要包括了**初始化Andfix**和**加载patch文件**两个方法

```java
public class AndFixPatchManager {

    private static AndFixPatchManager mInstance = null;

    private static PatchManager mPatchManager = null;

    public static AndFixPatchManager getInstance() {
        if (mInstance == null) {
            synchronized (AndFixPatchManager.class) {
                if (mInstance == null) {
                    mInstance = new AndFixPatchManager();
                }
            }
        }
        return mInstance;
    }

    //初始化AndFix方法
    public void initPatch(Context context) {
        mPatchManager = new PatchManager(context);
        mPatchManager.init(Utils.getVersionName(context));
        mPatchManager.loadPatch();
    }

    //加载我们的patch文件
    public void addPatch(String path) {
        try {
            if (mPatchManager != null) {
                mPatchManager.addPatch(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

3. 在`Application`中初始化`Andfix`

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initAndFix();
    }

    private void initAndFix() {
        AndFixPatchManager.getInstance().initPatch(this);
    }
}
```

4. 加载patch文件，这里我们主动通过一个点击事件来触发patch文件的加载，实际开发中可以设计自己的特定时机进行apatch文件的加载

```java
public void fixBug(View view) {
    AndFixPatchManager.getInstance().addPatch(getPatchName());
}

//构造patch文件名
private String getPatchName() {
    return mPatchDir.concat("fixbug").concat(".apatch");
}
```

## 模拟Bug产生

```java
public class Utils {
    /**
     * 模拟产生bug方法
     */
    public static void printLog() {
        String error = null;
      	//NullPointException
        Log.e("geekholt", error);
    }
}
```

使用`./gradlew assembleRelease`构建出一个带bug的apk，命名为**app-release-bug.apk**（**注意apk是需要签名的，因为后面构建apatch的时候会用到签名文件**）

将apk通过`adb install`安装到手机上，验证该apk是存在bug的

## 修复Bug

```java
public class Utils {
    /**
     * 模拟产生bug方法
     */
    public static void printLog() {
        String error = "Hello World!";
        Log.e("geekholt", error);
    }
}
```

修复bug后，同样构建出一个修复bug后的apk，命名为**app-release-fixbug.apk**

## 生成apatch文件

这里需要借助一个工具，可以到[官网](https://github.com/alibaba/AndFix)下载

![https://upload-images.jianshu.io/upload_images/10992781-f22c522f9a18fd26.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240](https://upload-images.jianshu.io/upload_images/10992781-f22c522f9a18fd26.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

下载解压后会出现三个文件

![https://upload-images.jianshu.io/upload_images/10992781-18ed3e4394f99caa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240](https://upload-images.jianshu.io/upload_images/10992781-18ed3e4394f99caa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

将**app-release-bug.apk**、**app-release-fixbug.apk**、和apk签名文件**sign.jks**一起放到文件夹中，再创建一个output目录用于存放apkpatch文件，最终目录结构如下所示

![https://upload-images.jianshu.io/upload_images/10992781-643210281201e877.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240](https://upload-images.jianshu.io/upload_images/10992781-643210281201e877.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


`cd`进入该文件目录，通过`./apkpatch.sh`查看如何使用

```
➜  apkpatch-1.0.3 ./apkpatch.sh
ApkPatch v1.0.3 - a tool for build/merge Android Patch file (.apatch).
Copyright 2015 supern lee <sanping.li@alipay.com>

usage: apkpatch -f <new> -t <old> -o <output> -k <keystore> -p <***> -a <alias> -e <***>
 -a,--alias <alias>     alias.
 -e,--epassword <***>   entry password.
 -f,--from <loc>        new Apk file path.
 -k,--keystore <loc>    keystore path.
 -n,--name <name>       patch name.
 -o,--out <dir>         output dir.
 -p,--kpassword <***>   keystore password.
 -t,--to <loc>          old Apk file path.

usage: apkpatch -m <apatch_path...> -k <keystore> -p <***> -a <alias> -e <***>
 -a,--alias <alias>     alias.
 -e,--epassword <***>   entry password.
 -k,--keystore <loc>    keystore path.
 -m,--merge <loc...>    path of .apatch files.
 -n,--name <name>       patch name.
 -o,--out <dir>         output dir.
 -p,--kpassword <***>   keystore password.
```

使用命令构建apatch文件

```
./apkpatch.sh -f app-release-fixbug.apk -t app-release-bug.apk -o output/ -k sign.jks -p 12345678 -a sign_alias -e 12345678
```

运行结果如下所示，提示我们printLog已经被修改，说明apatch文件构建成功。进入到output文件，可以看到apacth文件

```
add modified Method:V  printLog()  in Class:Lcom/geekholt/andfix/Utils;
```

## 安装apatch文件到手机

在生产环境中，我们封装相应的网络请求，来将apatch安装到手机上的指定目录中。这里为了模拟方便，直接通过`adb push`命令把apatch文件安装到手机

```
adb push /Users/geekholt/Desktop/apkpatch-1.0.3/output/app-release-fixbug-20b6ba47703502921f7649df39a7c5f7.apatch /storage/emulated/0/Android/data/com.geekholt.andfix/cache/apatch/fixbug.apatch
```

点击修复bug，会执行`patchManager.addPatch`，完成bug修复

# 生产环境下自动完成bug修复思路

前面说的过程主要是对Andfix的能力进行一个测试，主要是通过adb push将patch安装到手机的指定目录中，再通过手动的方式加载apatch文件

下面提供一个自动完成下载和安装apatch的思路，可以在`MainActivity`中启动`AndFixService`完成全过程

```java
/**
 * @author 吴灏腾
 * @date 2020/5/27
 * @describe 修复bug服务，包括patch文件下载功能和patch文件加载功能
 */
public class AndFixService extends Service {
    private static final String TAG = AndFixService.class.getSimpleName();
    private static final String FILE_END = ".apatch";
    private static final int UPDATE_PATCH = 0x02;
    private static final int DOWNLOAD_PATCH = 0x01;

    private BasePatch mBasePatchInfo;
    //patch文件存放文件夹路径
    private String mPatchFileDir;
    //patch文件真实路径，mPatchFile = mPatchFileDir + System.currentTimeMillis() + .apatch
    private String mPatchFile;
    private Handler mHandler = new AndFixHandler(this);

    private static class AndFixHandler extends Handler {
        private final WeakReference<AndFixService> mService;

        public AndFixHandler(AndFixService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mService.get() == null) {
                return;
            }
            switch (msg.what) {
                case UPDATE_PATCH:
                    mService.get().checkPatchUpdate();
                    break;
                case DOWNLOAD_PATCH:
                    mService.get().downloadPatch();
                    break;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.sendEmptyMessage(UPDATE_PATCH);
        return START_NOT_STICKY;
    }

    /**
     * 完成文件目录的构造
     */
    private void init() {
        mPatchFileDir = getExternalCacheDir().getAbsolutePath() + "/apatch/";
        File patchDir = new File(mPatchFileDir);

        try {
            if (patchDir == null || !patchDir.exists()) {
                patchDir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    /**
     * 检查服务器是否有patch文件
     */
    private void checkPatchUpdate() {
        RequestCenter.requestPatchUpdateInfo(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mBasePatchInfo = (BasePatch) responseObj;
                ////不为空则表明有更新
                if (!TextUtils.isEmpty(mBasePatchInfo.data.downloadUrl)) {
                    //下载patch文件
                    mHandler.sendEmptyMessage(DOWNLOAD_PATCH);
                } else {
                    stopSelf();
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                stopSelf();
            }
        });
    }

    /**
     * 完成patch文件的下载后，自动addPatch
     */
    private void downloadPatch() {
        //初始化patch文件下载路径
        mPatchFile = mPatchFileDir.concat(String.valueOf(System.currentTimeMillis())).concat(FILE_END);

        RequestCenter.downloadFile(mBasePatchInfo.data.downloadUrl, mPatchFile,
                new DisposeDownloadListener() {
                    @Override
                    public void onProgress(int progrss) {
                        Log.d(TAG, "current progedss: " + progrss);
                    }

                    @Override
                    public void onSuccess(Object responseObj) {
                        //将我们下载好的patch文件添加到我们的andfix中
                        AndFixPatchManager.getInstance().addPatch(mPatchFile);
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        stopSelf();
                    }
                });
    }
}
```


