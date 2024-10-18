package co.kr.myfitnote.lab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.mlkit.vision.pose.Pose;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.client.DisplayLogger;
import co.kr.myfitnote.client.interfaces.PoseDataListener;
import co.kr.myfitnote.client.ui.exercise.ExerciseResult;
import co.kr.myfitnote.cm.data.CommonResult;
import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.polar.HeartRateManager;
import co.kr.myfitnote.core.ui.FullScreenVideoView;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.MLkitCameraManager;
import co.kr.myfitnote.core.utils.Metronome;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.core.utils.UITimerManager;
import co.kr.myfitnote.exercise.ExerciseParam;
import co.kr.myfitnote.exercise.ExerciseSession;
import co.kr.myfitnote.exercise.ExerciseSessionEvent;
import co.kr.myfitnote.exercise.data.SitDownUp;
import co.kr.myfitnote.exercise.data.StandingBalance1;
import co.kr.myfitnote.exercise.data.Walking1;
import co.kr.myfitnote.measurement.data.WalkYCriteria;
import co.kr.myfitnote.prescription.data.PrescriptionDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LabMainActivity extends AppCompatActivity implements PoseDataListener, ExerciseSessionEvent, View.OnClickListener {
    private static final String TAG = "LabMainActivity";
    private static final String POSE_DETECTION = "Pose Detection";

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    private String selectedModel = POSE_DETECTION;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private VideoView videoView;
    private TextView tv_count;
    private ImageView btn_stop;
    private LottieAnimationView animationView;
    private NoticeView notice_view;

    private boolean showLogger = true;
    private DisplayLogger displayLogger;

    private float frameLikelihoodThreshold = 0.7f;

    private int set_count = 0; // 목표 카운트
    private int count = 0; // 현재 카운트
    private boolean didCheckPoint = false;

    // 심박수 관련 변수
    private TextView hr_board_text;
    private HeartRateManager heartRateManager;
    private ProgressBar hr_progressBar;

    private TextView seconds_view, exercise_name_text_view;

    private MLkitCameraManager mlkitCameraManager;

    private ExerciseSession exerciseSession;

    private UITimerManager uiTimerManager;
    private ProgressBar progressBar;

    // logging
    private TextView logText;
    private ScrollView logScrollView;

    // 심박수 및 사용자 정보
    private Metronome metronome;
    private int bpm;
    private int maxHR;

    private User user;
    private Client client;
    private int age = 60; // 사용자 나이

    private HashMap<String, ExerciseParam> exercises;
    private int prescription_id;

    // Video
    private FullScreenVideoView exercise_video_view;

    // Skip button
    private Button skip_button;
    private RelativeLayout logging_layout;
    private boolean useLogging = false;

    @Override
    public void onPoseDataReceived(Pose poseData, GraphicOverlay graphicOverlay) {
        exerciseSession.process(poseData, graphicOverlay);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_main);

        SoundManager.initSounds(this);

        // Get bundle from navigation
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            // set_count = bundle.getInt("count");
            prescription_id = bundle.getInt("prescription_id");
            Log.e(TAG, "prescription_id is => " + prescription_id);
            exercises = (HashMap<String, ExerciseParam>) bundle.getSerializable("prescriptionDetail");
            loadUser(bundle);
            // Log.e(TAG, "set_count => " + set_count);
        }

        initComponents();
        setExerciseSession();

        // Set UI Timer manager
        uiTimerManager = new UITimerManager(progressBar, seconds_view);
    }

    /**
     * 사용자 정보 불러오기
     */
    public void loadUser(Bundle bundle){
        // 기업 관리자
        user = PreferencesManager.getInstance(this).getUser();
        // 클라이언트
        client = new Client();
        client.setName(bundle.getString("name"));
        client.setPhone(bundle.getString("phone"));
        client.setBirth_date(bundle.getString("birth"));
    }

    /**
     * 운동 세팅
     */
    public void setExerciseSession(){
        ExerciseSessionEvent context = (ExerciseSessionEvent) this;
        exerciseSession = new ExerciseSession(context, notice_view);
        for (String key : exercises.keySet()) {
            ExerciseParam value = exercises.get(key);
            String exerciseName = value.getExerciseName();
            Log.e(TAG, "운동 이름 => " + key + ", " + exerciseName);

            if (exerciseName.equals("걷기")) {
                Walking1 exercise = new Walking1(exerciseSession, value);
                exerciseSession.addExercise(exercise);
            }else if (exerciseName.equals("스쿼트")){
                SitDownUp sitDownUp = new SitDownUp(exerciseSession, value);
                exerciseSession.addExercise(sitDownUp);
            }else{
                StandingBalance1 standingBalance1 = new StandingBalance1(exerciseSession, value);
                exerciseSession.addExercise(standingBalance1);
            }
        }
    }

    /**
     * 심박수 컴포넌트 초기화
     */
    public void setHeartRate(){
        String deviceID = PreferencesManager.getInstance(this).getData("BLE_DEVICE_ID") == null ? "" : PreferencesManager.getInstance(this).getData("BLE_DEVICE_ID");
        hr_board_text = findViewById(R.id.hr_board_text);
        hr_progressBar = findViewById(R.id.hr_progressBar);
        heartRateManager = new HeartRateManager(this, hr_board_text, deviceID, client);
        heartRateManager.setUp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            heartRateManager.attachProgressBar(hr_progressBar);
        }
    }

    /**
     * BPM에 따른 메트로놈 설정
     */
    public void setMetronome(){
        metronome = new Metronome(calculateMaxHR());
    }

    public int calculateMaxHR(){
        maxHR = 220 - age;
        return maxHR;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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

        if (SoundManager.isPlaying()){
            SoundManager.stop();
        }

        if (metronome != null){
            metronome = null;
        }
    }

    /**
     * 컴포넌트 초기화
     */
    private void initComponents(){
        setHeartRate();
        tv_count = findViewById(R.id.tv_count);

        displayLogger = new DisplayLogger(this, findViewById(R.id.tv_logging));
        displayLogger.setUseLogging(false);

        notice_view = findViewById(R.id.notice_view);
        animationView = findViewById(R.id.animation_view);

        hr_board_text = findViewById(R.id.hr_board_text);
        preview = findViewById(R.id.preview_view);

        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
        seconds_view = findViewById(R.id.seconds_view);
        progressBar = findViewById(R.id.progress_bar);

        mlkitCameraManager = new MLkitCameraManager(this, cameraSource, preview, graphicOverlay);
        exercise_name_text_view = findViewById(R.id.exercise_name_text_view);

        exercise_video_view = findViewById(R.id.exercise_video_view);
        skip_button = findViewById(R.id.skip_button);
        skip_button.setOnClickListener(this);
        setLoggingComponent();

    }

    private void updateCountInformation(int currentCount, int setCount){
        tv_count.setText(currentCount + " / " + setCount);
    }

    /**
     * Logging
     */
    public void setLoggingComponent(){
        logging_layout = findViewById(R.id.logging_layout);
        Button button = findViewById(R.id.logging_toggle_button);
        logText = findViewById(R.id.textView);
        logScrollView = findViewById(R.id.scrollView);
        if (useLogging) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (logText.getVisibility() == View.VISIBLE) {
                        logText.setVisibility(View.GONE);
                    } else {
                        logText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else{
            logging_layout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 오늘의 운동 이력 저장
     *
     */
    public void savePrescriptionHistory(){
        service.didTodayPrescription(client.getPhone(), prescription_id).enqueue(new Callback<CommonResult>() {
            @Override
            public void onResponse(Call<CommonResult> call, Response<CommonResult> response) {
                Log.e(TAG, response.message());
                // ExerciseResult fragment로 이동
                 Intent intent = new Intent(LabMainActivity.this, ExerciseResult.class);
                 startActivity(intent);
            }

            @Override
            public void onFailure(Call<CommonResult> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Intent intent = new Intent(LabMainActivity.this, ExerciseResult.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void displayLog(String message){
        if (!useLogging)
            return;
        logText.append(message + "\n");
        logScrollView.post(new Runnable() {
            @Override
            public void run() {
                logScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public PausableCountDownTimer startTimer(int milsInFuture, int countDownInterval, int delayTimeUntilFinish, Runnable onFinish) {
        Log.e(TAG, "Start Timer method is called!");
        PausableCountDownTimer timer = uiTimerManager.createCountDown(milsInFuture, countDownInterval, delayTimeUntilFinish, onFinish);
        return timer;
    }

    @Override
    public void showNotice(String message) {
        notice_view.showNotice(message);
    }

    @Override
    public void updateCount(int currentCount, int setCount) {
        updateCountInformation(currentCount, setCount);
    }

    @Override
    public void finishSession(){
        Log.e(TAG, "finishSession method is called!");

        savePrescriptionHistory();

        // 3초 뒤 해당 액티비티 종료
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        finish();
                    }
                },
                3000);
    }

    @Override
    public void setTitle(String title){
        exercise_name_text_view.setText(title);
    }

    @Override
    public void playSound(int soundId){
        SoundManager.play(soundId, this);
    }

    /**
     * 비디오 영상 세팅
     */
    @Override
    public void setVideoView(String url, boolean useVideo){

        if (exercise_video_view == null){
            return;
        }

        if (!useVideo){
            exercise_video_view.setVisibility(View.GONE);
            // if video is playing, stop
            if (exercise_video_view.isPlaying()){
                exercise_video_view.stopPlayback();
            }
            return;
        }
        exercise_video_view.setVisibility(View.VISIBLE);
        String videoPath = "http://marketian.co.kr:7575/media/4_repeated_5.mp4";
        exercise_video_view.setVideoPath(videoPath);
        exercise_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // exercise_video_view.start();
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.skip_button:
                // 다음 운동으로 넘어가기 버튼 클릭 시
                exerciseSession.nextExercise();
                if (exercise_video_view != null && exercise_video_view.isPlaying())
                    exercise_video_view.stopPlayback();
                break;
        }
    }
}