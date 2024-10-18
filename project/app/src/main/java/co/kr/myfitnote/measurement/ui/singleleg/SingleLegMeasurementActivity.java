package co.kr.myfitnote.measurement.ui.singleleg;

import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;
import static co.kr.myfitnote.core.mlkit.Variable.POSE_DETECTION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
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
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.MySoundPool;
import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.client.interfaces.PoseDataListener;
import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.mlkit.posedetector.CustomPoseGraphic;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.MLkitCameraManager;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;
import co.kr.myfitnote.measurement.data.Variable;
import co.kr.myfitnote.measurement.ui.common.MeasurementResultFragment;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.HandRaise;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.SingLegStance;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.views.SingleLegStanceTestResultActivity;
import co.kr.myfitnote.views.fragments.ReadyFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceSettingFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceStartedFragment;
import co.kr.myfitnote.views.pose.PoseMeasurementActivity;
import io.sentry.Sentry;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 신체기능검사 - 외발서기
 */
public class SingleLegMeasurementActivity extends AppCompatActivity implements MeasurementEventListener, PoseDataListener {
    static final String TAG = "SingleLegMeasurementActivity";

    private final int RUNNING_PENDDING = 1;
    private final int RUNNING_READY = 2;
    private final int RUNNING_START = 3;
    private final int RUNNING_PAUSE = 4;
    private int runningStatus = RUNNING_PENDDING;

    private enum SINGLE_LEG_STANCE_STATUS{
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
    private SINGLE_LEG_STANCE_STATUS status = SINGLE_LEG_STANCE_STATUS.SETTING;

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

    private Exercise exercise;

    private User user;
    private Client client;
    private Bundle bundle;

    private Bundle settingBundle;
    private SingLegStance singLegStance;
    private MySoundPool failSoundPool;

    private int second = 30000;
    private boolean isLeft = true;
    private long lastDropTime = 0;
    private final long DEBOUNCE_TIME = 500;
    private boolean playVoice = false;

    private Bitmap bitmap;
    private boolean isDrawLine = false;
    private ImageView overlayImageView;
    String lineColor = "#00b0a6";

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    // 체험 여부
    private boolean isExperience = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_leg_measurement);
        bundle = getIntent().getExtras();

        // 체험하기 여부, 체험하기의 경우 서버 데이터를 전송하지 않음
        isExperience = bundle.getBoolean("isExperience", false);
        Log.e(TAG, isExperience + "<= isExperience");

        SoundManager.initSounds(this);

        initComponents();

        user = PreferencesManager.getInstance(this).getUser();
        // 클라이언트
        client = new Client();
        client.setName(bundle.getString("name"));
        client.setPhone(bundle.getString("phone"));

        double threshold = getIntent().getDoubleExtra("threshold", 0.2);
        singLegStance = new SingLegStance(threshold);
        failSoundPool = new MySoundPool(getApplicationContext(), R.raw.knee_up);

        exercise = new Exercise();
        exercise.add(singLegStance);

