package co.kr.myfitnote.views.rom;

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

public class RomMenuFragment extends Fragment implements View.OnClickListener {

    LinearLayout btnRomUpLeft, btnRomUpRight, btnRomDownLeft, btnRomDownRight;
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
        View view = inflater.inflate(R.layout.fragment_rom_menu, container, false);

        btnRomUpLeft = view.findViewById(R.id.btnRomUpLeft);
        btnRomUpRight = view.findViewById(R.id.btnRomUpRight);
        btnRomDownLeft = view.findViewById(R.id.btnRomDownLeft);
        btnRomDownRight = view.findViewById(R.id.btnRomDownRight);

        btnRomUpLeft.setOnClickListener(this);
        btnRomUpRight.setOnClickListener(this);
        btnRomDownLeft.setOnClickListener(this);
        btnRomDownRight.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
//        Intent it;
//        it = new Intent(getContext(), ROMMeasurementActivity.class);
        switch (v.getId()){
            case R.id.btnRomUpLeft:
                bundle.putString("ROMType", "Up|Left");
                navController.navigate(R.id.action_romMenuFragment2_to_ROMMeasurementActivity, bundle);
//                it.putExtra("ROMType", "Up|Left");
                break;
            case R.id.btnRomUpRight:
                bundle.putString("ROMType", "Up|Right");
                navController.navigate(R.id.action_romMenuFragment2_to_ROMMeasurementActivity, bundle);
//                it.putExtra("ROMType", "Up|Right");
                break;
            case R.id.btnRomDownLeft:
                bundle.putString("ROMType", "Down|Left");
                navController.navigate(R.id.action_romMenuFragment2_to_ROMMeasurementActivity, bundle);
//                it.putExtra("ROMType", "Down|Left");
                break;
            case R.id.btnRomDownRight:
                bundle.putString("ROMType", "Down|Right");
                navController.navigate(R.id.action_romMenuFragment2_to_ROMMeasurementActivity, bundle);
//                it.putExtra("ROMType", "Down|Right");
                break;
        }
//        if (it != null) {
//            startActivity(it);
//        }
    }
}