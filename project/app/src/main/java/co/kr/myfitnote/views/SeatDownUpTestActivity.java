package co.kr.myfitnote.views;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


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
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.Graph;
import co.kr.myfitnote.model.SeatDownUp;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.viewmodels.PhysicalTestViewModel;
import co.kr.myfitnote.views.fragments.ExplanationTextFragment;
import co.kr.myfitnote.views.fragments.ExplanationVideoFragment;
import co.kr.myfitnote.model.ManagerFragment;
import co.kr.myfitnote.views.fragments.TestResultFragment;
import co.kr.myfitnote.views.fragments.TestStartedFragment;

public class SeatDownUpTestActivity extends AppCompatActivity implements View.OnClickListener, SendEventListener  {
    private static final String TAG = "YXH";
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
    private PhysicalTestViewModel physicalTestViewModel;
    private SeatDownUp seatDownUp;

    private FunctionTest functionTest;
    private Exercise exercise;
    private ManagerFragment managerFragment;

    private Intent it;

    private List<Graph> graphs = new ArrayList<>();
    private long startTimesMS = -1;

    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seat_down_up_test);
        // fragment 등록
        managerFragment = new ManagerFragment();
//        managerFragment.add(new ExplanationVideoFragment("근기능검사","http://nfa.kspo.or.kr/common/site/www/front/movie_zip/kind4_5/kind4_5.mp4", 30)); // index: 0
        managerFragment.add(new ExplanationVideoFragment("근기능검사","menu1", 30)); // index: 0
        ExplanationTextFragment explanationTextFragment = new ExplanationTextFragment("근기능검사","지금부터 근기능검사를 시작합니다.\n \"팔을 가슴에 교차하고 \n 바른자세로 3초간 서 주세요\"");
        explanationTextFragment.setButtonVisible(View.GONE);
        managerFragment.add(explanationTextFragment); // index: 1
        managerFragment.add(new  TestStartedFragment("근기능검사")); // index: 2
        managerFragment.add(new TestResultFragment()); // index: 3

        //운등 등록
        exercise = new Exercise();
        exercise.add(new SeatDownUp());

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



//        managerFragment.setIndex(3);


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
    public void nextFragment() {
        int index = managerFragment.getIndex() + 1;
        Log.e(TAG,"fragmentsise:"+managerFragment.getSize()+", index:"+index);
        if(index < managerFragment.getSize() ) {

            if(managerFragment.getCurrentFragment() instanceof TestStartedFragment){
                exercise.setIndex(exercise.getIndex() + 1);
            }
            managerFragment.setIndex(index);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();

        }else{
            Intent intent = new Intent(getApplicationContext(), WalkTestActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void nextFragment(Bundle bundle) {
        int index = managerFragment.getIndex() + 1;
        Log.e(TAG,"fragmentsise:"+managerFragment.getSize()+", index:"+index);
        if(index < managerFragment.getSize() ) {

            if(managerFragment.getCurrentFragment() instanceof TestStartedFragment){
                exercise.setIndex(exercise.getIndex() + 1);
            }
            managerFragment.setIndex(index);
            managerFragment.getCurrentFragment().setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, managerFragment.getCurrentFragment()).commit();

        }else{
            Intent intent = new Intent(getApplicationContext(), WalkTestActivity.class);
            startActivity(intent);
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

}