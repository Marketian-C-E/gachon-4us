package co.kr.myfitnote.views.pose;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import co.kr.myfitnote.R;

public class PoseMenuFragment extends Fragment implements View.OnClickListener {

    LinearLayout btnFront, btnSide;
    private NavController navController;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        if (getArguments() != null){
            bundle = getArguments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pose_menu, container, false);

        btnFront = view.findViewById(R.id.btnFront);
        btnSide = view.findViewById(R.id.btnSide);

        btnFront.setOnClickListener(this);
        btnSide.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {


//        Intent it;
//        it = new Intent(getContext(), PoseMeasurementActivity.class);
        switch (v.getId()){
            case R.id.btnFront:
                bundle.putString("Type", "Front");
                navController.navigate(R.id.action_poseMenuFragment2_to_poseMeasurementActivity2, bundle);
                break;
            case R.id.btnSide:
                // TODO: Change To Side when side is finished
                bundle.putString("Type", "Side");
                navController.navigate(R.id.action_poseMenuFragment2_to_poseMeasurementActivity2, bundle);
                break;
        }
//        if (it != null) {
//            startActivity(it);
//        }
    }
}