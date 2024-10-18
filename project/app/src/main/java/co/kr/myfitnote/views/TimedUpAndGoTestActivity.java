package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
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
import com.google.protobuf.InvalidProtocolBufferException;

import co.kr.myfitnote.MySoundPool;
import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.TUG;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.views.fragments.ReadyFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceStartedFragment;
import co.kr.myfitnote.views.fragments.tug.TugVideoFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TimedUpAndGoTestActivity extends AppCompatActivity implements SendEventListener {
    private static final String TAG = "TimedUpAndGoTestActivity";
    private static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";
    private static final boolean FLIP_FRAMES_VERTICALLY = true;
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    private ExternalTextureConverter converter;
    private SurfaceTexture previewFrameTexture;
    private SurfaceView previewDisplayView;
    private EglManager eglManager;
    private FrameProcessor processor;
    private CameraXPreviewHelper cameraHelper;
    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    private final int RUNNING_PENDDING = 1;
    private final int RUNNING_READY = 2;
    private final int RUNNING_START = 3;
    private final int RUNNING_PAUSE = 4;
    private int runningStatus = RUNNING_PENDDING;

    private ManagerFragment managerFragment;
    private Bundle settingBundle;

    // 초기 바로 체크될 수 있는 문제를 해결하기 위한 boolean
    private boolean isStandup = false;
    // 기준점을 1회만 그리기 위함
    private boolean drawCircleOnPosition = false;
    // this is boolean for preventing from duplication send startActivity and send log data
    private boolean isFinish = false;
    private int viewWeight, viewHeight;
    private TUG tug;
    private ImageView overlayImageView;
    private Bitmap bitmap;
    private TextView test_result_tv;
    private MySoundPool mySoundPool;

    private Retrofit retrofit = RetrofitClient.getRetrofit();
    private RetrofitService service = retrofit.create(RetrofitService.class);
    private SharedPreferences pref;
    private String userData;
    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_up_and_go_test);

        pref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "haven't token yet");
        user = gson.fromJson(userData, User.class);

        mySoundPool = new MySoundPool(getApplicationContext(), R.raw.voice_guide_start);

        previewDisplayView = findViewById(R.id.surface_view);
        overlayImageView = findViewById(R.id.overlay_imageView);
//        test_result_tv = findViewById(R.id.test_result_tv);
        setupPreviewDisplayView();

        AndroidAssetUtil.initializeNativeAssetManager(this);
        eglManager = new EglManager(null);

        processor =
                new FrameProcessor(
                        this,
                        eglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        processor
                .getVideoSurfaceOutput()
                .setFlipY(FLIP_FRAMES_VERTICALLY);



        tug = new TUG();
        managerFragment = new ManagerFragment();
        managerFragment.add(new TugVideoFragment("", "video_100_tug"));
        ReadyFragment readyFragment = new ReadyFragment("","지금부터 검사를 시작합니다.");
        readyFragment.setButtonVisible(View.GONE);
        managerFragment.add(readyFragment);
        managerFragment.add(new SingleLegStanceStartedFragment());
    }

    private void drawCircleCriteriaPosition(){
        drawCircleOnPosition = true;
        drawCircleOnBitmap((int) (tug.getLeft_hip_criteria_x() * viewWeight),
                           (int) (tug.getLeft_hip_criteria_y() * viewHeight),
                            30, "#00b0a6");
        drawCircleOnBitmap((int) (tug.getRight_hip_criteria_x() * viewWeight),
                           (int) (tug.getRight_hip_criteria_y() * viewHeight),
                30, "#00b0a6");
        drawCircleOnBitmap((int) (tug.getLeft_shoulder_criteria_x() * viewWeight),
                           (int) (tug.getLeft_shoulder_criteria_y() * viewHeight),
                30, "#00b0a6");
        drawCircleOnBitmap((int) (tug.getRight_shoulder_criteria_x() * viewWeight),
                           (int) (tug.getRight_shoulder_criteria_y() * viewHeight),
                30, "#00b0a6");

    }

    private void drawCircleOnBitmap(int x, int y, int radius, String color){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.parseColor(color));
                canvas.drawCircle(x, y, radius, paint);
                overlayImageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter =
                new ExternalTextureConverter(
                        eglManager.getContext(), 2);
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        converter.close();

        // Hide preview display until we re-open the camera again.
        previewDisplayView.setVisibility(View.INVISIBLE);
    }

    public void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    onCameraStarted(surfaceTexture);
                });
        CameraHelper.CameraFacing cameraFacing = CAMERA_FACING;
        cameraHelper.startCamera(
                this, cameraFacing, /*unusedSurfaceTexture=*/ null, null);


    }
    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        previewFrameTexture = surfaceTexture;
        previewDisplayView.setVisibility(View.VISIBLE);
    }

    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.INVISIBLE);
        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput()
                                         .setSurface(holder.getSurface());
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
                                Log.e(TAG, "Info: " + width + " " + height);
                                viewWeight = width;
                                viewHeight = height;
                                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                            }
                        });
    }

    protected void onPreviewDisplaySurfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {

        Size viewSize = computeViewSize(width, height);
        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
        boolean isCameraRotated = cameraHelper.isCameraRotated();

        converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
    }

    protected Size computeViewSize(int width, int height) {
        return new Size(width, height);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFragmentChange(int fragmentNum) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getFragment(fragmentNum)).commit();
    }

    @Override
    public void nextFragment() {}

    @Override
    public void nextFragment(Bundle bundle) {
        int index = managerFragment.getIndex() + 1;

        if(index < managerFragment.getSize() ) {
            managerFragment.setIndex(index);
            if(managerFragment.getCurrentFragment() instanceof ReadyFragment){
                runningStatus = RUNNING_READY;
                settingBundle = bundle;
            }else if(managerFragment.getCurrentFragment() instanceof SingleLegStanceStartedFragment){
                runningStatus = RUNNING_START;
            }else{
                runningStatus = RUNNING_PENDDING;
            }

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.frame_layout, managerFragment.getCurrentFragment())
                                       .commit();
        }
    }

    @Override
    public TestResult getTestResult() {
        return null;
    }

    @Override
    public Exercise getExercise() {
        return null;
    }

    @Override
    public void setStatus(int status) {}

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
        SingleLegStanceStartedFragment fragment = (SingleLegStanceStartedFragment) managerFragment.getCurrentFragment();
        if(runningStatus == RUNNING_PAUSE){
            runningStatus = RUNNING_START;
            fragment.startTimerTask();

        }else{
            runningStatus = RUNNING_PAUSE;
            fragment.stopStartTimerTask();
        }

    }

    @Override
    public boolean isPause(){
        return false;
    }

    private void sendLogData(){
        // if there is log data, send to server for stroing in DB
        new Thread(() -> {
            try {
                Call<RetrofitStatus> request = service.createExerciseResult(user.getToken(),
                        new Gson().toJson(tug, TUG.class));
                request.enqueue(new Callback<RetrofitStatus>() {
                    @Override
                    public void onResponse(Call<RetrofitStatus> call, Response<RetrofitStatus> response) {
                        Log.e("RESULT", "come to response");
                        finish();
                    }

                    @Override
                    public void onFailure(Call<RetrofitStatus> call, Throwable t) {
                        Log.e("RESULT", t.getMessage());
                        finish();
                    }
                });
            }catch(Exception err){
                err.printStackTrace();
            }
        }).start();
    }
}