package co.kr.myfitnote.views.fragments.gaitspeed;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import lombok.SneakyThrows;


public class GaitSpeedStartedFragment extends Fragment implements View.OnClickListener{
    private Timer timer;
    private TextView secondsView;
    private int seconds = 0;
    private Button startBtn, endBtn;

    private SendEventListener sendEventListener;
    private boolean isFinish = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            sendEventListener = (SendEventListener) context;
        }catch (ClassCastException e){
            new ClassCastException(context.toString()+" must implement SendEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gait_speed_started, container, false);

        secondsView = view.findViewById(R.id.seconds_view);
        endBtn = view.findViewById(R.id.end_btn);
        startBtn = view.findViewById(R.id.start_btn);

        endBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);

        seconds = 0;

        return view;
    }

    public int getSeconds(){
        return seconds;
    }

    public void setSeconds(int seconds){
        this.seconds = seconds;
    }

    public void startTimerTask(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask()
        {

            @Override
            public void run()
            {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            seconds += 10;
                            secondsView.setText(String.format("%.2f", seconds / 1000.0));
                        }
                    });
                }
            }
        };

        timer.schedule(timerTask,0 ,10);
    }
    public void stopStartTimerTask(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopStartTimerTask();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_btn:
                startTimerTask();
                break;
            case R.id.end_btn:
                if (isFinish) return;
                isFinish = true;
                stopStartTimerTask();
                Bundle args = new Bundle();
                args.putDouble("second", seconds);
                sendEventListener.nextFragment(args);
                break;
        }
    }
}