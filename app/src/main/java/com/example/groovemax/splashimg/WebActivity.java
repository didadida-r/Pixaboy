package com.example.groovemax.splashimg;

import com.example.groovemax.splashimg.Application.MyApplication;
import com.example.groovemax.splashimg.net.ThreadPoolTaskLogin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 60546 on 5/5/2016.
 */
public class WebActivity extends AppCompatActivity implements ThreadPoolTaskLogin.CallBack{

    private final static String url = "https://unsplash.com/oauth/authorize" +
            "?client_id=a191a4e92a6a6159fea270720bf06da51bd1efadf13f77901c842f7a57347467" +
            "&redirect_uri=http://cn.bing.com/" +
            "&response_type=code" +
            "&scope=public+read_user+read_photos+read_collections";
    private final static String TAG = "debug";

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);

        //默认行为是使用浏览器，设置此项后都用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return true;
            }

            //get the Authorization Code
            @Override
            public void onPageFinished(WebView view, String url) {
                String resultUrl = webView.getUrl();
                String code = null;
                if (resultUrl != null) {
                    if (resultUrl.contains("code="))
                        code = resultUrl.substring(resultUrl.indexOf("code=") + 5, resultUrl.length());
                }

                if(code == null){
                    Log.v(TAG, "fail to get authorization code");
                    return;
                }
                MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLogin(code, WebActivity.this));

                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public void onReady(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            SharedPreferences sharedPreferences = getSharedPreferences("oauth2", MODE_PRIVATE);
            SharedPreferences.Editor editor =  sharedPreferences.edit();
            editor.putString("access_token", jsonObject.getString("access_token"));
            editor.putString("expires_in", jsonObject.getString("expires_in"));
            editor.putString("refresh_token", jsonObject.getString("refresh_token"));
            editor.putString("client_id", "a191a4e92a6a6159fea270720bf06da51bd1efadf13f77901c842f7a57347467");
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finish();
    }

    /*
    public static String sendByPost(){

        String result = "";
        String url = "https://unsplash.com/oauth/token/";
        try{
            //只是建立tcp连接，没有发送http请求
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            //允许输入输出
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            //建立tcp连接，所有set的设置必须在此之前完成
            urlConnection.connect();


            //传递用户名与密码,这里也可以用StringBuffer
            String data = "client_id=" + URLEncoder.encode("a191a4e92a6a6159fea270720bf06da51bd1efadf13f77901c842f7a57347467", "UTF-8")
                    + "&client_secret=" + URLEncoder.encode("794d077b397cc97f69daf7910a9b496e3106ec067cb711915d1c4c29b2fe51a3","UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode("http://cn.bing.com/", "UTF-8")
                    + "&code=" + URLEncoder.encode(MyApplication.getMyApplication().getAuthorizationCode(), "UTF-8")
                    + "&grant_type=" + URLEncoder.encode("authorization_code", "UTF-8");
            Log.v(TAG, data);
            int len;
            byte buffer[] = new byte[1024];

            //将输出流与输出数据绑定
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
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
    */


}
