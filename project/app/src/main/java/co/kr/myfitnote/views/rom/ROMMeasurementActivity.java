package co.kr.myfitnote.views.rom;

import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;
import static co.kr.myfitnote.core.mlkit.Variable.POSE_DETECTION;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.errorprone.annotations.Var;
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

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.client.interfaces.PoseDataListener;
import co.kr.myfitnote.components.SurfaceDrawingView;
import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.mlkit.posedetector.CustomPoseGraphic;
import co.kr.myfitnote.core.mlkit.posedetector.LineGraphic;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.AnimationManager;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.CaptureManager;
import co.kr.myfitnote.core.utils.MLkitCameraManager;
import co.kr.myfitnote.core.utils.MediapipeUtil;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;
import co.kr.myfitnote.measurement.data.Variable;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.pose.CheckConditionResult;
import co.kr.myfitnote.model.pose.FrontPose;
import co.kr.myfitnote.model.rom.ROM;
import co.kr.myfitnote.model.rom.ROMMeasurement;
import co.kr.myfitnote.views.pose.PoseMeasurementActivity;
import co.kr.myfitnote.views.rom.data.ClientRomResultData;
import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ROMMeasurementActivity extends AppCompatActivity implements RomResultFragment.OnFragmentListener, View.OnClickListener, PoseDataListener {
    final private String TAG = "ROMMeasurementActivity";

    private String selectedModel = POSE_DETECTION;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private SurfaceDrawingView surfaceDrawingView;
    private String ROMType;
    private ROMMeasurement romMeasurement;

    private FragmentContainerView fragment_container;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private RomResultFragment romResultFragment;
    private enum ROM_STATUS {BODY_CHECK, READY, COUNTING, FINISH, PENDING};
    private ROM_STATUS rom_status = ROM_STATUS.BODY_CHECK;
    private Pose lastPoseData;

    private Intent intent;
    private TextView tvLog;
    private LinearLayout center_text_section;
    private Button btn_start_point, btn_end_point;
    private NoticeView notice_view;
    private TextView seconds_view;
    private ProgressBar progressBar;

    private AnimationManager animationManager;
    private MLkitCameraManager mlkitCameraManager;

    private User user;
    private Client client;
    private Bundle bundle;

    private PausableCountDownTimer countDownTimer;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    // Image Capture
    ClientRomResultData result;

    FrontPose frontPose = new FrontPose("Front");

    TextView logTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rommeasurement);
        bundle = getIntent().getExtras();

        SoundManager.initSounds(this);

        user = PreferencesManager.getInstance(this).getUser();
        // 클라이언트
        client = new Client();
        client.setName(bundle.getString("name"));
        client.setPhone(bundle.getString("phone"));

        animationManager = new AnimationManager();

        if (savedInstanceState == null){
            romResultFragment = new RomResultFragment();
        }

        initComponents();
        mlkitCameraManager = new MLkitCameraManager(this, cameraSource, preview, graphicOverlay);

