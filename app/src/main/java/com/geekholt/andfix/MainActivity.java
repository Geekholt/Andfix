package com.geekholt.andfix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

// /storage/emulated/0/Android/data/com.geekholt.andfix/cache/apatch/
public class MainActivity extends AppCompatActivity {

    private static final String FILE_END = ".apatch";
    private String mPatchDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPatchDir = getExternalCacheDir().getAbsolutePath() + "/apatch/";
        Log.i("MainActivity", mPatchDir);
        //创建文件夹
        File file = new File(mPatchDir);
        if (file == null || file.exists()) {
            file.mkdir();
        }

    }

    //按钮点击事件1
    public void createBug(View view) {
        Utils.printLog();
    }

    //按钮点击事件2
    public void fixBug(View view) {
        AndFixPatchManager.getInstance().addPatch(getPatchName());
    }

    //构造patch文件名
    private String getPatchName() {
        return mPatchDir.concat("fixbug").concat(FILE_END);
    }
}

