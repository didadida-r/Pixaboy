package com.example.groovemax.splashimg.net;

import com.example.groovemax.splashimg.Application.MyApplication;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 文件名：
 * 描述：
 * 作者：
 * 时间：
 */
public class ThreadPoolTaskLogin extends ThreadPoolTask {
    private static final String TAG = "debug";

    private CallBack callBack;
    private String code;

    public ThreadPoolTaskLogin(String code, CallBack callBack) {
        this.callBack = callBack;
        this.code = code;
    }

    @Override
    public void run() {
        //降低优先级
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);

        try {
            String httpArg = "client_id=" + URLEncoder.encode("a191a4e92a6a6159fea270720bf06da51bd1efadf13f77901c842f7a57347467", "UTF-8")
                    + "&client_secret=" + URLEncoder.encode("794d077b397cc97f69daf7910a9b496e3106ec067cb711915d1c4c29b2fe51a3","UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode("http://cn.bing.com/", "UTF-8")
                    + "&code=" + URLEncoder.encode(code, "UTF-8")
                    + "&grant_type=" + URLEncoder.encode("authorization_code", "UTF-8");

            String result = NetHelper.sendByPost(MyApplication.TOKEN_URL, httpArg);
            if(callBack != null){
                callBack.onReady(result);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public interface CallBack {
        void onReady(String result);
    }

}
