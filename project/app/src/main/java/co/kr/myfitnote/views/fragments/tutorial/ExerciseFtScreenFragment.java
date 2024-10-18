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

public class ExerciseFtScreenFragment extends Fragment {

    private NavController navController;
    LinearLayout exercise_single_leg_stance_test,
                 exercise_single_leg_stance_test_for_disabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_exercise_ft_screen, container, false);

        navController = NavHostFragment.findNavController(this);

        exercise_single_leg_stance_test = rootview.findViewById(R.id.exercise_single_leg_stance_test);
        exercise_single_leg_stance_test_for_disabled = rootview.findViewById(R.id.exercise_single_leg_stance_test_for_disabled);

        exercise_single_leg_stance_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                navController.navigate(R.id.singleLegStanceTestActivity);
                Intent it1 = new Intent(getContext(), SingleLegStanceTestActivity.class);
                it1.putExtra("threshold", 0.2);
                startActivity(it1);
            }
        });

        exercise_single_leg_stance_test_for_disabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                navController.navigate(R.id.singleLegStanceTestActivity);
                Intent it2 = new Intent(getContext(), SingleLegStanceTestActivity.class);
                it2.putExtra("threshold", 0.23);
                startActivity(it2);
            }
        });

        return rootview;
    }
}