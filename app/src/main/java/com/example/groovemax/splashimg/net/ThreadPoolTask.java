package com.example.groovemax.splashimg.net;

/**
 * 文件名：
 * 描述：任务单元基类
 * 作者：
 * 时间：
 */
public abstract class ThreadPoolTask implements Runnable {

    private final static String SERVER_URL = "sayhitest.applinzi.com/register";

    public ThreadPoolTask() {

    }

    public abstract void run();

    public String getURL() {
        return this.SERVER_URL;
    }
}
