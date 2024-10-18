package co.kr.myfitnote.views.fragments.handraise;


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

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.core.ui.CustomCheckboxItem;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.measurement.MeasurementEventListener;

public class HandRaiseSettingFragment extends Fragment implements View.OnClickListener {
    private String TAG = "HandRaiseSettingFragment";
    private Spinner mLegSpinner;
    private String mTitle;
    private MeasurementEventListener sendEventListener;

    private CustomCheckboxItem handCheckbox;
    private int timer = 30;
    private WheelPicker hourPicker, minutePicker, secondPicker;

    public HandRaiseSettingFragment(String title){
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
        View view = inflater.inflate(R.layout.fragment_hand_raise_setting, container, false);

        handCheckbox = view.findViewById(R.id.hand_checkbox);
        hourPicker = (WheelPicker) view.findViewById(R.id.hour_picker);
        minutePicker = (WheelPicker) view.findViewById(R.id.minute_picker);
        secondPicker = (WheelPicker) view.findViewById(R.id.second_picker);

        List<Integer> hourData = new ArrayList<>();
        List<Integer> minuteData = new ArrayList<>();
        List<Integer> secondData = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            minuteData.add(i);
            secondData.add(i);
            if (i < 24) {
                hourData.add(i);
            }
        }
        hourPicker.setData(hourData);
        minutePicker.setData(minuteData);
        secondPicker.setData(secondData);

        if (timer != 0){
            hourPicker.setSelectedItemPosition(timer / 3600, false);
            minutePicker.setSelectedItemPosition(timer / 60, false);
            secondPicker.setSelectedItemPosition(timer % 60, false);
        }

        view.findViewById(R.id.leg_test_start_btn).setOnClickListener(this);

        return view;
    }

    public boolean validate(String hand_direction){
        if(hand_direction == null){
            new NormalDialogFragment("손 유형을 선택해 주세요.", null, null).show(getFragmentManager(), "dialog");
            return false;
        }
        return true;
    }

    public boolean validate(int time){
        if(time == 0){
            new NormalDialogFragment("시간을 설정해 주세요.", null, null)
                    .show(getFragmentManager(), "dialog");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.leg_test_start_btn:
                Bundle bundle = new Bundle();

                // Calculate hour, minute and second to seconds
                int hour = hourPicker.getCurrentItemPosition();
                int minute = minutePicker.getCurrentItemPosition();
                int second = secondPicker.getCurrentItemPosition();
                int time = hour * 3600 + minute * 60 + second;

                if (!validate(time)){
                    return;
                }

                String hand_direction = handCheckbox.getCheckedItemLabel();
                Log.d(TAG, "hand_direction => " + hand_direction );
                if (!validate(hand_direction)) {
                    return;
                }
                bundle.putString("hand_direction", hand_direction);
                bundle.putInt("time", time);
                Log.e(TAG, "timer => " + time);
//                sendEventListener.nextFragment(bundle);
                sendEventListener.setOptions(bundle);
                break;

        }

    }

}
