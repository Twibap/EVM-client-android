package io.mystudy.tnn.myevmapplication.Application;

import android.util.Log;

public class Dlog {
    static final String TAG = "T.K. Yu";

    private Dlog(){}

    public static final void e(String message){
        if (BaseApplication.DEBUG) Log.e(TAG, buildLogMsg(message));
    }
    public static final void w(String message){
        if (BaseApplication.DEBUG) Log.w(TAG, buildLogMsg(message));
    }
    public static final void i(String message){
        if (BaseApplication.DEBUG) Log.i(TAG, buildLogMsg(message));
    }
    public static final void d(String message){
        if (BaseApplication.DEBUG) Log.d(TAG, buildLogMsg(message));
    }
    public static final void v(String message){
        if (BaseApplication.DEBUG) Log.v(TAG, buildLogMsg(message));
    }

    public static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java",""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);

        return sb.toString();
    }
}
