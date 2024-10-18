package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import lombok.SneakyThrows;

public class ReadyFragment extends Fragment {

    private MeasurementEventListener sendEventListener;
    private String title;
    private String guideMessage;
    private int containerBackgroundColor = Color.parseColor("#000000ff");
    private int buttonBackgroundColor = Color.parseColor("#ffbc64");
    private int visible = View.VISIBLE;
    private final int SATRT_TIME_SEC = 3;

    private Timer startTimer;
    private View view;

    public ReadyFragment(String title, String guideMessage){
        this.title = title;
        this.guideMessage = guideMessage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sing_leg_stance_ready, container, false);
        view.findViewById(R.id.container).setBackgroundColor(containerBackgroundColor);
        startTimerTask();

        return view;
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

    public void setButtonVisible(int visible){
        this.visible = visible;
    }

    private void startTimerTask(){

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
}