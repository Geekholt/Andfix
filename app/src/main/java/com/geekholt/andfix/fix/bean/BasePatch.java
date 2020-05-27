package com.geekholt.andfix.fix.bean;

import java.io.Serializable;

/**
 * @author 吴灏腾
 * @date 2020/5/26
 *
 */
public class BasePatch implements Serializable {
    public int ecode;
    public String emsg;
    public PatchInfo data;
}
