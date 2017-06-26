package com.hongzhen.ruixin.utils;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yuhongzhen on 2017/5/20.
 */

public class ThreadUtils {
    private static Handler mHandler=new Handler(Looper.getMainLooper());
    private static Executor mExecutor=Executors.newSingleThreadExecutor();
    public static void runOnSubThread(Runnable runnable){
        mExecutor.execute(runnable);
    }
    public static void runOnMainThread(Runnable runnable){
        mHandler.post(runnable);
    }
}
