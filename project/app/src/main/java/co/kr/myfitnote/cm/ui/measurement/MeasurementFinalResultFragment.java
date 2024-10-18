package co.kr.myfitnote.cm.ui.measurement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.GradeView;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.core.utils.BitmapUtil;
import co.kr.myfitnote.measurement.data.ClientFinalResultData;
import co.kr.myfitnote.measurement.data.ClientMeasurementData;
import co.kr.myfitnote.measurement.data.MeasurementData;
import co.kr.myfitnote.measurement.data.MeasurementROMData;
import co.kr.myfitnote.measurement.data.MeasurementTypeData;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MeasurementFinalResultFragment extends Fragment implements View.OnClickListener{
    private String TAG = "MeasurementFinalResultFragment";
    private RelativeLayout loadingPanel;
    private ArrayList<Integer> checkedIds;

    private TextView nameTV, birthDateTv, sexTV, heightTV, weightTV, titleNameTV, bmiTV;
    private TextView cardioValueTV, cardioGradeTV;
    private TextView upperValueTV, upperGradeTV;
    private TextView lowerValueTV, lowerGradeTV;
    private TextView leftsinglelegValueTV, leftsinglelegGradeTV, rightsinglelegValueTV, rightsinglelegGradeTV;
    private TextView rom_right_leg_abduction, rom_left_leg_abduction, rom_right_shoulder_abduction, rom_left_shoulder_abduction;

    private Button client_changed_graph_btn, prescription_btn;

    private ImageView rom_right_leg_abduction_img, rom_left_leg_abduction_img, rom_right_shoulder_abduction_img, rom_left_shoulder_abduction_img, pose_front_img, pose_side_img;

    private Client client;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    NavController navController;

    private GradeView walkGradeView, lowerGradeView, upperGradeView;
    private ScrollView scrollView;

    public MeasurementFinalResultFragment() {
        // Required empty public constructor
    }

    public static MeasurementFinalResultFragment newInstance(String param1, String param2) {
        MeasurementFinalResultFragment fragment = new MeasurementFinalResultFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        checkedIds = new ArrayList<>();
        if (getArguments() != null){
            checkedIds = getArguments().getIntegerArrayList("checkedIds");
        }

        Log.e(TAG, "Passed checked items length is => " + checkedIds.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement_final_result_v2, container, false);;
        navController = NavHostFragment.findNavController(this);

        scrollView = view.findViewById(R.id.scroll_view);
        nameTV = view.findViewById(R.id.tv_name);
        birthDateTv = view.findViewById(R.id.tv_birth);
        sexTV = view.findViewById(R.id.tv_sex);
        heightTV = view.findViewById(R.id.tv_height);
        weightTV = view.findViewById(R.id.tv_weight);
        titleNameTV = view.findViewById(R.id.title_user_tv);
        bmiTV = view.findViewById(R.id.tv_bmi);

        cardioValueTV = view.findViewById(R.id.cardio_value_tv);
        cardioGradeTV = view.findViewById(R.id.cardio_grade_tv);

        upperValueTV = view.findViewById(R.id.upper_value_tv);
        upperGradeTV = view.findViewById(R.id.upper_grade_tv);

        lowerValueTV = view.findViewById(R.id.lower_value_tv);
        lowerGradeTV = view.findViewById(R.id.lower_grade_tv);

        leftsinglelegValueTV = view.findViewById(R.id.left_singleleg_value_tv);
        rightsinglelegValueTV = view.findViewById(R.id.right_singleleg_value_tv);
        leftsinglelegGradeTV = view.findViewById(R.id.left_grade_view);
        rightsinglelegGradeTV = view.findViewById(R.id.right_grade_view);

        rom_right_leg_abduction = view.findViewById(R.id.rom_right_leg_abduction);
        rom_left_leg_abduction = view.findViewById(R.id.rom_left_leg_abduction);
        rom_right_shoulder_abduction = view.findViewById(R.id.rom_right_shoulder_abduction);
        rom_left_shoulder_abduction = view.findViewById(R.id.rom_left_shoulder_abduction);

        walkGradeView = view.findViewById(R.id.walk_grade_view);
        upperGradeView = view.findViewById(R.id.upper_grade_view);
        lowerGradeView = view.findViewById(R.id.lower_grade_view);

        loadingPanel = view.findViewById(R.id.loadingPanel);

        client_changed_graph_btn = view.findViewById(R.id.client_changed_graph_btn);
        prescription_btn = view.findViewById(R.id.prescription_btn);
        client_changed_graph_btn.setOnClickListener(this);
        prescription_btn.setOnClickListener(this);

        rom_right_leg_abduction_img = view.findViewById(R.id.rom_right_leg_abduction_img);
        rom_left_leg_abduction_img = view.findViewById(R.id.rom_left_leg_abduction_img);
        rom_right_shoulder_abduction_img = view.findViewById(R.id.rom_right_shoulder_abduction_img);
        rom_left_shoulder_abduction_img = view.findViewById(R.id.rom_left_shoulder_abduction_img);
        pose_front_img = view.findViewById(R.id.pose_front_img);
        pose_side_img = view.findViewById(R.id.pose_side_img);

        getClientFinalResult();

        return view;
    }

    public void getClientFinalResult(){

        if (checkedIds == null){
            return;
        }

        service.getClientFinalResult(checkedIds)
                .enqueue(new Callback<ClientFinalResultData>() {
            @Override
            public void onResponse(Call<ClientFinalResultData> call, Response<ClientFinalResultData> response) {
                ClientFinalResultData data = response.body();
                updateValueField(data);
                loadingPanel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ClientFinalResultData> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Sentry.captureMessage(t.getMessage());
                loadingPanel.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateValueField(ClientFinalResultData data){
        // 결과 바탕 텍스트 뷰 채우기
        MeasurementData measurement = data.getMeasurement();
        client = data.getClient();

        titleNameTV.setText(client.getName());
        nameTV.setText(client.getName());
        birthDateTv.setText(client.getBirth_date());
        sexTV.setText(client.getGender());
        heightTV.setText(client.getHeight());
        weightTV.setText(client.getWeight());

        // calculate BMI and catch exception for null value of height and weight
        try {
            double height = Double.parseDouble(client.getHeight());
            double weight = Double.parseDouble(client.getWeight());
            double bmi = weight / ((height / 100) * (height / 100));
            bmiTV.setText(String.format("%.2f", bmi));
        }catch (Exception e){
            bmiTV.setText(String.format("%.2f", 0.00));
            Log.e(TAG, e.getMessage());
            Sentry.captureMessage(e.getMessage());
        }

        cardioGradeTV.setText(getLevelOfGrade(measurement.getCardio().getGrade()) +
                " (" + measurement.getCardio().getGrade() + "등급)");
        cardioValueTV.setText(measurement.getCardio().getValue());

        if (!measurement.getCardio().getGrade().equals("-"))
            walkGradeView.setGrade(Integer.parseInt(measurement.getCardio().getGrade()));

        lowerGradeTV.setText(getLevelOfGrade(measurement.getLower().getGrade()) +
                " (" + measurement.getLower().getGrade() + "등급)");
        lowerValueTV.setText(measurement.getLower().getValue());
        if (!measurement.getLower().getGrade().equals("-"))
            lowerGradeView.setGrade(Integer.parseInt(measurement.getLower().getGrade()));

        upperGradeTV.setText(getLevelOfGrade(measurement.getUpper().getGrade()) +
                " (" + measurement.getUpper().getGrade() + "등급)");
        upperValueTV.setText(measurement.getUpper().getValue());
        if (!measurement.getUpper().getGrade().equals("-"))
            upperGradeView.setGrade(Integer.parseInt(measurement.getUpper().getGrade()));

        leftsinglelegValueTV.setText("왼발 · " + measurement.getSingleleg_left().getValue());
        rightsinglelegValueTV.setText("오른발 · " + measurement.getSingleleg_right().getValue());

        // if get single leg grade is 1, leftsinglelegGradeTV settext as Passed
        setSinglelegGradeView(leftsinglelegGradeTV, measurement.getSingleleg_left().getGrade());
        setSinglelegGradeView(rightsinglelegGradeTV, measurement.getSingleleg_right().getGrade());

        HashMap<String, MeasurementTypeData> romData = measurement.getRom();

        rom_right_shoulder_abduction.setText(romData.get("rom_right_shoulder_abduction").getValue());
        rom_left_shoulder_abduction.setText(romData.get("rom_left_shoulder_abduction").getValue());
        rom_right_leg_abduction.setText(romData.get("rom_right_leg_abduction").getValue());
        rom_left_leg_abduction.setText(romData.get("rom_left_leg_abduction").getValue());

        // Set image of rom to ImageView with url
        String[] keys = {"rom_right_shoulder_abduction", "rom_left_shoulder_abduction", "rom_right_leg_abduction", "rom_left_leg_abduction", "pose_front", "pose_side"};
        ImageView[] imageViews = {rom_right_shoulder_abduction_img, rom_left_shoulder_abduction_img, rom_right_leg_abduction_img, rom_left_leg_abduction_img, pose_front_img, pose_side_img};

        for (int i = 0; i < keys.length; i++){
            String key = keys[i];
            if (!romData.get(key).getResult_image().equals("")){
                Glide.with(this)
                        .load(romData.get(key).getResult_image())
                        .into(imageViews[i]);
            }
        }
    }

    public void setSinglelegGradeView(TextView singlelegGradeView, String grade){
        /**
         * 외발서기 등급에 따른 색상 및 라벨 변경
         */
        if (grade.equals("1")) {
            singlelegGradeView.setText("Passed");
        }else{
            singlelegGradeView.setText("Failed");
            singlelegGradeView.setBackgroundTintList(getResources().getColorStateList(R.color.n_red));
        }
    }

    public String getLevelOfGrade(String grade){
        // if grade 1 is 매우 좋음, 2 is 좋음, 3 is 보통, 4 is 미달
        String level = "";
        switch (grade){
            case "1":
                level = "매우 좋음";
                break;
            case "2":
                level = "좋음";
                break;
            case "3":
                level = "보통";
                break;
            case "4":
                level = "미달";
                break;
            default:
                level = "-";
                break;
        }
        return level;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()){
            case R.id.client_changed_graph_btn:
                bundle.putString("client_id", client.getUsername());
                navController.navigate(R.id.action_measurementFinalResultFragment_to_resultPage32, bundle);
                break;
            case R.id.prescription_btn:
                // 처방전 발급
                Log.e(TAG, "처방전 발급 버튼 클릭");
                bundle.putString("userName", client.getName());
                bundle.putIntegerArrayList("checkedIds", checkedIds);
                navController.navigate(R.id.action_measurementFinalResultFragment_to_makePrescriptionFragment, bundle);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fianl_result_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_btn:
                Log.e(TAG, "Share button clicked");
                shareResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 상단 공유하기 버튼을 클릭했을 경우 호출되는 메소드
     */
    public void shareResult(){
        if (scrollView != null){
            Bitmap bitmap = BitmapUtil.getBitmapFromView(scrollView);
            Uri imageUri = saveBitmapToFile(bitmap);
            if (imageUri != null) {
                shareImageFile(imageUri);
            }
        }
    }

    /**
     * Bitmap의 Uri로 반환하는 메소드
     *
     * Bitmap을 캐싱 디렉토리에 저장하고, 해당 파일의 Uri를 반환하도록 함.
     * 캐싱 디렉토리 사용을 위해 Manifest에 FileProvider를 등록 필요
     *
     * @param bitmap
     * @return Uri
     */
    public Uri saveBitmapToFile(Bitmap bitmap) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getActivity().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "report.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(getContext(), "co.kr.myfitnote.fileprovider", file);

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    /**
     * 캐싱 디렉토리에 있는 Uri를 Intent를 담아 공유를 호출하는 메소드
     *
     * @param imageUri
     */
    public void shareImageFile(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}