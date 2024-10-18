package co.kr.myfitnote.views.fragments.tutorial;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import co.kr.myfitnote.R;
import co.kr.myfitnote.views.TimedUpAndGoTestActivity;
import co.kr.myfitnote.views.WalkTestActivity;


public class Exercise100ScreenFragment extends Fragment implements View.OnClickListener{

    private NavController navController;
    private LinearLayout exercise_2min_step_test,
                         exercise_chair_stand_test,
                         exercise_2min_step_disabled_test,
                         exercise_timedup_and_go_test;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise100_screen, container, false);

        navController = NavHostFragment.findNavController(this);

        exercise_2min_step_test = view.findViewById(R.id.exercise_2min_step_test);
        exercise_chair_stand_test = view.findViewById(R.id.exercise_chair_stand_test);
        exercise_2min_step_disabled_test = view.findViewById(R.id.exercise_2min_step_disabled_test);
        exercise_timedup_and_go_test = view.findViewById(R.id.exercise_timedup_and_go_test);

        exercise_timedup_and_go_test.setOnClickListener(this);


        exercise_2min_step_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                navController.navigate(R.id.walkTestActivity);
                Intent it = new Intent(getContext(), WalkTestActivity.class);
                it.putExtra("threshold", 135);
                it.putExtra("fullAngleThreshold", 165);
                startActivity(it);
            }
        });
        exercise_2min_step_disabled_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), WalkTestActivity.class);
                it.putExtra("threshold", 165);
                it.putExtra("fullAngleThreshold", 170);
                startActivity(it);
            }
        });

        exercise_chair_stand_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.seatDownUpTestActivity);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent it = null;
        switch (v.getId()){
            case R.id.exercise_timedup_and_go_test:
                it = new Intent(getContext(), TimedUpAndGoTestActivity.class);
                break;

        }

        if (it != null){
            startActivity(it);
        }
    }
}