package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.model.TestLog;
import co.kr.myfitnote.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CmprhResultExtractionFragment extends Fragment implements View.OnClickListener {
    static final private String TAG = "CMPRH";
    private NavController navController;
    private CheckBox singLegStanceCheckbox;
    private CheckBox sitDownUpCheckbox;
    private CheckBox walkCheckbox;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    SharedPreferences pref;
    String userData;
    User user;

    View rootView;

    HashMap<String, TestLog> finalData = new HashMap<>();

    private ArrayList<TestLog> testList;


    public CmprhResultExtractionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cmprh_result_extraction, container, false);

        pref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "");
        user = gson.fromJson(userData, User.class);

        navController = NavHostFragment.findNavController(this);

        getTestItems();

//        //외발서기
//        rootView.findViewById(R.id.single_leg_stance_test_btn).setOnClickListener(this);
//        singLegStanceCheckbox = rootView.findViewById(R.id.single_leg_stance_test_checkbox);
//        singLegStanceCheckbox.setOnClickListener(this);
//
//        //앉았다 일어서기
//        rootView.findViewById(R.id.sit_down_up_btn).setOnClickListener(this);
//        sitDownUpCheckbox = rootView.findViewById(R.id.sit_down_up_checkbox);
//        sitDownUpCheckbox.setOnClickListener(this);
//
//        //제자리 걸기
//        rootView.findViewById(R.id.walk_btn).setOnClickListener(this);
//        walkCheckbox = rootView.findViewById(R.id.walk_checkbox);
//        walkCheckbox.setOnClickListener(this);

        // 결과추출 버튼
        rootView.findViewById(R.id.result_extraction_btn).setOnClickListener(this);

        return rootView;
    }

    void getTestItems(){
        new Thread(() -> {
            service.getUserTest(user.getToken()).enqueue(new Callback<ArrayList<TestLog>>() {
                @Override
                public void onResponse(Call<ArrayList<TestLog>> call, Response<ArrayList<TestLog>> response) {
                    if (response.isSuccessful()) {
                        // Test list retrieved successfully
                        testList = response.body();
                        renderTestItems();
                        Log.e(TAG, testList.size() + "of size!");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<TestLog>> call, Throwable t) {
                    // Handle failure
                    // ...
                    Log.e(TAG, "ERROR:" + t.getMessage());
                }
            });
        }).start();
    }

    private void renderTestItems() {
        LinearLayout testContainer = rootView.findViewById(R.id.test_container);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // Clear the existing views in the container
        testContainer.removeAllViews();

        // Iterate over the testList and create a view for each test item
        for (TestLog test : testList) {
            View itemView = inflater.inflate(R.layout.test_item, testContainer, false);

            // Find and set the data for each view in the item layout
            TextView exerciseTextView = itemView.findViewById(R.id.exercise_text_view);
//            TextView gradeTextView = itemView.findViewById(R.id.grade_text_view);
            TextView createdAtTextView = itemView.findViewById(R.id.created_at_text_view);

            exerciseTextView.setText(test.getExercise() + " (" + test.getUser() + ")");
//            gradeTextView.setText(test.getGrade());
            createdAtTextView.setText(test.getCreated_at());

            CheckBox cb = itemView.findViewById(R.id.single_leg_stance_test_checkbox);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cb.isChecked()) {
                        finalData.put(test.getId(), test);
                    }else{
                        finalData.remove(test.getId());
                    }
                }
            });

            // Add the item view to the container
            testContainer.addView(itemView);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.result_extraction_btn:
                Bundle data = new Bundle();
                Gson gson = new Gson();
                data.putString("finalData", gson.toJson(finalData));
                navController.navigate(R.id.action_cmprhResultExtractionFragment_to_cmprhResultFragment, data);
                break;
        }
    }
}