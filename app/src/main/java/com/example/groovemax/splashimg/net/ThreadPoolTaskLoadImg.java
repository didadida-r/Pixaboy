package com.example.groovemax.splashimg.net;

import android.util.Log;

import com.example.groovemax.splashimg.Application.MyApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 */
public class ThreadPoolTaskLoadImg extends ThreadPoolTask{

    private static final String TAG = "debug";

    private CallBack callBack;
    private String httpArg;

    public ThreadPoolTaskLoadImg(String httpArg, CallBack callBack) {
        this.callBack = callBack;
        this.httpArg = httpArg;
    }

    @Override
    public void run() {
        //降低优先级
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);

        String result = NetHelper.sendByGet(MyApplication.TOKEN_URL_PIXABAY, httpArg);
        if(callBack != null) {
            callBack.onReady(result);
        }
        Log.v(TAG, result);

    }

    public interface CallBack {
        void onReady(String result);
    }

}
