package com.example.miniwhere.Main;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.miniwhere.Map.MapActivity;
import com.example.miniwhere.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  {

    private ActivityMainBinding binding;
    private Pressure pressure = new Pressure(this);
    private Location location = new Location(this);
    private Times times = new Times(this);
    private Weather weather = new Weather(this);
    private String province = "null";
    private String city;
    private String country;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        fullScreen(this);
        binding.go.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });
        pressure.InitPressure();
        location.InitLocation();
    }
    public void InitWeather(){
        weather.initWeather();
        times.InitTimes();
    }


    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country){
        this.country = country;
    }
    public String getCountry(){
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public void setTimes(String Shijian){
        binding.Time.setText(Shijian);
    }
    public void setTime2(String Shijian){
        binding.day.setText(Shijian);
    }
    public void setPressure(String Qiya,String PressureVal){
        binding.qiya.setText(Qiya);
        binding.pressure.setText(PressureVal);
    }
    public void setWeather(String weather,String weatherToday){
        binding.WeatherIn.setText(weather);
        binding.WeatherToday.setText(weatherToday);
    }
    public void setAltitude(String Haiba,String AltitudeVal){
        binding.haiba.setText(Haiba);
        binding.altitude.setText(AltitudeVal);
    }
    public void setWet(String Shidu,String wet){
        binding.shidu.setText(Shidu);
        binding.wet.setText(wet);
    }
    public void setLat2Lon(String lat,String lon){
        binding.longitude.setText(lon);
        binding.latitude.setText(lat);
    }
    public void setLocation(String Local,String local){
        binding.LocationNow.setText(Local);
        binding.LocationIn.setText(local);
    }
    public void setTemperature(String temperature){
        binding.TemperatureIn.setText(temperature);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        location.CloseLocation();
        //关闭定位监听器
    }

    @Override
    protected void onResume(){
        super.onResume();
        pressure.RegisterPressure();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pressure.UnregisterPressure();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    // 申请成功
                    Log.d("info:", "-----get--Permissions--success--3-");

                } else {
                    // 申请失败
                    Toast.makeText(this, "请在设置中更改定位权限", Toast.LENGTH_SHORT).show();
                    Log.d("info:", "-----get--Permissions--success--4-");
                }
            }
        }
    }
    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }


}