//        intent = getIntent();
//        ROMType = intent.getStringExtra("ROMType");
        ROMType = bundle.getString("ROMType");
        romMeasurement = new ROMMeasurement();
        romMeasurement.setRom(new ROM(ROMType));

        logTv = findViewById(R.id.log_tv);

    }

    private void initComponents(){
        tvLog = findViewById(R.id.tvLog);
        center_text_section = findViewById(R.id.center_text_section);
        preview = findViewById(R.id.preview_view);
        notice_view = findViewById(R.id.notice_view);
        seconds_view = findViewById(R.id.seconds_view);
        fragment_container = findViewById(R.id.fragment_container);
//        btn_start_point = findViewById(R.id.btn_start_point);
//        btn_end_point = findViewById(R.id.btn_end_point);
//        btn_start_point.setOnClickListener(this);
//        btn_end_point.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_bar);

        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
    }

    private void setLogText(String message){
        animationManager.fadeIn(center_text_section);
        tvLog.setText(message);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                animationManager.fadeOut(center_text_section);
//                tvLog.setText(message);
            }
        }, 3000);
    }

    public void restart(){
        /**
         * 재측정
         */
        // reset fragment container
        Fragment fragment = fragmentManager.findFragmentByTag("resultFragment");
        if (fragment != null){
            fragmentManager.beginTransaction()
                           .remove(fragment)
                           .commit();
        }

        // clean surfaceView
//        surfaceDrawingView.cleanCanvas();
//        surfaceDrawingView.setZOrderOnTop(true);

        // reset romMeasurement
//        this.romMeasurement.restart();

        rom_status = ROM_STATUS.BODY_CHECK;
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

        SoundManager.cleanUp();
        // SoundManager.stop();
    }

    @Override
    public void onMessage(String message) {
        switch (message){
            case "action_finish":
                finish();
                break;
            case "action_restart":
                restart();
                break;
        }
    }

    public void measureStart(Pose poseData){
        Log.d(Variable.TAG, "Save Start point!");

        romMeasurement.calcStartPointF(poseData);
//        drawLineFromPointList(romMeasurement.getThreePointFList(), new ArrayList<>(Arrays.asList(0, 1)));
//        btn_start_point.setVisibility(View.GONE);
//        btn_end_point.setVisibility(View.VISIBLE);

//        rom_status = ROM_STATUS.DID_START;
//        setLogText("시작 지점 측정이 정상적으로 되었습니다.\n다음 동작 이후 종료 지점 측정 버튼을 클릭해 주세요.");
    }

    public void measureFinish(){
        /**
         * 측정 종료 시 액티비티 역할
         */
        Log.d(TAG, "Save End point!");
        rom_status = ROM_STATUS.FINISH;
//        btn_end_point.setVisibility(View.GONE);
//        btn_start_point.setVisibility(View.VISIBLE);
//        romMeasurement.calcEndPointF(poseData);
        notice_view.showNotice("측정이 종료되었습니다.\n잠시 후 결과가 안내됩니다!");

        // Get angle from lastPoseData which is a Pose object.
        double angle = 0;
        try {
            PointF p1 = new PointF(
                    lastPoseData.getPoseLandmark(romMeasurement.getJoinPoints().get(0)).getPosition().x,
                    graphicOverlay.getHeight());
            PointF p2 = new PointF(
                    lastPoseData.getPoseLandmark(romMeasurement.getJoinPoints().get(0)).getPosition().x,
                    lastPoseData.getPoseLandmark(romMeasurement.getJoinPoints().get(0)).getPosition().y);
            PointF p3 = new PointF(
                    lastPoseData.getPoseLandmark(romMeasurement.getJoinPoints().get(1)).getPosition().x,
                    lastPoseData.getPoseLandmark(romMeasurement.getJoinPoints().get(1)).getPosition().y);
            angle = romMeasurement.getAngle(p1, p2, p3);
        } catch (Exception err){
            err.printStackTrace();
        }
        romMeasurement.setCalculatedAngle((int) angle);

        // 결과 리스트 반환 {어깨 : 120, 골반 : 120}
        ArrayList<RomResultData> resultData = new ArrayList<>();
//        resultData.add(new RomResultData(romMeasurement.getRom().getTypeName(), Math.round(romMeasurement.calcAngle())));
        resultData.add(new RomResultData(romMeasurement.getRom().getTypeName(), romMeasurement.getCalculatedAngle()));

        // 측정 결과 저장
        Bitmap viewImage = getBitmapFromView(findViewById(R.id.pose_estimation_section));
        sendMeasurementData(viewImage);

        Bundle bundle = new Bundle();
        bundle.putString("type", "관절가동범위");
        bundle.putSerializable("resultData", resultData);
        bundle.putString("resultStr", String.format("%s\nROM 각도는 %d° 입니다.",
                romMeasurement.getRom().getTypeName(),
                romMeasurement.getCalculatedAngle()));
        bundle.putParcelable("resultImage", viewImage);


        Log.e(TAG, "Data => " + resultData.get(0).getType() + " " + resultData.get(0).getValue());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container,  romResultFragment.getClass(), bundle, "resultFragment")
                        .commit();
            }
        }, 1500);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_point:
                // 셀프 측정 촬영 버튼 클릭 시
                measureFinish();
                break;
        }
    }

    private void sendMeasurementData(Bitmap resultImage) {
        /**
         * 측정 정보 저장
         */

        // 고객 측정 정보 데이터 모델 생성
        result = new ClientRomResultData();
        result.setName(client.getName());
        result.setPhone(client.getPhone());
        result.setType(romMeasurement.getRom().getTypeName());
        result.setAngle(romMeasurement.getCalculatedAngle());
        // bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultImage.compress(Bitmap.CompressFormat.PNG, 80, bos);
        byte[] bitmapData = bos.toByteArray();

        // byte array to request body for sending server
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), bitmapData);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "file.png", requestFile);

        Gson gson = new Gson();
        String json = gson.toJson(result);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);


        // 고객 측정 정보 전송
        service.createClientRom("Token " + user.getToken(), body, requestBody)
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

        lastPoseData = poseData;

        if (rom_status == ROM_STATUS.PENDING){
            return;
        }


    }

    public Bitmap getBitmapFromView(View view) {
        // Create a Bitmap with the same dimensions as the View
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Draw the view inside the Bitmap
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

}