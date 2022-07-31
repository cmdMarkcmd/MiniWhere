package com.example.miniwhere.Main;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.miniwhere.Map.MapActivity;
import com.example.miniwhere.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  {

    private ActivityMainBinding binding;
    private Pressure pressure = new Pressure(this);
    private Location location = new Location(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        binding.go.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        });
        pressure.InitPressure();
        location.InitLocation();
    }

    public void setPressure(String Qiya,String PressureVal){
        binding.qiya.setText(Qiya);
        binding.pressure.setText(PressureVal);
    }
    public void setAltitude(String Haiba,String AltitudeVal){
        binding.haiba.setText(Haiba);
        binding.altitude.setText(AltitudeVal);
    }
    public void setLat2Lon(String lat,String lon){
        binding.longitude.setText(lon);
        binding.latitude.setText(lat);
    }
    public void setLocation(String Local,String local){
        binding.LocationNow.setText(Local);
        binding.LocationIn.setText(local);
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

}