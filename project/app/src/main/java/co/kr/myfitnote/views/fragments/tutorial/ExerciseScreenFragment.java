package co.kr.myfitnote.views.fragments.tutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import co.kr.myfitnote.R;
import co.kr.myfitnote.model.User;

public class ExerciseScreenFragment extends Fragment {

    private NavController navController;
    private TextView tv_es_header;
    LinearLayout exerciseSppb, exerciseSft, exerciseFt, exericse100, company_daekyo_btn;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        SharedPreferences pref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userData = pref.getString("userData", "haven't token yet");
        user = gson.fromJson(userData, User.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_exercise_screen, container, false);

        navController = NavHostFragment.findNavController(this);

        tv_es_header = rootview.findViewById(R.id.tv_es_header);
        exerciseSppb = rootview.findViewById(R.id.exercise_sppb);
        exerciseSft = rootview.findViewById(R.id.exercise_sft);
        exerciseFt = rootview.findViewById(R.id.exercise_ft);
        exericse100 = rootview.findViewById(R.id.exercise_100);

        // For daekyo
        company_daekyo_btn = rootview.findViewById(R.id.company_daekyo_btn);
        company_daekyo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_exerciseScreenFragment_to_exerciseDaekyo);
            }
        });

        tv_es_header.setText(user.getName() + "님의 신체기능검사 항목을\n선택해 주세요.");


        exerciseSft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_exerciseScreenFragment_to_exerciseSftScreenFragment);
            }
        });

        exerciseFt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_exerciseScreenFragment_to_exerciseFtScreenFragment);
            }
        });

        exericse100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_exerciseScreenFragment_to_exercise100ScreenFragment);
            }
        });

        return rootview;
    }
}