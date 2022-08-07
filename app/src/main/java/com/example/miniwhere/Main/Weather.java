package com.example.miniwhere.Main;


import android.content.Context;
import android.util.Log;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class Weather {
    private MainActivity mainActivity;
    public Weather(Context context) {
        mainActivity = (MainActivity) context;
    }


    public void initWeather(){
            requestData();//这块填的是城市编码
    }

    public void requestData() {

//        String textUrl = "http://www.weather.com.cn/data/cityinfo/" + "101200106" + ".html";
        if (mainActivity.getProvince().equals("null")) return;
        String textUrl = "https://wis.qq.com/weather/common?source=pc&weather_type=observe|forecast_24h|alarm&";
        textUrl += "province="+mainActivity.getProvince();
        textUrl += "&city="+mainActivity.getCity();
        textUrl += "&county="+mainActivity.getCountry();
        Log.d("Wea",textUrl);
        HttpUtil.sendOkHttpRequest(textUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //线程回到主线程，否则系统交出现错调
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //数据抓取失败时，检查Mainfest 中 Application 节点中     android:usesCleartextTraffic="true"
                        //此问题由于 "http:" 引起
                        Log.d("Wea","Failed");
//                        Toast.makeText(Activity名.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                //线程回到主线程，否则系统交出现错调
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseText !=null){
//                            mainActivity.setLocation("1",responseText);
                            String text = responseText;
                            Map<String,Object> map2 = getTodayWeather(text);
                            text=(map2.get("degree") + "℃" );
                            Log.d("Wea",text);
                            mainActivity.setTemperature(text);
                            text=(map2.get("humidity") + "%");
                            mainActivity.setWet("湿度",text);
                            text=(map2.get("weather") +"");
                            String textlong = "今日 ";
                            String night = (String) map2.get("night");
                            String day = (String) map2.get("day");
                            assert day != null;
                            if (!day.equals(night)) textlong+=day+"转"+night;
                            else textlong +=day;
                            textlong+="  "+map2.get("max")+"°/"+map2.get("min")+"°";
                            mainActivity.setWeather(text,textlong);
                            Log.d("Wea","获取成功");
                        } else {
                            Log.d("Wea","获取失败");
//                            Toast.makeText(mainActivity, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private Map<String, Object> getTodayWeather(String datas) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject jsonData = JSONObject.fromObject(datas);
        JSONObject info = jsonData.getJSONObject("data");
        JSONObject ObserveInfo = info.getJSONObject("observe");
        map.put("degree", ObserveInfo.getString("degree").toString());
        map.put("humidity",ObserveInfo.getString("humidity").toString());
        map.put("weather",ObserveInfo.getString("weather").toString());
        JSONObject info24 = info.getJSONObject("forecast_24h");
        info24 = info24.getJSONObject("1");
        map.put("day",info24.getString("day_weather").toString());
        map.put("night",info24.getString("night_weather").toString());
        map.put("max",info24.getString("max_degree").toString());
        map.put("min",info24.getString("min_degree").toString());
        return map;

    }


}