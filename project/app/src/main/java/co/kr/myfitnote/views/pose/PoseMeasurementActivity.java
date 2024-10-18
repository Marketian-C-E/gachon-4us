package co.kr.myfitnote.views.pose;

import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;
import static co.kr.myfitnote.core.mlkit.Variable.POSE_DETECTION;

import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.mlkit.vision.pose.PoseLandmark;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.client.interfaces.PoseDataListener;
import co.kr.myfitnote.components.SurfaceDrawingView;
import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.mlkit.posedetector.PoseGraphic;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.AnimationManager;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.MLkitCameraManager;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.measurement.data.Variable;
import co.kr.myfitnote.model.pose.CheckConditionResult;
import co.kr.myfitnote.model.pose.Pose;
import co.kr.myfitnote.model.pose.PoseMeasurement;
import co.kr.myfitnote.views.rom.RomResultData;
import co.kr.myfitnote.views.rom.RomResultFragment;
import co.kr.myfitnote.views.rom.data.ClientRomResultData;
import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PoseMeasurementActivity extends AppCompatActivity implements RomResultFragment.OnFragmentListener, View.OnClickListener, PoseDataListener {
    private static final String TAG = "PoseMeasurementActivity";
    private static final int MAX_BODY_CHECK_COUNT = 30;

    private String selectedModel = POSE_DETECTION;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private float previewWidth = 480;

    private String type;
    private PoseMeasurement poseMeasurement;
    private Pose pose;
    private enum POSE_STATUS {BODY_CHECK, READY, COUNTING, FINISH, PENDING};
    private POSE_STATUS status = POSE_STATUS.BODY_CHECK; // POSE_STATUS.READY;

    private FragmentContainerView fragment_container;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private RomResultFragment romResultFragment;

    private Intent intent;
    private TextView tvLog;
    private LinearLayout center_text_section;
    private Button btn_start_point;
    private NoticeView notice_view;
    private TextView seconds_view;
    private PausableCountDownTimer countDownTimer;

    private AnimationManager animationManager;
    private MLkitCameraManager mlkitCameraManager;

    private User user;
    private Client client;
    private Bundle bundle;

    private int bodyCheckCount = 0;
    private boolean readyProgressForBodyCheck = false;


    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    private Runnable afterReady  = new Runnable() {
        @Override
        public void run() {
            // 시작 위치와 종료 위치 캘리브레이션이 다 완료 되었을 때 호출
            measureStart();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posemeasurement);
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

        intent = getIntent();
        type = intent.getStringExtra("Type");

        poseMeasurement = new PoseMeasurement();
        poseMeasurement.setPose(type);
        pose = poseMeasurement.getPose();

        Log.i(TAG, "Type => " + type);

    }

    private void initComponents(){
        tvLog = findViewById(R.id.tvLog);
        center_text_section = findViewById(R.id.center_text_section);
        fragment_container = findViewById(R.id.fragment_container);
        btn_start_point = findViewById(R.id.btn_start_point);
        btn_start_point.setOnClickListener(this);
        preview = findViewById(R.id.preview_view);
        notice_view = findViewById(R.id.notice_view);
        seconds_view = findViewById(R.id.seconds_view);

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
            }
        }, 3000);
    }

    public void restart(){
        /**
         * 재측정
         */
        status = POSE_STATUS.BODY_CHECK;
        bodyCheckCount = 0;
        readyProgressForBodyCheck = false;
        // reset fragment container
        Fragment fragment = fragmentManager.findFragmentByTag("resultFragment");
        if (fragment != null){
            fragmentManager.beginTransaction()
                           .remove(fragment)
                           .commit();
        }


        // reset romMeasurement
        this.poseMeasurement.restart();
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

    public void measureStart(){
        Bundle bundle = new Bundle();
        bundle.putString("type", poseMeasurement.getPose().getTypeName() + " 자세 측정");
        // Send bitmap which taken from view to fragment to show user.
        Bitmap viewImage = getBitmapFromView(findViewById(R.id.pose_estimation_section));
        sendMeasurementData(viewImage);
        bundle.putParcelable("resultImage", viewImage);
        status = POSE_STATUS.FINISH;

        setLogText("자세 측정이 종료되었습니다.\n잠시 후 결과가 안내됩니다!");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container,  romResultFragment.getClass(), bundle, "resultFragment")
                        .commit();
            }
        }, 2000);
    }

    public void measureFinish(){
    }

    public Bitmap getBitmapFromView(View view) {
        // Create a Bitmap with the same dimensions as the View
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Draw the view inside the Bitmap
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_point:
                measureStart();
                break;
        }
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

    private void sendMeasurementData(Bitmap resultImage) {
        /**
         * 측정 정보 저장
         */

        // 고객 측정 정보 데이터 모델 생성
        ClientRomResultData result = new ClientRomResultData();
        result.setName(client.getName());
        result.setPhone(client.getPhone());
        result.setType(poseMeasurement.getPose().getTypeName());

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
    public void onPoseDataReceived(com.google.mlkit.vision.pose.Pose poseData, GraphicOverlay graphicOverlay) {
        if (poseData == null){
            return;
        }

        if (status == POSE_STATUS.PENDING){
            return;
        }

    }

    public void readyProgress(int max){
        /**
         * Ready progress bar for progress function
         */
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(max);
    }

    public void updateProgress(int progress){
        /**
         * Update progress bar for progress function
         */
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(progress);
    }

}