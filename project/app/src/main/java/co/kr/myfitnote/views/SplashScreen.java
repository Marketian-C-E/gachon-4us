package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

//import com.polar.sdk.api.PolarBleApiDefaultImpl;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.ui.login.LoginActivity;
import co.kr.myfitnote.lab.LabMainActivity;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    private static final int PERMISSIONS_REQUEST_CODE = 123;
    String[] appPermissions;
//    String[] appPermissions = {
//            Manifest.permission.BLUETOOTH_SCAN,
//            Manifest.permission.CAMERA,
//            Manifest.permission.BLUETOOTH_CONNECT
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                appPermissions = new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.CAMERA
                };
            }else {
                appPermissions = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.CAMERA
                };
            }
        } else {
            appPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
            };
        }

        if (checkAndRequestPermissions()) {
            startMainIntent();
        }
    }

    private boolean checkAndRequestPermissions() {
        for (String permission : appPermissions) {
            Log.e(TAG, permission + " : " + ContextCompat.checkSelfPermission(this, permission));
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, appPermissions, PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                startMainIntent();
            } else {
                Toast.makeText(this, "원활한 앱 이용을 위해서 향후에 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                startMainIntent();
            }
        }
    }

    private void startMainIntent() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                 Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
//                Intent mainIntent = new Intent(SplashScreen.this, LabMainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, 1000);
    }
}