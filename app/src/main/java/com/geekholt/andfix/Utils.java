package com.geekholt.andfix;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author 吴灏腾
 * @date 2020/5/26
 */
public class Utils {

    /**
     * 获取应用程序versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "1.0.0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 模拟产生bug方法
     */
    public static void printLog() {
        //String error = null;
        String error = "Hello World!";
        Log.e("geekholt", error);
    }

}
