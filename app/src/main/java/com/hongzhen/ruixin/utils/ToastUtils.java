package com.hongzhen.ruixin.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yuhongzhen on 2017/5/20.
 */

public class ToastUtils {
    private static Toast mToast;
    public static void showToast(Context context,String msg){
        if (mToast==null){
            mToast=Toast.makeText(context.getApplicationContext(),msg,Toast.LENGTH_SHORT);
        }
        //如果这个Toast已经在显示了，那么这里会立即修改文本
        mToast.setText(msg);
        mToast.show();
    }
}
