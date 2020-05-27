package com.geekholt.andfix.fix.bean;

import java.io.Serializable;

/**
 * @author 吴灏腾
 * @date 2020/5/26
 */
public class PatchInfo implements Serializable {

    public String downloadUrl; //不为空则表明有更新

    public String versionName; //本次patch包的版本号

    public String patchMessage; //本次patch包含的相关信息，例如：主要做了那些改动
}
