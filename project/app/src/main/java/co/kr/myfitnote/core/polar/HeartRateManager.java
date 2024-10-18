package co.kr.myfitnote.core.polar;

import static co.kr.myfitnote.core.utils.DateConverter.getAgeFromBirthYear;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiCallback;
import com.polar.sdk.api.PolarBleApiDefaultImpl;
import com.polar.sdk.api.errors.PolarInvalidArgument;
import com.polar.sdk.api.model.PolarDeviceInfo;
import com.polar.sdk.api.model.PolarHrData;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import co.kr.myfitnote.account.data.model.Client;

/**
 * Polar Heart Rate Manager
 */
public class HeartRateManager {
    private static final String TAG = "HeartRateManager";

    private Context context;
    private TextView tv;
    private PolarBleApi api;
    private String deviceID = "";
    private ArrayList<Integer> heartRateList = new ArrayList<>();
    private Client client = null;
    private ProgressBar progressBar = null;
    private int lastHeartRate = 0;
    private int userMaxHeartRate = 220;

    public HeartRateManager(
            Context context,
            TextView tv,
            String deviceID
    ){
        this.context = context;
        this.tv = tv;
        this.deviceID = deviceID;
    }

    public HeartRateManager(
            Context context,
            TextView tv,
            String deviceID,
            Client client
    ){
        this.context = context;
        this.tv = tv;
        this.deviceID = deviceID;
        this.client = client;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void attachProgressBar(ProgressBar progressBar){
        this.progressBar = progressBar;
        // this.progressBar.setMin(0);
        if (client != null) {
            int age = getAgeFromBirthYear(this.client.getBirth_date().substring(0, 4));
            userMaxHeartRate = 220 - age;
            Log.e(TAG, "max HR" + " " + userMaxHeartRate);
            this.progressBar.setMax(userMaxHeartRate);
        }else{
            this.progressBar.setMax(220);
        }
    }

    public void setUp(){
        api = PolarBleApiDefaultImpl.defaultImplementation(context, PolarBleApi.ALL_FEATURES);
        // connect to device
        try {
            api.connectToDevice(deviceID);
        } catch (PolarInvalidArgument e) {
            Log.e(TAG, "PolarInvalidArgument: " + e.getMessage());
//            throw new RuntimeException(e);
        }

        // set api callback
        api.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void batteryLevelReceived(@NonNull String identifier, int level) {
                super.batteryLevelReceived(identifier, level);
            }

            @Override
            public void blePowerStateChanged(boolean powered) {
                super.blePowerStateChanged(powered);
            }

            @Override
            public void deviceConnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                super.deviceConnected(polarDeviceInfo);
                Log.e(TAG, "deviceConnected: " + polarDeviceInfo.getDeviceId());
            }

            @Override
            public void deviceConnecting(@NonNull PolarDeviceInfo polarDeviceInfo) {
                super.deviceConnecting(polarDeviceInfo);
                Log.e(TAG, "deviceConnecting: " + polarDeviceInfo.getDeviceId());
                updateHeartRate(0);
            }

            @Override
            public void deviceDisconnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                super.deviceDisconnected(polarDeviceInfo);
                Log.e(TAG, "deviceDisconnected: " + polarDeviceInfo.getDeviceId());
                updateHeartRate(0);
            }

            @Override
            public void disInformationReceived(@NonNull String identifier, @NonNull UUID uuid, @NonNull String value) {
                super.disInformationReceived(identifier, uuid, value);
            }

            @Override
            public void hrFeatureReady(@NonNull String identifier) {
                super.hrFeatureReady(identifier);
                Log.e(TAG, "hrFeatureReady: " + identifier);
            }

            @Override
            public void hrNotificationReceived(@NonNull String identifier, @NonNull PolarHrData data) {
                super.hrNotificationReceived(identifier, data);
                Log.e(TAG, "hrNotificationReceived: " + data.getHr());
                updateHeartRate(data.getHr());
                heartRateList.add(data.getHr());
            }

            @Override
            public void polarFtpFeatureReady(@NonNull String identifier) {
                super.polarFtpFeatureReady(identifier);
            }

            @Override
            public void sdkModeFeatureAvailable(@NonNull String identifier) {
                super.sdkModeFeatureAvailable(identifier);
            }

            @Override
            public void streamingFeaturesReady(@NonNull String identifier, @NonNull Set<? extends PolarBleApi.DeviceStreamingFeature> features) {
                super.streamingFeaturesReady(identifier, features);
            }
        });
    }

    public void cleanUp(){
        if (api != null) {
            try {
                api.disconnectFromDevice(deviceID);
            } catch (PolarInvalidArgument e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateHeartRate(int heartRate){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                tv.setText(String.valueOf(heartRate));
                if (progressBar != null) {
                    progressBar.setProgress(heartRate);
                }
            }
        });
        lastHeartRate = heartRate;
    }

    public int getLastHeartRate(){
        /**
         * 현재 심박수 값 반환
         */
        return lastHeartRate;
    }

    public int getUserMaxHeartRate(){
        /**
         * 사용자 최대 심박수 값 반환
         */
        return userMaxHeartRate;
    }

    public ArrayList<Integer> getHeartRateList() {
        return heartRateList;
    }

    public int getAverageHeartRate(){
        int sum = 0;
        for(int i = 0; i < heartRateList.size(); i++){
            sum += heartRateList.get(i);
        }

        try{
            return sum / heartRateList.size();
        } catch (ArithmeticException e){
            return 0;
        }
    }
}
