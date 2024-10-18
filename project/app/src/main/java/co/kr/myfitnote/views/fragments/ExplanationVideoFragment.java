package co.kr.myfitnote.views.fragments;

//import android.media.session.MediaController;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.measurement.MeasurementEventListener;

/**
 * 설명영상 프래그먼트
 */
public class ExplanationVideoFragment extends Fragment implements View.OnClickListener{
    private String TAG = "ExplanationVideoFragment";
    private View view;
    private String title;
    private String videoUri;
    private MeasurementEventListener sendEventListener;
    private int timer = 30;
    private int containerBackgroundColor = Color.parseColor("#000000ff");
    private int buttonBackgroundColor = Color.parseColor("#00A99F");
    private int visible = View.GONE;
    private boolean timeVisible = true;

    private WheelPicker hourPicker, minutePicker, secondPicker;
    private TextView explain_tv;
    private String explainText;


    public ExplanationVideoFragment(String title, String videoUri){
        this.title = title;
        this.videoUri = videoUri;
        timeVisible = false;
    }

    public ExplanationVideoFragment(String title, String videoUri, int timer){
        this.title = title;
        this.videoUri = videoUri;
        this.timer = timer;
    }

    public ExplanationVideoFragment(String title, String videoUri, int timer, String explainText){
        this.title = title;
        this.videoUri = videoUri;
        this.timer = timer;
        this.explainText = explainText;
    }


    // 게임용 프로그먼트 생성자
    public ExplanationVideoFragment(String title, String videoUri, int containerBackgroundColor, int buttonBackgroundColor, int timer){
        this.title = title;
        this.videoUri = videoUri;
        this.containerBackgroundColor = containerBackgroundColor;
        this.buttonBackgroundColor = buttonBackgroundColor;
        this.timer = timer;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_explanation_video, container, false);

        hourPicker = (WheelPicker) view.findViewById(R.id.hour_picker);
        minutePicker = (WheelPicker) view.findViewById(R.id.minute_picker);
        secondPicker = (WheelPicker) view.findViewById(R.id.second_picker);
        explain_tv = (TextView) view.findViewById(R.id.explain_tv);

        // 설명 문구 여부
        if (!explainText.equals("")) {
            explain_tv.setText(explainText);
        } else {
            explain_tv.setVisibility(View.GONE);
        }

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

        view.findViewById(R.id.container).setBackgroundColor(containerBackgroundColor);
        view.findViewById(R.id.leg_test_start_btn).setBackgroundTintList(ColorStateList.valueOf(buttonBackgroundColor));

//        EditText editText = view.findViewById(R.id.timer_edit);
//        editText.setText(timer+"");

        return view;
    }

    public boolean validate(int time){
        if(time == 0){
            new NormalDialogFragment("시간을 설정해 주세요.", null, null)
                    .show(getFragmentManager(), "dialog");
            return false;
        }
        return true;
    }

    public void stop(){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

//                EditText editText = view.findViewById(R.id.timer_edit);
//                bundle.putString("timer", editText.getText().toString());
                bundle.putInt("time", time);
                Log.e(TAG, "timer => " + time);
                sendEventListener.setOptions(bundle);
//                sendEventListener.nextFragment(bundle);
                break;
        }
    }
}
