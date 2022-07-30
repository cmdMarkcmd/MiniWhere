package com.example.miniwhere.Main;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.tencent.map.geolocation.TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.miniwhere.databinding.ActivityMainBinding;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class MainActivity extends AppCompatActivity implements TencentLocationListener {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
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
                .setGpsFirst(true)
                .setAllowCache(true);
        init(locationRequest);
    }

    public void init(TencentLocationRequest request) {
        Context context = this;
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
//                Toast.makeText(this,location.getProvider(),Toast.LENGTH_SHORT).show();
                int alt = (int) location.getAltitude();
                String address = location.getAddress();
                binding.LocationIn.setText(qu+" "+stress);
                binding.latitude.setText(lat+"°");
                if (alt != 0) binding.altitude.setText(alt+"米");
                else binding.altitude.setText("GPS信号不佳，无法获取");
                binding.longitude.setText(lon+"°");
                binding.LocationNow.setText(address);
            }
        } else {
            // 定位失败
            Log.v("this", "定位失败！");
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //关闭定位监听器
        TencentLocationManager locationManager =
                TencentLocationManager.getInstance(this);
        locationManager.removeUpdates((TencentLocationListener) this);
    }



    /**
     * 获取定位的权限
     */
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                // 检查权限状态
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    /**
                     * 用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
                     * 第一次授权失败之后，退出App再次进入时，再此处重新调出允许权限提示框
                     */
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Log.d("info:", "-----get--Permissions--success--1-");
                } else {
                    /**
                     * 用户未彻底拒绝授予权限
                     * 第一次安装时，调出的允许权限提示框，之后再也不提示
                     */
                    Log.d("info:", "-----get--Permissions--success--2-");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }
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


}