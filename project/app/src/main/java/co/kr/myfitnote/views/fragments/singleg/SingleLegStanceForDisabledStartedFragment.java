package co.kr.myfitnote.views.fragments.singleg;

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

public class SingleLegStanceForDisabledStartedFragment extends Fragment implements View.OnClickListener {

    private TextView secondsView;
    private Timer updateViewTimer;
    private int seconds = 0;
    private Button pauseBtn;

    private SendEventListener sendEventListener;

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
        View view = inflater.inflate(R.layout.fragment_single_leg_stance_for_disabled_started, container, false);
        secondsView = view.findViewById(R.id.seconds_view);

        pauseBtn = view.findViewById(R.id.pause_btn);
        view.findViewById(R.id.restart_btn).setOnClickListener(this);
        view.findViewById(R.id.pause_btn).setOnClickListener(this);

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
        if(updateViewTimer != null) return;
        updateViewTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {

            @Override
            public void run()
            {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SneakyThrows
//                    @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            seconds += 1;
                            secondsView.setText(seconds + "");
                        }
                    });
                }
            }
        };

        updateViewTimer.schedule(timerTask,0 ,1000);
    }
    public void stopStartTimerTask(){
        if(updateViewTimer != null){
            updateViewTimer.cancel();
            updateViewTimer = null;
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
            case R.id.restart_btn:
                sendEventListener.restart();
                break;
            case R.id.pause_btn:
                if(pauseBtn.getText().toString().equals("검사중지")){
                    pauseBtn.setText("검사시작");
                }else{
                    pauseBtn.setText("검사중지");
                }
                sendEventListener.pause();
                break;
        }
    }
}