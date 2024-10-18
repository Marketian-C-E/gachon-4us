package co.kr.myfitnote.measurement.ui.seatdownup;


import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;
import static co.kr.myfitnote.core.mlkit.Variable.POSE_DETECTION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
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
import java.util.List;

import co.kr.myfitnote.FunctionTest;
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
import co.kr.myfitnote.measurement.ui.singleleg.SingleLegMeasurementActivity;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.Graph;
import co.kr.myfitnote.model.HandRaise;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.SeatDownUp;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.viewmodels.PhysicalTestViewModel;
import co.kr.myfitnote.views.WalkTestActivity;
import co.kr.myfitnote.views.fragments.ExplanationTextFragment;
import co.kr.myfitnote.views.fragments.ExplanationVideoFragment;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.views.fragments.TestResultFragment;
import co.kr.myfitnote.views.fragments.TestStartedFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceSettingFragment;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SeatDownUpMeasurementActivity extends AppCompatActivity implements MeasurementEventListener, PoseDataListener {
    private static final String TAG = "SeatDownUpMeasurementActivity";

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
    private SeatDownUp seatDownUp;

    private Bundle bundle;
    private User user;
    private Client client;

    private int time;
    private long startTimesMS = -1;
    private long lastSaveTime = 0;
    private final long DEBOUNCE_TIME = 500;


    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_down_up_measurement_acitivty);

        bundle = getIntent().getExtras();
        seatDownUp = new SeatDownUp();

        SoundManager.initSounds(this);

        initComponents();

        // 기업 관리자
        user = PreferencesManager.getInstance(this).getUser();
        // 클라이언트
        client = new Client();
        client.setName(bundle.getString("name"));
        client.setPhone(bundle.getString("phone"));

        //운등 등록
        exercise = new Exercise();
        exercise.add(seatDownUp);

        // fragment 등록
        managerFragment = new ManagerFragment();
        managerFragment.add(new ExplanationVideoFragment("근기능검사","menu1", 30, "하지 근기능 검사의 기본 시간은 30초입니다."));
        managerFragment.add(new MeasurementResultFragment());

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
        if(index < managerFragment.getSize()) {
            managerFragment.setIndex(index);
            if (getSupportFragmentManager().isDestroyed()) return;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
        }
//        Log.e(TAG,"fragment sizes => " + managerFragment.getSize()+", index:"+index);
//        if(index < managerFragment.getSize() ) {
//
//            if(managerFragment.getCurrentFragment() instanceof TestStartedFragment){
//                exercise.setIndex(exercise.getIndex() + 1);
//            }
//            managerFragment.setIndex(index);
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
//
//        }else{
//            Intent intent = new Intent(getApplicationContext(), WalkTestActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    public void nextFragment(Bundle bundle) {
        int index = managerFragment.getIndex() + 1;
        Log.e(TAG,"fragment sizes => " + managerFragment.getSize()+", index:"+index);
        if(index < managerFragment.getSize()) {
            if (managerFragment.getCurrentFragment() instanceof ExplanationVideoFragment) {
//                status = SingleLegMeasurementActivity.SINGLE_LEG_STANCE_STATUS.EXERCISE_READY;
                index += 1;
            }
            managerFragment.setIndex(index);
        }
//        if(index < managerFragment.getSize() ) {
//
//            if(managerFragment.getCurrentFragment() instanceof TestStartedFragment){
//                exercise.setIndex(exercise.getIndex() + 1);
//            }
//            managerFragment.setIndex(index);
//            managerFragment.getCurrentFragment().setArguments(bundle);
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
//
//        }else{
//            Intent intent = new Intent(getApplicationContext(), WalkTestActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    public void setOptions(Bundle bundle) {
        if (managerFragment.getCurrentFragment() instanceof ExplanationVideoFragment) {
            status = MEASUREMENT_STATUS.EXERCISE_READY;
            time = bundle.getInt("time");
            // Set time to test result object for showing in result screen.
            getTestResult().setSecond(time);
            // remove setting fragment
            getSupportFragmentManager().beginTransaction().remove(managerFragment.getCurrentFragment()).commit();
        }
    }

    @Override
    public TestResult getTestResult() {
        return seatDownUp.getTestResult();
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
        // this method is called from fragment.
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
                        Log.e(TAG, response.message());
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Client>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
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

}