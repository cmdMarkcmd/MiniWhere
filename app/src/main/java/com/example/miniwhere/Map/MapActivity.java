package com.example.miniwhere.Map;

import static com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.example.miniwhere.databinding.ActivityMapBinding;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements TencentLocationListener, LocationSource {

    private ActivityMapBinding binding;
    private TencentMap tencentMap;
    private TencentLocationManager locationManager;
    private TencentLocationRequest locationRequest;
    private OnLocationChangedListener locationChangedListener;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float mAngle;
    private Context context;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION: {
                    float x = event.values[0];
                    x += getScreenRotationOnPhone(context);
                    x %= 360.0F;
                    if (x > 180.0F)
                        x -= 360.0F;
                    else if (x < -180.0F)
                        x += 360.0F;

                    if (Math.abs(mAngle - x) < 3.0f) {
                        break;
                    }
                    mAngle = Float.isNaN(x) ? 0 : x;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        fullScreen(this);
        context = (Context) this;
        InitView();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //????????????
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);//????????????code
        }
    }


    void RegisterSensor(){
        sensorManager.registerListener(sensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_FASTEST);

    }
    void UnregisterSensor(){
        if(sensorEventListener!=null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
    void InitSensor(){
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor == null) return;
    }



    private void initLocation(){
        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);
        //????????????????????????????????????, ???????????????????????????????????????
        locationManager = TencentLocationManager.getInstance(this);
        //??????????????????
        locationRequest = TencentLocationRequest.create();
        //??????????????????????????????????????????????????????3s
        locationRequest.setInterval(100);
        init(locationRequest);
    }
    private void init(TencentLocationRequest request) {
        Context context = this;
        TencentLocationListener listener = (TencentLocationListener) this;
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(request, listener);
        if (error == 0) {
            Log.v("this", "??????????????????????????????");
        } else {
            Log.d("this", "??????????????????????????????");
        }
    }

    public static int getScreenRotationOnPhone(Context context) {
        final Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;

            case Surface.ROTATION_90:
                return 90;

            case Surface.ROTATION_180:
                return 180;

            case Surface.ROTATION_270:
                return -90;
        }
        return 0;
    }


    private void InitView() {
        checkPermission();
        initLocation();
        InitSensor();

        tencentMap = binding.map.getMap();
        //??????????????????????????????
        tencentMap.setLocationSource(this);
        //?????????
        tencentMap.getUiSettings().setCompassEnabled(true);
        //????????????????????????
        tencentMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {

        if(i == TencentLocation.ERROR_OK ){
//            Log.d("Loa","QWQ");
            Location location = new Location(tencentLocation.getProvider());
            //???????????????
            location.setLatitude(tencentLocation.getLatitude());
            location.setLongitude(tencentLocation.getLongitude());
            //??????????????????????????????????????????????????????????????????????????????
            location.setAccuracy(tencentLocation.getAccuracy());
            //??????????????????????????????????????? tencentLocation.getBearing() ????????? gps ?????????????????????
            location.setBearing((float) mAngle);
            //??????????????????????????????
            locationChangedListener.onLocationChanged(location);
        }

    }




   @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationChangedListener = onLocationChangedListener;

    }

    @Override
    public void deactivate() {
        //????????????????????????????????????????????????????????????????????????
        locationManager.removeUpdates(this);
        locationManager = null;
        locationRequest = null;
        locationChangedListener=null;
    }



    @Override
    protected void onStart() {
        super.onStart();
        binding.map.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.map.onResume();
        RegisterSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.map.onPause();
        UnregisterSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.map.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.map.onDestroy();
        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
        locationManager.removeUpdates((TencentLocationListener) this);
    }
    /**
     * ??????????????????????????????????????????
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x????????????????????????????????????????????????????????????????????????????????????
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //?????? flag ??????????????????????????????????????????????????????????????????????????????
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //????????????????????????????????????
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