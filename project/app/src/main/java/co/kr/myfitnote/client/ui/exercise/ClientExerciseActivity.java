package co.kr.myfitnote.client.ui.exercise;

import static co.kr.myfitnote.core.mlkit.Utils.getAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getLeftElbowAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getLeftHipAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getLeftKneeAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getLeftShoulderAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getRightElbowAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getRightHipAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getRightKneeAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getRightShoulderAngle;
import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.glutil.EglManager;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.IOException;

import co.kr.myfitnote.R;
import co.kr.myfitnote.client.DisplayLogger;
import co.kr.myfitnote.client.interfaces.PoseDataListener;
import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.mlkit.posedetector.CustomPoseGraphic;
import co.kr.myfitnote.core.mlkit.posedetector.PoseDetectorProcessor;
import co.kr.myfitnote.core.mlkit.preference.PreferenceUtils;
import co.kr.myfitnote.core.polar.HeartRateManager;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.Metronome;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.measurement.data.Variable;
import co.kr.myfitnote.measurement.data.WalkYCriteria;
import co.kr.myfitnote.measurement.ui.walk.WalkMeasurementActivity;
import co.kr.myfitnote.model.ManagerFragment;

public class ClientExerciseActivity extends AppCompatActivity implements View.OnClickListener, PoseDataListener {
    private static final String TAG = "ClientExerciseActivity";
    private static final String POSE_DETECTION = "Pose Detection";

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

    private HeartRateManager heartRateManager;
    private TextView hr_board_text;

    private TextView seconds_view;
    private PausableCountDownTimer countDownTimer;


    private int time = 120;
    private long lastSaveTime = 0;
    private final long DEBOUNCE_TIME = 200;
    private WalkYCriteria walkYCriteria;

    private Metronome metronome;
    private int bpm;

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
    private MEASUREMENT_STATUS status = MEASUREMENT_STATUS.EXERCISE_READY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercise);

        SoundManager.initSounds(this);

        bpm = 300;
//        metronome = new Metronome(this, bpm, R.raw.metronome);

        walkYCriteria = new WalkYCriteria();
        walkYCriteria.setThreshold(0.3f);

        tv_count = findViewById(R.id.tv_count);

        // Get bundle from navigation
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            set_count = bundle.getInt("count");
            setCountInfomation();
            Log.e(TAG, "set_count => " + set_count);
        }
        displayLogger = new DisplayLogger(this, findViewById(R.id.tv_logging));
        displayLogger.setUseLogging(false);

        notice_view = findViewById(R.id.notice_view);
        animationView = findViewById(R.id.animation_view);

        // Set heartRateManager
        hr_board_text = findViewById(R.id.hr_board_text);
        heartRateManager = new HeartRateManager(this, hr_board_text, "B5359025");
        heartRateManager.setUp();

//        setVideo("http://58.120.166.106:7575/media/IMG_9607_1.mp4");
//        btn_stop = findViewById(R.id.btn_stop);
//        btn_stop.setOnClickListener(this);
        preview = findViewById(R.id.preview_view);

        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
        seconds_view = findViewById(R.id.seconds_view);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource(selectedModel);
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }

        if (SoundManager.isPlaying()){
            SoundManager.stop();
        }

//        if (metronome != null){
//            metronome.stop();
//        }

    }


    public void setVideo(String url){
        /**
         * 운동 영상을 재생하는 함수
         */
//        videoView = findViewById(R.id.video_view);
//        String videoUrl = url;
//        Uri videoUri = Uri.parse(videoUrl);
//        videoView.setVideoURI(videoUri);
//        videoView.start();
//
//        // restart video when video is finished
//        videoView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(android.media.MediaPlayer mp) {
//                videoView.start();
//            }
//        });
    }

    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
            switch (model) {
                case POSE_DETECTION:
                    PoseDetectorOptionsBase poseDetectorOptions =
                            PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
                    Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);
