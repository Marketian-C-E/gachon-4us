package co.kr.myfitnote.views.fragments.singleg;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.core.ui.CustomCheckboxItem;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.measurement.MeasurementEventListener;

public class SingleLegStanceSettingFragment extends Fragment implements View.OnClickListener {
    private String TAG = "SingleLegStanceSettingFragment";
    private Spinner mLegSpinner;
    private Spinner mEyeSpinner;
    private String mTitle;
    private MeasurementEventListener sendEventListener;

    private CustomCheckboxItem eyeCheckbox;
    private CustomCheckboxItem legCheckbox;

    public SingleLegStanceSettingFragment(String title){
        mTitle = title;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            sendEventListener = (MeasurementEventListener) context;
        }catch (ClassCastException e){
            new ClassCastException(context.toString()+" must implement SendEventListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_leg_stance_setting, container, false);

        eyeCheckbox = view.findViewById(R.id.eye_checkbox);
        legCheckbox = view.findViewById(R.id.leg_checkbox);

        //제목 설정
        // TextView titleView = view.findViewById(R.id.title_view);
        // titleView.setText(mTitle);


        //왼발/오른발 스피너 설정
//        mLegSpinner = view.findViewById(R.id.leg_spinner);
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.leg_spinner_list, R.layout.spinner_item);
//        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        mLegSpinner.setAdapter(adapter);
//
        //눈뜨고/눈감고 스피너 설정
//        mEyeSpinner = view.findViewById(R.id.eye_spinner);
//        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.eye_spinner_list, R.layout.spinner_item);
//        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        mEyeSpinner.setAdapter(adapter1);


        view.findViewById(R.id.leg_test_start_btn).setOnClickListener(this);
        return view;
    }

    public boolean validate(String eyeType, String legType) {
        if (eyeType == null) {
            new NormalDialogFragment("눈 옵션을 선택해 주세요.", null, null)
                    .show(getFragmentManager(), "dialog");
            return false;
        }

        if (legType == null) {
            new NormalDialogFragment("다리 옵션을 선택해 주세요.", null, null)
                    .show(getFragmentManager(), "dialog");
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.leg_test_start_btn:
                String eyeType = eyeCheckbox.getCheckedItemLabel();
                String legType = legCheckbox.getCheckedItemLabel();

                if (!validate(eyeType, legType)){
                    return;
                }

                if (legType.equals("왼발들기")){
                    legType = "왼발";
                }else{
                    legType = "오른발";
                }

                Bundle bundle = new Bundle();

                bundle.putString("eye", eyeCheckbox.getCheckedItemLabel());
                bundle.putString("leg", legType);
//                sendEventListener.nextFragment(bundle);
                sendEventListener.setOptions(bundle);
                break;
        }

    }
}
