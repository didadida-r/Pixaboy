package com.example.groovemax.splashimg.Application;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.groovemax.splashimg.net.ThreadPoolManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 注：自定义Application需要在manifest中注册
 */
public class MyApplication extends Application{
    private static MyApplication myApplication = null;

    private static ThreadPoolManager threadPoolManager;

    private static String authorizationCode = null;
    public static final String TOKEN_URL = "https://unsplash.com/oauth/token/";//the url to get the final token
    public static final String TOKEN_URL_PIXABAY = "https://pixabay.com/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        threadPoolManager = new ThreadPoolManager(0, 10);
        threadPoolManager.start();

    }

    public static ThreadPoolManager getThreadPoolManager(){
        return threadPoolManager;
    }

    public static  MyApplication getMyApplication(){
        return myApplication;
    }

    public String getAuthorizationCode(){
        return authorizationCode;
    }

    public void setAuthorizationCode(String input){
        authorizationCode = input;
    }

}
