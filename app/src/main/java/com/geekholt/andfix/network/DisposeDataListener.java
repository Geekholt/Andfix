package com.geekholt.andfix.network;

/**
 * @author 吴灏腾
 * @date 2020/5/27
 * @describe 网络请求接口
 */
public interface DisposeDataListener {
    public void onSuccess(Object responseObj);

    public void onFailure(Object reasonObj);
}
