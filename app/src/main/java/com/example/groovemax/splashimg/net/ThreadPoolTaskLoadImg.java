package com.example.groovemax.splashimg.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.groovemax.splashimg.Application.MyApplication;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URLEncoder;

/**
 *
 */
public class ThreadPoolTaskLoadImg extends ThreadPoolTask{

    private static final String TAG = "debug";

    private CallBack callBack;
    private String httpArg;
    private Context context;

    public ThreadPoolTaskLoadImg(String httpArg, CallBack callBack, Context context) {
        this.callBack = callBack;
        this.httpArg = httpArg;
        this.context = context;
    }

    @Override
    public void run() {
        //降低优先级
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);

        if(isNetConnected()){
            String result = NetHelper.sendByGet(MyApplication.TOKEN_URL_PIXABAY, httpArg);
            if(callBack != null) {
                callBack.onReady(result);
            }
            Log.v(TAG, result);
        }else{
            if(callBack != null)
                callBack.onReady("Net Error");
            Log.v(TAG, "Internet fail!");
        }



    }

    private boolean isNetConnected(){
        if(callBack != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null)
                return info.isAvailable();
        }
        return  false;
    }

    public interface CallBack {
        void onReady(String result);
    }

}