        managerFragment = new ManagerFragment();
        managerFragment.add(new SingleLegStanceSettingFragment("외발서기"));
        managerFragment.add(new MeasurementResultFragment(true, isExperience));

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
        mlkitCameraManager = new MLkitCameraManager(this, cameraSource, preview, graphicOverlay);
    }

    @Override
    public void onFragmentChange(int fragmentNum) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getFragment(fragmentNum)).commit();
    }

    @Override
    public void nextFragment() {
        int index = managerFragment.getIndex() + 1;
        if(index < managerFragment.getSize()) {
            managerFragment.setIndex(index);
            if (getSupportFragmentManager().isDestroyed()) return;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public void nextFragment(Bundle bundle) {
        int index = managerFragment.getIndex();
        if(index < managerFragment.getSize()) {
            if(managerFragment.getCurrentFragment() instanceof SingleLegStanceSettingFragment) {
                status = SINGLE_LEG_STANCE_STATUS.EXERCISE_READY;
                index += 1;
            }
            managerFragment.setIndex(index);
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();

        }
    }

    @Override
    public void setOptions(Bundle bundle) {
        if (managerFragment.getCurrentFragment() instanceof SingleLegStanceSettingFragment){
            status = SINGLE_LEG_STANCE_STATUS.EXERCISE_READY;
            settingBundle = bundle;
            String leg = settingBundle.getString("leg");
            singLegStance.setSide(leg);
            isLeft = (leg.equals("왼발")) ? true : false;
            // Remove fragment
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
    public void setStatus(int status) {}

    @Override
    public void sendDataToServer() {
        sendMeasurementData();
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void restart() {
        singLegStance.setFailCount(0);
        singLegStance.setStatus(SingLegStance.EXERCISE_READY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getSupportFragmentManager().beginTransaction().detach(managerFragment.getCurrentFragment()).commitNow();
            getSupportFragmentManager().beginTransaction().attach(managerFragment.getCurrentFragment()).commitNow();
        } else {
            getSupportFragmentManager().beginTransaction().detach(managerFragment.getCurrentFragment()).attach(managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public void pause() {

        if(runningStatus == RUNNING_PAUSE){
            runningStatus = RUNNING_START;
            singLegStance.setStatus(SingLegStance.EXERCISE_READY);
        }else{
            runningStatus = RUNNING_PAUSE;
        }
    }

    @Override
    public boolean isPause(){
        return false;
    }

    private void sendMeasurementData(){
        /**
         * 측정 정보 저장
         */

        // 고객 측정 정보 데이터 모델 생성
        ClientMeasurementResultData result = new ClientMeasurementResultData();
        result.setName(client.getName());
        result.setPhone(client.getPhone());
        result.setExercise(singLegStance.getLabel());
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
                        Log.e(Variable.TAG, "onFailure => " + t.getMessage());
                        Sentry.captureMessage(t.getMessage());
                    }
                });
    }

    @Override
    public void onPoseDataReceived(Pose poseData, GraphicOverlay graphicOverlay) {
        if (poseData == null){
            return;
        }
    }

    public void startCount(int time, Runnable onFinishTask, int delaySeconds){

        // if countDownTimer is already running, break this method
        if (countDownTimer != null){
            return;
        }

        // Convert time to milliseconds
        long timeInMillis = time * 1000;

        // Initialize the ProgressBar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax((int) timeInMillis);

        // Create a new CountDownTimer
        countDownTimer = new CountDownTimer(timeInMillis, 100) {
            int lastPlayedSecond = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                // This callback is invoked on every tick of the timer (every 1000 milliseconds in this case)
                int second = (int) (millisUntilFinished / 1000);

                if (lastPlayedSecond != second){
                    lastPlayedSecond = second;
                    SoundManager.playSecond(second, getApplicationContext());
                }
                // if remaining time is less than 0.10 second, show 0.00
                if (millisUntilFinished < 100){
                    seconds_view.setText("0");
                }else {
                    // set text to seconds_view as "00:00"
                    seconds_view.setText(String.format("%d", millisUntilFinished / 1000));
//                    seconds_view.setText(String.format("%.2f", millisUntilFinished / 1000.0));
                }

                // Update the progress of the ProgressBar
                progressBar.setProgress((int) (timeInMillis - millisUntilFinished));
            }

            @Override
            public void onFinish() {
                // This callback is invoked when the timer finishes
                Log.d(TAG, "Countdown finished");
                onFinishTask.run();
                countDownTimer = null;
            }
        };

        // if delayTime is not 0, start countDownTimer after delayTime
        if (delaySeconds != 0){
            delaySeconds = delaySeconds * 1000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start the timer after 3 seconds
                    countDownTimer.start();
                }
            }, delaySeconds); // Delay in milliseconds
        }else{
            // Start the timer
            countDownTimer.start();
        }
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
}