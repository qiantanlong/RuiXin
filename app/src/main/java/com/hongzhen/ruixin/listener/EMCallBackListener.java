package com.hongzhen.ruixin.listener;

import com.hongzhen.ruixin.utils.ThreadUtils;
import com.hyphenate.EMCallBack;



/**
 * Created by yuhongzhen on 2017/5/22.
 */

public abstract class EMCallBackListener implements EMCallBack {

    public abstract void mainOnSuccess();
    public abstract void mainOnError(String s);
    @Override
    public void onSuccess() {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mainOnSuccess();
            }
        });
    }

    @Override
    public void onError(int i, final String s) {
        final String msg=s;
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mainOnError(msg);
            }
        });
    }

    @Override
    public void onProgress(int i, String s) {

    }
}
