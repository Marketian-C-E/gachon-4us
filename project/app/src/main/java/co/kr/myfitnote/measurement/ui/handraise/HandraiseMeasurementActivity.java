package co.kr.myfitnote.measurement.ui.handraise;

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
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
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
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;

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
import co.kr.myfitnote.core.utils.UITimerManager;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;
import co.kr.myfitnote.measurement.data.Variable;
import co.kr.myfitnote.measurement.ui.seatdownup.SeatDownUpMeasurementActivity;
import co.kr.myfitnote.measurement.ui.walk.WalkMeasurementActivity;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.HandRaise;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.views.SingleLegStanceTestResultActivity;
import co.kr.myfitnote.views.WalkTestActivity;
import co.kr.myfitnote.views.fragments.ExplanationVideoFragment;
import co.kr.myfitnote.views.fragments.ReadyFragment;
import co.kr.myfitnote.views.fragments.TestStartedFragment;
import co.kr.myfitnote.views.fragments.handraise.HandRaiseResultFragment;
import co.kr.myfitnote.views.fragments.handraise.HandRaiseSettingFragment;
import co.kr.myfitnote.views.fragments.handraise.HandRaiseStartedFragment;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HandraiseMeasurementActivity extends AppCompatActivity implements MeasurementEventListener, PoseDataListener {
    private static final String TAG = "HandraiseMeasurementActivity";

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
    private TestResult testResult;

    private User user;
    private Client client;
    private Bundle bundle;
    private HandRaise handRaise;

    private boolean failCountFlag = true;
    private boolean isDrawLine = false;

    private ImageView overlayImageView;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    private LinearLayout count_layout;
    private int viewWeight, viewHeight;
    private Bitmap bitmap;
    private TextView count_view;
    String lineColor = "#00b0a6";
    String lineColor2 = "#FFBC64";
    private boolean isNeedCheckHandYPosition = false;
    HandRaiseStartedFragment fragment = null;

    private int time = 30;

    private UITimerManager uiTimerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handraise_measurement);
        bundle = getIntent().getExtras();

        SoundManager.initSounds(this);

        initComponents();

        user = PreferencesManager.getInstance(this).getUser();
        // 클라이언트
        client = new Client();
        client.setName(bundle.getString("name"));
        client.setPhone(bundle.getString("phone"));
        client.setGender(bundle.getString("gender"));
        client.setBirth_date(bundle.getString("birth"));

        count_view = findViewById(R.id.count_view);
        count_layout = findViewById(R.id.count_layout);
        testResult = new TestResult();

        handRaise = new HandRaise();

        managerFragment = new ManagerFragment();
        managerFragment.add(new HandRaiseSettingFragment("상지 근기능"));
        managerFragment.add(new HandRaiseResultFragment());

        Toolbar titleBar = findViewById(R.id.title_bar);
        titleBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSupportActionBar(titleBar);
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
        overlayImageView = findViewById(R.id.overlay_imageView);
        notice_view = findViewById(R.id.notice_view);
        seconds_view = findViewById(R.id.seconds_view);
        tv_count = findViewById(R.id.tv_count);
        mlkitCameraManager = new MLkitCameraManager(this, cameraSource, preview, graphicOverlay);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(0);

        uiTimerManager = new UITimerManager(progressBar, seconds_view);
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
        if(index < managerFragment.getSize()) {
            if (managerFragment.getCurrentFragment() instanceof ExplanationVideoFragment) {
                index += 1;
            }
            managerFragment.setIndex(index);
        }
    }

    @Override
    public void setOptions(Bundle bundle) {
        if (managerFragment.getCurrentFragment() instanceof HandRaiseSettingFragment) {
            status = MEASUREMENT_STATUS.EXERCISE_READY;
            time = bundle.getInt("time");
            // Set time to test result object for showing in result screen.
            getTestResult().setSecond(time);
            handRaise.setSide(bundle.getString("hand_direction"));
            getSupportFragmentManager().beginTransaction().remove(managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public TestResult getTestResult() {
        testResult.setCount(handRaise.getRepetitionCount());
        testResult.setGrade(handRaise.getGrade(client));
        testResult.setLeftAngleGraphData(handRaise.getChartData());
        testResult.setWristData(handRaise.getScatterData());
        return testResult;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getSupportFragmentManager().beginTransaction().detach(managerFragment.getCurrentFragment()).commitNow();
            getSupportFragmentManager().beginTransaction().attach(managerFragment.getCurrentFragment()).commitNow();
        } else {
            getSupportFragmentManager().beginTransaction().detach(managerFragment.getCurrentFragment()).attach(managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public void pause() {

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
        result.setExercise(handRaise.getExerciseLabel());
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

    private void displayTextView(String text){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                count_view.setText(text);
            }
        });
    }

    private void drawLine(float startLineY, float endLineY){
        /**
         * 시작 라인과 종료 라인 y 값을 기준으로 선을 그리는 함수
         */
        if (!isDrawLine){
//            if (handRaise.getSide().equals("왼손")) {
//                drawLine(startLineY, lineColor, 0);
//                drawLine(endLineY, lineColor, 1);
//            }else{
//                drawLine(startLineY, lineColor, 0);
//                drawLine(endLineY, lineColor, 1);
//            }
            bitmap = Bitmap.createBitmap(overlayImageView.getWidth(), overlayImageView.getHeight(), Bitmap.Config.ARGB_8888);
            drawLine(startLineY, lineColor2, 0);
            drawLine(endLineY, lineColor, 1);
            isDrawLine = true;
        }
    }

    public void drawLine(float y, String color, int type){
        /**
         * 실제 선의 위치에 기준선 이미지를 만드는 함수
         *
         * type 0 : start line
         * type 1 : end line
         */
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.parseColor(color));
                paint.setStrokeWidth(30);
                canvas.drawLine(0, y, overlayImageView.getWidth(), y, paint);

                // Load your .png image as a Bitmap
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), type != 0 ? R.drawable.ic_criteria_line : R.drawable.ic_criteria_down_line);

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