package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.model.Exercise;
import lombok.SneakyThrows;

public class ExplanationTextFragment extends Fragment implements View.OnClickListener{

    private MeasurementEventListener sendEventListener;
    private String title;
    private String guideMessage;
    private int containerBackgroundColor = Color.parseColor("#000000ff");
    private int buttonBackgroundColor = Color.parseColor("#ffbc64");
    private int visible = View.VISIBLE;

    Timer startTimer;
    //시작시 3초 카운트
    private final int SATRT_TIME_SEC = 3;
    private View view;

    public ExplanationTextFragment(String title){
        this.title = title;
    }

    public ExplanationTextFragment(String title,String guideMessage,  int containerBackgroundColor, int buttonBackgroundColor){
        this.title = title;
        this.containerBackgroundColor = containerBackgroundColor;
        this.buttonBackgroundColor = buttonBackgroundColor;
        this.guideMessage= guideMessage;
    }
    public ExplanationTextFragment(String title,String guideMessage){
        this.title = title;
        this.guideMessage = guideMessage;
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
        view = inflater.inflate(R.layout.fragment_explanation_text, container, false);

//        TextView titleView = view.findViewById(R.id.title_view);
//        titleView.setText(title);

//        TextView guideView = view.findViewById(R.id.guide_message);
//        guideView.setText(guideMessage);

        view.findViewById(R.id.container).setBackgroundColor(containerBackgroundColor);

//        view.findViewById(R.id.leg_test_start_btn).setBackgroundTintList(ColorStateList.valueOf(buttonBackgroundColor));
//
//        view.findViewById(R.id.leg_test_start_btn).setVisibility(visible);

        startTimerTask();

        return view;
    }

    public void setButtonVisible(int visible){
        this.visible = visible;
    }

    private void startTimerTask(){
        sendEventListener.setStatus(Exercise.READY);
        startTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            int count = SATRT_TIME_SEC;
            int minus = 100 / count;
            int max = 100;
            ProgressBar progressBar = view.findViewById(R.id.progressbar);
            TextView timerView = view.findViewById(R.id.timer_view);

            @Override
            public void run()
            {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SneakyThrows
//                    @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            if (count == -1) {
                                startTimer.cancel();
                                progressBar.setVisibility(View.GONE);
                                timerView.setVisibility(View.GONE);
//                            startTestTimerTask();
                                sendEventListener.nextFragment(getArguments());
                            }

                            progressBar.setProgress(
                                    (max < minus) ? 0 : max
                            );
                            timerView.setText(count + "");
                            max = max - minus;
                            count--;


                        }
                    });
                }
            }
        };

        startTimer.schedule(timerTask,0 ,1000);


    }

    private void stopStartTimerTask(){
        if(startTimer != null) startTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopStartTimerTask();
    }


    @Override
    public void onClick(View v) {

    }
}
