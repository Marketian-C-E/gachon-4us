package co.kr.myfitnote.cm.ui.measurement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.views.SingleLegStanceTestActivity;

public class MeasurementMenuFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MeasurementMenuFragment";
    private String ARG_NAME = "name";
    private String ARG_PHONE = "phone";

    private NavController navController;
    private String name, phone;
    private TextView headerTv;
    private LinearLayout romBtn, positionMeasurementBtn, singlelegBtn, lowermfBtn, uppermfBtn, cardioBtn, stepBtn;

    private Bundle bundle;

    public static MeasurementMenuFragment newInstance(String param1, String param2) {
        MeasurementMenuFragment fragment = new MeasurementMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            phone = getArguments().getString(ARG_PHONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measurement_menu, container, false);

        headerTv = view.findViewById(R.id.cm_ms_header_tv);
        headerTv.setText(name + "님의 신체기능검사를\n시작합니다.");

        romBtn = view.findViewById(R.id.cm_ms_rom_btn);
        positionMeasurementBtn = view.findViewById(R.id.cm_ms_position_measurement_btn);
        singlelegBtn = view.findViewById(R.id.cm_ms_singleleg_btn);
        lowermfBtn = view.findViewById(R.id.cm_ms_lower_msucle_function_btn);
        uppermfBtn = view.findViewById(R.id.cm_ms_upper_msucle_function_btn);
        cardioBtn = view.findViewById(R.id.cm_ms_cardiorespiratory_function_btn);
//        stepBtn = view.findViewById(R.id.cm_ms_step_test_btn);

        romBtn.setOnClickListener(this);
        positionMeasurementBtn.setOnClickListener(this);
        singlelegBtn.setOnClickListener(this);
        lowermfBtn.setOnClickListener(this);
        uppermfBtn.setOnClickListener(this);
        cardioBtn.setOnClickListener(this);
//        stepBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cm_ms_rom_btn:
                navController.navigate(R.id.action_measurementMenuFragment_to_romMenuFragment2, getArguments());
                break;
            case R.id.cm_ms_position_measurement_btn:
                navController.navigate(R.id.action_measurementMenuFragment_to_poseMenuFragment2, getArguments());
                break;
            case R.id.cm_ms_lower_msucle_function_btn:
                navController.navigate(R.id.action_measurementMenuFragment_to_seatDownUpMeasurementActivity, getArguments());
                break;
            case R.id.cm_ms_upper_msucle_function_btn:
                navController.navigate(R.id.action_measurementMenuFragment_to_handraiseMeasurementActivity, getArguments());
                break;
            case R.id.cm_ms_cardiorespiratory_function_btn:
                navController.navigate(R.id.action_measurementMenuFragment_to_walkMeasurementActivity, getArguments());
                break;
            case R.id.cm_ms_singleleg_btn:
                navController.navigate(R.id.action_measurementMenuFragment_to_singleLegMeasurementActivity, getArguments());
                break;
        }
    }
}