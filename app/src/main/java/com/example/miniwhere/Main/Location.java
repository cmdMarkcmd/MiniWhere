package com.example.miniwhere.Main;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.tencent.map.geolocation.TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class Location implements TencentLocationListener{
    private MainActivity mainActivity;
    public Location(Context context){
        mainActivity = (MainActivity) context;
    }
    public void InitLocation(){
        getLocation();
    }

    private void getLocation(){
        TencentLocationManager.setUserAgreePrivacy(true);
        initPermission();
        TencentLocationRequest locationRequest = TencentLocationRequest.create();
        locationRequest.setInterval(1000)
                .setRequestLevel(REQUEST_LEVEL_ADMIN_AREA)
                .setAllowDirection(true)
                .setIndoorLocationMode(true)
                .setGpsFirst(false)
                .setAllowCache(true);
        init(locationRequest);
    }
//
    private void init(TencentLocationRequest request) {
        Context context = mainActivity;
        TencentLocationListener listener = (TencentLocationListener) this;
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(request, listener);
        if (error == 0) {
            Log.v("this", "注册位置监听器成功！");
        } else {
            Log.d("this", "注册位置监听器失败！");
        }
    }
    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        // do your work
//        Toast.makeText(this,name+" "+String.valueOf(status),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        // TODO Auto-generated method stub
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            Log.v("this", "定位成功！");
            if (location != null) {
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                String stress = location.getStreet();
                String qu = location.getDistrict();
                String address = location.getAddress();
                mainActivity.setCity(location.getCity());
                mainActivity.setProvince(location.getProvince());
                mainActivity.setCountry(qu);
                mainActivity.setLat2Lon(lat+"°",lon+"°");
                mainActivity.setLocation(address,qu+" "+stress);
            }
            mainActivity.InitWeather();

        } else {
            // 定位失败
            Log.v("this", "定位失败！");
        }
    }

    public void CloseLocation(){
        TencentLocationManager locationManager = TencentLocationManager.getInstance(mainActivity);
        locationManager.removeUpdates((TencentLocationListener) this);
    }



    /**
     * 获取定位的权限
     */
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                // 检查权限状态
                if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    /**
                     * 用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
                     * 第一次授权失败之后，退出App再次进入时，再此处重新调出允许权限提示框
                     */
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Log.d("info:", "-----get--Permissions--success--1-");
                } else {
                    /**
                     * 用户未彻底拒绝授予权限
                     * 第一次安装时，调出的允许权限提示框，之后再也不提示
                     */
                    Log.d("info:", "-----get--Permissions--success--2-");
                    ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }
    }


}
