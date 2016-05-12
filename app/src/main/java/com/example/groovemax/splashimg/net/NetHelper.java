package com.example.groovemax.splashimg.net;

import android.util.Log;
import android.widget.Toast;

import com.example.groovemax.splashimg.Application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 文件名：NetHelper
 * 描述：负责建立tcp/ip连接（POST方式）
 * 作者：
 * 时间：
 */
public class NetHelper {

    final private static String TAG = "debug";

    public static String sendByPost(String httpUrl, String httpArg){
        /** 接收服务器数据 */
        String result = "";

        try{
            //只是建立tcp连接，没有发送http请求
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(httpUrl).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            //允许输入输出
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            //建立tcp连接，所有set的设置必须在此之前完成
            urlConnection.connect();

            int len;
            byte buffer[] = new byte[1024];
            //将输出流与输出数据绑定
            OutputStream os = urlConnection.getOutputStream();
            os.write(httpArg.getBytes());
            os.flush();

            //获取服务器传送过来的数据
            if(urlConnection.getResponseCode() == 200){
                InputStream in = urlConnection.getInputStream();
                //创建字节输出流对象，用于接收服务器数据
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                //定义读取的字节流长度
                while((len = in.read(buffer)) != -1){
                    //根据读入数据长度写byteOut对象
                    byteOut.write(buffer, 0 ,len);
                }
                //将数据转为字符串
                result = new String(byteOut.toByteArray());
                in.close();
                byteOut.close();
            }else {
                Log.v(TAG, "连接失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "the result" + result);
        return result;
    }

    public static String sendByGet(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if(connection.getResponseCode() == 200){
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                result = sbf.toString();
            }else{
                Log.v(TAG, "fail to load");
                result = "fail";
            }

        } catch (Exception e) {
            Log.v(TAG, "Exception");
            e.printStackTrace();
        }

        return result;
    }

}
