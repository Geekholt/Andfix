package com.geekholt.andfix.fix;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.geekholt.andfix.fix.bean.BasePatch;
import com.geekholt.andfix.network.DisposeDataListener;
import com.geekholt.andfix.network.DisposeDownloadListener;
import com.geekholt.andfix.network.RequestCenter;

import java.io.File;
import java.lang.ref.WeakReference;

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