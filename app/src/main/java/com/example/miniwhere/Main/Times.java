package com.example.miniwhere.Main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Times {
    private MainActivity mainActivity;
    public Times(Context context){
        mainActivity = (MainActivity) context;
    }
    private Calendar calendar;
    public void InitTimes(){
        FindTime();
    }

    private void FindTime(){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getNetTime(1);
            }
        });
    }
    private String getWeek(int x){
        switch (x){
            case 1:
                return "日";
            case 2:
                return "一";
            case 3:
                return "二";
            case 4:
                return "三";
            case 5:
                return "四";
            case 6:
                return "五";
            default:
                return "六";
        }
    }

    private void getNetTime(int index) {
        if (index == 1){
            URL url;//取得资源对象
            try {
                url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
                URLConnection uc = url.openConnection();//生成连接对象
                //发出连接
                uc.setReadTimeout(2000);
                uc.setConnectTimeout(2000);
                uc.connect();
                long ld = uc.getDate(); //取得网站日期时间
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(ld);
                Log.d("Time","ok");
            } catch (Exception e) {
                Log.d("Time","fail");
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
            }
        }else{
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                formatter = new SimpleDateFormat("HH:mm");
                String format = formatter.format(calendar.getTime());
                mainActivity.setTimes(format);
                formatter = new SimpleDateFormat("yyyy年MM月dd日 周");
                format = formatter.format(calendar.getTime())+getWeek(calendar.get(Calendar.DAY_OF_WEEK));
                mainActivity.setTime2(format);
            }
        });
    }

}
