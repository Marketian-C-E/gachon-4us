package co.kr.myfitnote.client.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.client.HomeTodayExerciseView;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.exercise.ExerciseParam;
import co.kr.myfitnote.prescription.data.PrescriptionDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ClientHomeFragment extends Fragment implements View.OnClickListener{
    private String TAG = "ClientHomeFragment";
    private TextView tv_home_header;
    private Client client;
    private Button do_exercise_btn;
    private User user;
    private HomeTodayExerciseView homeTodayExerciseView;
    private LinearLayout homeTodayExerciseViewLayout;

    private NavController navController;

    private ArrayList<PrescriptionDetail>  prescriptionDetail;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    public ClientHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);
        navController = NavHostFragment.findNavController(this);

        client = PreferencesManager.getInstance(getContext()).getClient();
//        user = PreferencesManager.getInstance(getContext()).getUser();
        tv_home_header = view.findViewById(R.id.client_home_title);
        tv_home_header.setText("안녕하세요, "+ client.getName() + "님!");
//        homeTodayExerciseView = view.findViewById(R.id.today_exercise_view);
        homeTodayExerciseViewLayout = view.findViewById(R.id.today_exercise_layout);

        do_exercise_btn = view.findViewById(R.id.do_exercise_btn);
        do_exercise_btn.setOnClickListener(this);

        // Get today prescription
        getTodayPrescription();

        return view;
    }

    public void doExercise(PrescriptionDetail prescriptionDetail) {
        /**
         * 운동 시작 버튼 클릭 시 호출
         */
        // Invoke navigate to other fragment
        Bundle bundle = new Bundle();
        bundle.putString("exercise", prescriptionDetail.getExercise().getName());
        bundle.putInt("count", prescriptionDetail.getCount());
        Log.e(TAG, "처방전 운동 정보 => " + prescriptionDetail.getExercise().getName() + ", " + prescriptionDetail.getCount());
        navController.navigate(R.id.action_clientHomeFragment_to_clientExerciseActivity, bundle);
    }

    /**
     * 운동 시작 버튼 클릭 시 호출
     */
    public void doExercise(ArrayList<PrescriptionDetail> prescriptionDetail) {
        // Invoke navigate to other fragment
        Bundle bundle = new Bundle();

        // 유저 정보 삽입
        bundle.putString("name", client.getName());
        bundle.putString("phone", client.getPhone());
        bundle.putString("birth", client.getBirth_date());

        // 처방전 ID 삽입
        bundle.putInt("prescription_id", prescriptionDetail.get(0).getPrescription_id());

        HashMap<String, ExerciseParam> exercises = new HashMap<>();
        for (PrescriptionDetail detail : prescriptionDetail) {
            // ExerciseParam 데이터 생성 후 bundle에 전달
            ExerciseParam exerciseParam = new ExerciseParam();
            exerciseParam.setExerciseName(detail.getExercise().getName());
            exerciseParam.setCount(detail.getCount());
            exerciseParam.setInterval(detail.getInterval());
            exerciseParam.setSet(detail.getSet());
            exercises.put(detail.getExercise().getName(), exerciseParam);
        }
        bundle.putSerializable("prescriptionDetail", exercises);
//        Log.e(TAG, "처방전 운동 정보 => " + prescriptionDetail);
        navController.navigate(R.id.action_clientHomeFragment_to_labMainActivity, bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.do_exercise_btn:
                if (prescriptionDetail != null){
                    doExercise(prescriptionDetail);
                }
                break;
        }
    }

    public void getTodayPrescription(){
        /**
         * 오늘의 처방을 가져온다.
         */
        service.getClientTodayPrescription("Token " + client.getToken()).enqueue(new Callback<ArrayList<PrescriptionDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<PrescriptionDetail>> call, Response<ArrayList<PrescriptionDetail>> response) {
                if (response.isSuccessful()){
                    prescriptionDetail = response.body();
                    homeTodayExerciseViewLayout.setVisibility(View.VISIBLE);

                    for (PrescriptionDetail detail : prescriptionDetail) {
                        Log.e(TAG, detail.getExercise().getName());
                        // Append homeTodayExerciseView to homeTodayExerciseViewLayout
                        HomeTodayExerciseView homeTodayExerciseView = new HomeTodayExerciseView(getContext());
                        homeTodayExerciseView.setExerciseName(detail.getSummary());
                        homeTodayExerciseView.setClassfication(detail.getExercise().getClassification());
                        homeTodayExerciseViewLayout.addView(homeTodayExerciseView);
                    }


//                    homeTodayExerciseView.setExerciseName(prescriptionDetail.getExercise().getName());
//                    homeTodayExerciseView.setClassfication(prescriptionDetail.getExercise().getClassification());
                } else {
                    Log.e(TAG, "PrescriptionDetail => " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PrescriptionDetail>> call, Throwable t) {
                Log.e(TAG, "ClientTodayPrescription => " + t.getMessage());
            }
        });
    }
}