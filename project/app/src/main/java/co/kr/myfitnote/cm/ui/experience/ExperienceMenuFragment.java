package co.kr.myfitnote.cm.ui.experience;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import co.kr.myfitnote.R;


public class ExperienceMenuFragment extends Fragment implements View.OnClickListener{

    private NavController navController;

    private LinearLayout cm_ex_singleleg_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_experience_menu, container, false);
        cm_ex_singleleg_btn = view.findViewById(R.id.cm_ex_singleleg_btn);
        cm_ex_singleleg_btn.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cm_ex_singleleg_btn:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isExperience", true);
                navController.navigate(R.id.action_experienceMenuFragment_to_singleLegMeasurementActivity, bundle);
                break;
        }
    }
}