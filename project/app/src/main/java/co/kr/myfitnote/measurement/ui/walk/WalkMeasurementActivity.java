package co.kr.myfitnote.measurement.ui.walk;

import static co.kr.myfitnote.core.mlkit.Utils.getLeftKneeAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getRightKneeAngle;
import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;
import static co.kr.myfitnote.core.mlkit.Variable.POSE_DETECTION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.protobuf.InvalidProtocolBufferException;
import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiCallback;
import com.polar.sdk.api.PolarBleApiDefaultImpl;
import com.polar.sdk.api.errors.PolarInvalidArgument;
import com.polar.sdk.api.model.PolarDeviceInfo;
import com.polar.sdk.api.model.PolarHrData;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.client.interfaces.PoseDataListener;
import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.mlkit.posedetector.CustomPoseGraphic;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.polar.HeartRateManager;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.MLkitCameraManager;
import co.kr.myfitnote.core.utils.Metronome;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.game.GameMain;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;
import co.kr.myfitnote.measurement.data.Variable;
import co.kr.myfitnote.measurement.data.WalkYCriteria;
import co.kr.myfitnote.measurement.ui.common.MeasurementResultFragment;
import co.kr.myfitnote.measurement.ui.seatdownup.SeatDownUpMeasurementActivity;
import co.kr.myfitnote.measurement.ui.singleleg.SingleLegMeasurementActivity;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.model.SeatDownUp;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.Walk;
import co.kr.myfitnote.views.fragments.ExplanationTextFragment;
import co.kr.myfitnote.views.fragments.ExplanationVideoFragment;
import co.kr.myfitnote.views.fragments.TestStartedFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceSettingFragment;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WalkMeasurementActivity extends AppCompatActivity implements MeasurementEventListener, PoseDataListener {
    private String TAG = "WalkMeasurementActivity";

    private enum MEASUREMENT_STATUS{
        SETTING,
        COUNTING,
        EXERCISE_READY,
        CALIBRATION,
        CALIBRATIONING,
        EXERCISE_START,
        EXERCISE_STARTING,
        EXERCISE_PAUSE,
        EXERCISE_FINISH
    }
    private MEASUREMENT_STATUS status = MEASUREMENT_STATUS.SETTING;

    private String selectedModel = POSE_DETECTION;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private ManagerFragment managerFragment;
    private MLkitCameraManager mlkitCameraManager;

    private NoticeView notice_view;
    private TextView seconds_view;
    private CountDownTimer countDownTimer;
    private PausableCountDownTimer measurementTimer;
    private TextView tv_count;

    private Exercise exercise;
    private WalkYCriteria walkYCriteria;

    private Bundle bundle;
    private User user;
    private Client client;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    private int time = 120;
    private long lastSaveTime = 0;
    private final long DEBOUNCE_TIME = 200;
    private long startTimesMS = -1;

    private boolean isDrawLine = false;
    private Bitmap bitmap;
    ImageView overlayImageView;

    String deviceID;
    private TextView hr_board_text;
    private HeartRateManager heartRateManager;
    private ProgressBar hr_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_measurement);
        deviceID = PreferencesManager.getInstance(this).getData("BLE_DEVICE_ID") == null ? "" : PreferencesManager.getInstance(this).getData("BLE_DEVICE_ID");
        bundle = getIntent().getExtras();
        walkYCriteria = new WalkYCriteria();
        walkYCriteria.setThreshold(0.3f);
        SoundManager.initSounds(this);

        initComponents();
        // 기업 관리자
        user = PreferencesManager.getInstance(this).getUser();
        // 클라이언트
        client = new Client();
        client.setName(bundle.getString("name"));
        client.setPhone(bundle.getString("phone"));
        client.setBirth_date(bundle.getString("birth"));

        hr_board_text = findViewById(R.id.hr_board_text);
        hr_progressBar = findViewById(R.id.hr_progressBar);
        heartRateManager = new HeartRateManager(this, hr_board_text, deviceID, client);
        heartRateManager.setUp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            heartRateManager.attachProgressBar(hr_progressBar);
        }

        // fragment 등록
        managerFragment = new ManagerFragment();
        managerFragment.add(new ExplanationVideoFragment("심폐기능검사", "walk",120, "심폐 기능 검사는 2분 동안 진행됩니다.")); // index: 4
        managerFragment.add(new MeasurementResultFragment());

        int angleThreshold = getIntent().getIntExtra("threshold", 135);
        int fullAngleThreshold = getIntent().getIntExtra("fullAngleThreshold", 165);

        //운등 등록
        exercise = new Exercise();
        exercise.add(walkYCriteria);