//                    boolean shouldShowInFrameLikelihood =
//                            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
//                    boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
                    boolean shouldShowInFrameLikelihood = false;
                    boolean visualizeZ = false;
                    boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
                    boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
                    cameraSource.setMachineLearningFrameProcessor(
                            new PoseDetectorProcessor(
                                    this,
                                    poseDetectorOptions,
                                    /* showInFrameLikehood */ shouldShowInFrameLikelihood,
                                    /* visualizeZ */ visualizeZ,
                                    /* rescaleZ */ rescaleZ,
                                    runClassification,
                                    /* isStreamMode = */ true));
                    break;
                default:
                    Log.e(TAG, "Unknown model: " + model);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Can not create image processor: " + model, e);
            Toast.makeText(
                            getApplicationContext(),
                            "Can not create image processor: " + e.getMessage(),
                            Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }

                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.btn_stop:
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                } else {
//                    videoView.start();
//                }
//                break;
            default:
                Log.e(TAG, "Unknown button");
        }
    }

    @Override
    public void onPoseDataReceived(Pose poseData, GraphicOverlay graphicOverlay) {
        // Handle pose data
        Log.d(TAG, "onPoseDataReceived");
        if (poseData == null){
            return;
        }

        if ((!isShowFullBody(poseData)) &&
                (status.compareTo(MEASUREMENT_STATUS.CALIBRATIONING.SETTING) > 0 && status.compareTo(MEASUREMENT_STATUS.CALIBRATIONING.EXERCISE_FINISH) < 0)){
            SoundManager.play(SoundManager.SHOW_FULL_BODY, this);
            notice_view.showNotice("전신이 보이게 서주세요.");
            return;
        }

        if (status.compareTo(MEASUREMENT_STATUS.CALIBRATIONING.CALIBRATIONING) >= 0 ){
            // CALIBRATIONING 단계 이후에는 Calibration 값을 그래픽으로 표시
            graphicOverlay.add(new CustomPoseGraphic(graphicOverlay, "#00b0a6", 0, (float) walkYCriteria.getCalibrationKneeY()));
        }

        if (status == MEASUREMENT_STATUS.EXERCISE_READY) {
            status = MEASUREMENT_STATUS.COUNTING;
            notice_view.showNotice("현재 자세로 5초 동안 서 주세요.");
            startCount(5, new Runnable() {
                @Override
                public void run() {
                    status = MEASUREMENT_STATUS.CALIBRATION;
                }
            }, 0);
        }else if (status == MEASUREMENT_STATUS.CALIBRATION){
            // 측정 준비
            status = MEASUREMENT_STATUS.CALIBRATIONING;
            notice_view.showNotice("5초 동안 현재 자세를 그대로 유지해 주세요.");
            startCount(5, new Runnable() {
                @Override
                public void run() {
                    status = MEASUREMENT_STATUS.EXERCISE_START;
                }
            }, 0);
        }else if (status == MEASUREMENT_STATUS.CALIBRATIONING) {
            Log.d(TAG, "CALIBRATIONING");
            walkYCriteria.addLandmark(poseData);
        }else if (status == MEASUREMENT_STATUS.EXERCISE_START){
            notice_view.showNotice("운동을 시작합니다.");
//            metronome.start();

            if (countDownTimer == null) {
                long timeInMillis = time * 1000;

                // Initialize the ProgressBar
                ProgressBar progressBar = findViewById(R.id.progress_bar);
                progressBar.setProgress(0);
                progressBar.setMax((int) timeInMillis);
                countDownTimer = new PausableCountDownTimer(timeInMillis, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // if remaining time is less than 0.10 second, show 0.00
                        if (millisUntilFinished < 100) {
                            // when time is over
                            seconds_view.setText("0");
                            progressBar.setProgress(0);
                        } else {
                            // set text to seconds_view as "00:00"
                            seconds_view.setText(String.format("%d", millisUntilFinished / 1000));
                        }
                        progressBar.setProgress((int) (timeInMillis - millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        finishExercise(false);
                    }
                };

                countDownTimer.resume();
                status = MEASUREMENT_STATUS.EXERCISE_STARTING;
            }

            status = MEASUREMENT_STATUS.EXERCISE_STARTING;
        }else if (status == MEASUREMENT_STATUS.EXERCISE_STARTING) {
            long now = System.currentTimeMillis();
            if (now - lastSaveTime > DEBOUNCE_TIME){
                walkYCriteria.addLandmarkData(poseData);
                lastSaveTime = now;
                if (walkYCriteria.check(poseData)){
                    animationView.playAnimation();
                    count = walkYCriteria.getCount();
                    setCountInfomation();
                    if (count == set_count){
                        // if count is over set_count, stop exercise
                        finishExercise(true);
//                        if (countDownTimer != null){
//                            countDownTimer.pause();
//                        }
//                        status = MEASUREMENT_STATUS.EXERCISE_FINISH;
//                        // stop exercise
//                        notice_view.showNotice("성공적으로 운동을 하였습니다!\n잠시 후에 결과창으로 이동합니다.");
//                        // After 3 seconds, finish this activity
//                        new android.os.Handler().postDelayed(
//                                new Runnable() {
//                                    public void run() {
//                                        finish();
//                                        // navigation to exercise result
//                                        Intent intent = new Intent(ClientExerciseActivity.this, ExerciseResult.class);
//                                        intent.putExtra("count", count);
//                                        intent.putExtra("exercise", "심폐 기능");
//                                        intent.putExtra("totalCount", set_count);
//                                        startActivity(intent);
//                                    }
//                                },
//                                3000);
                        return;
                    }
                }
            }

        }

//        checkPose(poseData);
    }

    public void finishExercise(
            boolean isClear
    ){
        /**
         * Invoked when exercise is finished
         */
        // if count is over set_count, stop exercise
        if (countDownTimer != null){
            countDownTimer.pause();
        }
        status = MEASUREMENT_STATUS.EXERCISE_FINISH;
        notice_view.showNotice("운동이 종료되었습니다. 고생하셨습니다.");

        // TODO: Send to log data to server

        // After 3 seconds, finish this activity
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        finish();
                        // navigation to exercise result
                        Intent intent = new Intent(ClientExerciseActivity.this, ExerciseResult.class);
                        intent.putExtra("count", count);
                        intent.putExtra("exercise", "심폐 기능");
                        intent.putExtra("totalCount", set_count);
                        startActivity(intent);
                    }
                },
                3000);
    }



    public void checkPose(Pose pose){
        String message = "";

        // When pose is null, break this method
        if (pose == null){
            return;
        }

        boolean isShowBody = isShowFullBody(pose);
        message = "isShowFullBody : " + isShowBody + "\n";

        // When isShowBody is false, notice to user to show whole body
        if (!isShowBody){
            if (showLogger) {
                displayLogger.displayToScreen(message);
            }
            notice_view.showNotice("전신이 보이도록 서 주세요.");
            return;
        }

        // This method checks the squat pose with pose data
        double leftHipAngle = getLeftHipAngle(pose);
        double rightHipAngle = getRightHipAngle(pose);
        double leftKneeAngle = getLeftKneeAngle(pose);
        double rightKneeAngle = getRightKneeAngle(pose);

        // make a string above variable values
        message += "(좌) 엉덩이: " + leftHipAngle + "\n" +
                "(우) 엉덩이: " + rightHipAngle + "\n" +
                "(좌) 무릎: " + leftKneeAngle + "\n" +
                "(우) 무릎: " + rightKneeAngle + "\n" +
                "Status : " + didCheckPoint + "\n" +
                "Count : " + count + "\n";

        // check the squat pose
        if (leftKneeAngle < 110 && rightKneeAngle < 110) {
            // squat pose
            message += "스쿼트 자세 Down";
            didCheckPoint = true;
        }

        if (didCheckPoint && leftKneeAngle > 165 && rightHipAngle > 165){
            message = "스쿼트 자세 Up";
            didCheckPoint = false;
            count++;

            // if count is over set_count, stop exercise
            if (count > set_count){
                // stop exercise
                notice_view.showNotice("성공적으로 운동을 하였습니다!");
                // After 3 seconds, finish this activity
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                finish();
                            }
                        },
                        3000);
                return;
            }
            animationView.playAnimation();
            setCountInfomation();
        }

        if (showLogger) {
            displayLogger.displayToScreen(message);
        }

    }

    public boolean isShowFullBody(Pose pose) {
        // if pose is null, return false (not show full body)
        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) == null){
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.LEFT_HIP) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.RIGHT_HIP) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.LEFT_KNEE) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE) == null) {
            return false;
        }

        // check point -> shoulder, hip, knee
        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getInFrameLikelihood() < frameLikelihoodThreshold) {
            return false;
        }

        return true;
    }

    private void setCountInfomation(){
        tv_count.setText(count + " / " + set_count);
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
        countDownTimer = new PausableCountDownTimer(timeInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // This callback is invoked on every tick of the timer (every 1000 milliseconds in this case)
                Log.d(TAG, "Seconds remaining: " + millisUntilFinished);
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
                    countDownTimer.resume();
                }
            }, delaySeconds); // Delay in milliseconds
        }else{
            // Start the timer
            countDownTimer.resume();
        }
    }
}