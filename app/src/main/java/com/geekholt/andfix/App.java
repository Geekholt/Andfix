package com.geekholt.andfix;

import android.app.Application;

/**
 * @author 吴灏腾
 * @date 2020/5/26
 */
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
