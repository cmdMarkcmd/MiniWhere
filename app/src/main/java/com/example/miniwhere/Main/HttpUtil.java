package com.example.miniwhere.Main;
//引包
import okhttp3.OkHttpClient;
import okhttp3.Request;
//http工具类封装
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
