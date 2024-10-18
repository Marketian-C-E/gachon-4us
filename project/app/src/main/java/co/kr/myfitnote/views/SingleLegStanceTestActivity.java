package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

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

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.MySoundPool;
import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.SingLegStance;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.views.fragments.ReadyFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceSettingFragment;
import co.kr.myfitnote.views.fragments.singleg.SingleLegStanceStartedFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SingleLegStanceTestActivity extends AppCompatActivity implements View.OnClickListener, SendEventListener {
    private static final String TAG = "SingleLegStanceTestActivity";
    private static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";
    private static final boolean FLIP_FRAMES_VERTICALLY = true;
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    private final int RUNNING_PENDDING = 1;
    private final int RUNNING_READY = 2;
    private final int RUNNING_START = 3;
    private final int RUNNING_PAUSE = 4;


    private ExternalTextureConverter converter;
    private SurfaceTexture previewFrameTexture;
    private SurfaceView previewDisplayView;
    private EglManager eglManager;
    private FrameProcessor processor;
    private CameraXPreviewHelper cameraHelper;

    private FunctionTest functionTest;
    private Exercise exercise;
    private ManagerFragment managerFragment;

    private int runningStatus = RUNNING_PENDDING;

    private Bundle settingBundle;
    private SingLegStance singLegStance;
    private MySoundPool failSoundPool;

    private boolean failCountFlag = true;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    SharedPreferences pref;
    String userData;
    User user;

    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seat_down_up_test);

        pref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "");
        user = gson.fromJson(userData, User.class);
        double threshold  = getIntent().getDoubleExtra("threshold", 0.2);
        singLegStance = new SingLegStance(threshold);
        failSoundPool = new MySoundPool(getApplicationContext(),R.raw.knee_up);

        managerFragment = new ManagerFragment();
        managerFragment.add(new SingleLegStanceSettingFragment("외발서기"));
        ReadyFragment readyFragment = new ReadyFragment("외발서기","지금부터 외발서기 검사를 시작합니다.");
        readyFragment.setButtonVisible(View.GONE);
        managerFragment.add(readyFragment);
        managerFragment.add(new SingleLegStanceStartedFragment());
//        managerFragment.add(new SingleLegStanceResultFragment());



        Toolbar titleBar = findViewById(R.id.title_bar);
        titleBar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setSupportActionBar(titleBar);

        PermissionHelper.checkAndRequestCameraPermissions(this);

        previewDisplayView = findViewById(R.id.surface_view);
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

    }

    public void endTest(int time, boolean isPass){
        Intent intent = new Intent(getApplicationContext(), SingleLegStanceTestResultActivity.class);
        singLegStance.setGrade(isPass);
        singLegStance.setTime(time);
        intent.putExtra("time", time);
        intent.putExtra("isPass", isPass);
        intent.putParcelableArrayListExtra("leftShoulderData", singLegStance.getLeftShoulderData());
        intent.putParcelableArrayListExtra("rightShoulderData", singLegStance.getRightShoulderData());
        intent.putParcelableArrayListExtra("leftHipData", singLegStance.getLeftHipData());
        intent.putParcelableArrayListExtra("rightHipData", singLegStance.getRightHipData());
        intent.putParcelableArrayListExtra("leftKneeData", singLegStance.getLeftKneeData());
        intent.putParcelableArrayListExtra("rightKneeData", singLegStance.getRightKneeData());

        sendLogData();

        startActivity(intent);
//        finish();
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
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
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
    public void onClick(View v) {

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
        Log.e(TAG,"fragmentsise:"+managerFragment.getSize()+", index:"+index);
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

//            managerFragment.getCurrentFragment().setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();

        }
    }

    @Override
    public TestResult getTestResult() {
        return null;
    }

    @Override
    public Exercise getExercise() {
        return exercise;
    }

    @Override
    public void setStatus(int status) {}

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


    private void sendLogData(){
            // if there is log data, send to server for stroing in DB
            new Thread(() -> {
                try {
                    Call<RetrofitStatus> request = service.createExerciseResult(user.getToken(),
                            new Gson().toJson(singLegStance, SingLegStance.class));
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
