package com.hongzhen.ruixin.utils;

import android.util.Log;

import com.hongzhen.ruixin.main.RXApplication;


/**
 * Created by yuhongzhen on 2016/9/6.
 * 打印日志的工具类,控制LOG的输出的开关在app
 */
public class LogUtils {
    private static boolean showLog = RXApplication.isDebug;
    //过滤
    private static final String LogFilter = "ruixin-log";

    public static void i(Object objTag, Object objMsg) {
        if (showLog) {
            String msg = getMsg(objMsg);
            int strLength = msg.length();
            String tag = getTag(objTag);
            int start = 0;
            int end = 150;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(LogFilter + tag, " : " + msg.substring(start, end));
                    start = end;
                    end = end + 150;
                } else {
                    Log.i(LogFilter + tag, " : " +msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void i(Object objMsg) {
        if (showLog)
            Log.i(LogFilter + getTag("TAG"), " : " + getMsg(objMsg));
    }

    public static void e(Object objTag, Object objMsg, Throwable e) {
        if (showLog)
            Log.e(LogFilter + getTag(objTag), " : " + getMsg(objMsg), e);
    }

    private static String getTag(Object objTag) {
        String tag;
        if (objTag == null) {
            tag = "null";
        } else if (objTag instanceof String) {
            tag = (String) objTag;
        } else if (objTag instanceof Class) {
            tag = ((Class<?>) objTag).getSimpleName();    // 如果objTag不是String，则取它的类名
        } else {
            tag = objTag.getClass().getSimpleName();
        }
        return tag;
    }

    private static String getMsg(Object objMsg) {
        String msg;
        if (objMsg == null) {
            msg = "null";
        } else {
            msg = objMsg.toString();
        }
        return msg;
    }

}
