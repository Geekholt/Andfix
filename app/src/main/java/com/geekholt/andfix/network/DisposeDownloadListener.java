package com.geekholt.andfix.network;

/**
 * @author 吴灏腾
 * @date 2020/5/27
 * @describe 下载文件接口
 */
public interface DisposeDownloadListener {
    public void onProgress(int progress);

    public void onSuccess(Object responseObj);

    public void onFailure(Object reasonObj);
}