//        exercise.add(new Walk(angleThreshold, fullAngleThreshold));

        Toolbar titleBar = findViewById(R.id.title_bar);
        titleBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        overlayImageView = findViewById(R.id.overlay_imageView);
        setSupportActionBar(titleBar);
        // start first fragment when activity created.
        onFragmentChange(0);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mlkitCameraManager.createCameraSource(selectedModel);
        mlkitCameraManager.startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraSource != null) {
            cameraSource.release();
        }

        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }

        SoundManager.cleanUp();
    }

    private void initComponents(){
        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
        notice_view = findViewById(R.id.notice_view);
        seconds_view = findViewById(R.id.seconds_view);
        tv_count = findViewById(R.id.tv_count);
        mlkitCameraManager = new MLkitCameraManager(this, cameraSource, preview, graphicOverlay);
    }


    @Override
    public void onFragmentChange(int fragmentNum) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getFragment(fragmentNum)).commit();
    }

    @Override
    public void nextFragment() {
        int index = managerFragment.getIndex() + 1;
        if(index < managerFragment.getSize() ) {
            managerFragment.setIndex(index);
            if (getSupportFragmentManager().isDestroyed()) return;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public void nextFragment(Bundle bundle) {
        int index = managerFragment.getIndex() + 1;
        if(index < managerFragment.getSize() ) {
            if(managerFragment.getCurrentFragment() instanceof ExplanationVideoFragment) {
                index += 1;
            }
            managerFragment.setIndex(index);
        }
    }

    @Override
    public void setOptions(Bundle bundle) {
        if (managerFragment.getCurrentFragment() instanceof ExplanationVideoFragment) {
            status = MEASUREMENT_STATUS.EXERCISE_READY;
            time = bundle.getInt("time");
            // Set seconds to test result
            exercise.getTestResult().setSecond(time);

            // remove setting fragment
            getSupportFragmentManager().beginTransaction().remove(managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public TestResult getTestResult() {

        return exercise.getTestResult();
    }

    @Override
    public Exercise getExercise() {
        return exercise;
    }

    @Override
    public void setStatus(int status) {
        exercise.setStatus(status);
    }

    @Override
    public void sendDataToServer() {
        sendMeasurementData();
    }

    @Override
    public Client getClient() {
        return client;
    }

    /**
     * 기능검사 새로시작
     */
    @Override
    public void restart() {

        exercise.setCount(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getSupportFragmentManager().beginTransaction().detach(managerFragment.getCurrentFragment()).commitNow();
            getSupportFragmentManager().beginTransaction().attach(managerFragment.getCurrentFragment()).commitNow();
        } else {
            getSupportFragmentManager().beginTransaction().detach(managerFragment.getCurrentFragment()).attach(managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public void pause() {
        exercise.setPause(!exercise.isPause());
    }

    @Override
    public boolean isPause() {
        return exercise.isPause();
    }

    private void sendMeasurementData(){
        /**
         * 측정 정보 저장
         */

        // 고객 측정 정보 데이터 모델 생성
        ClientMeasurementResultData result = new ClientMeasurementResultData();
        result.setName(client.getName());
        result.setPhone(client.getPhone());
        result.setExercise(exercise.getExerciseLabel());
        result.setTestResult(getTestResult());

        // 고객 측정 정보 전송
        service.createClientMeasurement("Token " + user.getToken(), result)
                .enqueue(new Callback<ArrayList<Client>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Client>> call, Response<ArrayList<Client>> response) {
                        Log.e(Variable.TAG, response.message());
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Client>> call, Throwable t) {
                        Log.e(Variable.TAG, t.getMessage());
                        Sentry.captureMessage(t.getMessage());
                    }
                });
    }

    private void drawLine(float y, String color){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.parseColor(color));
                paint.setStrokeWidth(30);
                canvas.drawLine(0, y, overlayImageView.getWidth(), y, paint);

                // Load your .png image as a Bitmap
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_criteria_line);

                // Define the size of the scaled bitmap
                int scaledWidth = originalBitmap.getWidth() / 2;  // Scale down to half the original width
                int scaledHeight = originalBitmap.getHeight() / 2;  // Scale down to half the original height

                // Create the scaled bitmap
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);

                // Draw the scaled image on the canvas at the left side of the line
                canvas.drawBitmap(scaledBitmap, 0, y - scaledBitmap.getHeight() / 2, null);

                overlayImageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void onPoseDataReceived(Pose poseData, GraphicOverlay graphicOverlay) {

        if (poseData == null){
            return;
        }

    }
}