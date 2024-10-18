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
import co.kr.myfitnote.views.SingleLegStanceTestActivity;
import co.kr.myfitnote.views.WalkTestActivity;

public class ExerciseDaekyo extends Fragment implements View.OnClickListener {

    private NavController navController;
    LinearLayout exercise_daekyo_position_measurement, exercise_daekyo_rom,
            exercise_daekyo_singleleg, exercise_daekyo_seatedUp, exercise_daekyo_2min;

    public ExerciseDaekyo() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_daekyo, container, false);

        navController = NavHostFragment.findNavController(this);

        exercise_daekyo_position_measurement = view.findViewById(R.id.exercise_daekyo_position_measurement);
        exercise_daekyo_rom = view.findViewById(R.id.exercise_daekyo_rom);
        exercise_daekyo_singleleg = view.findViewById(R.id.exercise_daekyo_singleleg);
        exercise_daekyo_seatedUp = view.findViewById(R.id.exercise_daekyo_seatedUp);
        exercise_daekyo_2min = view.findViewById(R.id.exercise_daekyo_2min);

        exercise_daekyo_position_measurement.setOnClickListener(this);
        exercise_daekyo_rom.setOnClickListener(this);
        exercise_daekyo_singleleg.setOnClickListener(this);
        exercise_daekyo_seatedUp.setOnClickListener(this);
        exercise_daekyo_2min.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exercise_daekyo_rom:
                navController.navigate(R.id.action_exerciseDaekyo_to_romMenuFragment);
                break;

            case R.id.exercise_daekyo_singleleg:
                Intent it1 = new Intent(getContext(), SingleLegStanceTestActivity.class);
                it1.putExtra("threshold", 0.2);
                startActivity(it1);
                break;

            case R.id.exercise_daekyo_seatedUp:
                navController.navigate(R.id.seatDownUpTestActivity);
                break;

            case R.id.exercise_daekyo_2min:
                Intent it2 = new Intent(getContext(), WalkTestActivity.class);
                it2.putExtra("threshold", 135);
                it2.putExtra("fullAngleThreshold", 165);
                startActivity(it2);
                break;

            case R.id.exercise_daekyo_position_measurement:
                navController.navigate(R.id.action_exerciseDaekyo_to_poseMenuFragment);
                break;

        }
    }
}