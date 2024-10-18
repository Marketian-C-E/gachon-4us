package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.model.Exercise;
import lombok.SneakyThrows;

public class TestStartedFragment extends Fragment implements View.OnClickListener {
    private MeasurementEventListener sendEventListener;
    private String title;
    private View view;
    private boolean isFinish = false;



    //검사시간 30초
    private  int TEST_START_TIME_SEC;
    private int increment = -1;
    private int testTimerSec;

    private Timer testTimer;


    public TestStartedFragment(String title, int testTimerSec ){
        this.title = title;
        this.TEST_START_TIME_SEC = testTimerSec;

    }
    public TestStartedFragment(String title){
        this.title = title;
//

    }

    public TestStartedFragment(String title, int increment, int startTimeSec){
        this.title = title;
        TEST_START_TIME_SEC = startTimeSec;
        this.increment = increment;
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
        view = inflater.inflate(R.layout.fragment_test_started, container, false);
        init();
        return view;
    }


    private void init(){

        if(increment == -1){
            TEST_START_TIME_SEC = Integer.parseInt(String.valueOf(getArguments().get("timer")));
        }
        testTimerSec = TEST_START_TIME_SEC * 1000;

        sendEventListener.getTestResult().setSecond(TEST_START_TIME_SEC);

//        TextView titleView = view.findViewById(R.id.title_view);
//        titleView.setText(title);


        TextView testTimerView = view.findViewById(R.id.test_timer_view);
//        testTimerView.setText(TEST_START_TIME_SEC+"");
        testTimerView.setText(String.format("%.2f", TEST_START_TIME_SEC / 1000.0));

        view.findViewById(R.id.restart_btn).setOnClickListener(this);
        view.findViewById(R.id.pause_btn).setOnClickListener(this);



        Toast toast = Toast.makeText(getContext(), "시작", Toast.LENGTH_LONG);
//        ViewGroup group = (ViewGroup)toast.getView();
//        TextView msgTextView = (TextView)group.getChildAt(0);
//        msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
        toast.show();

        startTestTimerTask();
    }

    public void setCount(int count){
        TextView countView = view.findViewById(R.id.count_view);
        countView.setText(count+"");
    }
    public void setHint(String hint){
        if(hint != null){
            TextView hintView = view.findViewById(R.id.hint_view);
            hintView.setText(hint);
        }
    }

    public void setTextView(int TextViewRID, String value){
        TextView tv = view.findViewById(TextViewRID);
        tv.setText(value);
    }




    public void startTestTimerTask(){
         sendEventListener.setStatus(Exercise.START);
         testTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            TextView testTimerView = view.findViewById(R.id.test_timer_view);
            @Override
            public void run(){
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SneakyThrows
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {

                            //                        testTimerView.setText(testTimerSec+"");
                            testTimerView.setText(String.format("%.2f", testTimerSec / 1000.0));
//                            Log.d("TestStartedFragment", increment + "");
                            testTimerSec += increment * 10;
                            //                        if(testTimerSec == -1) {
                            if (testTimerSec <= -1 * 10 && !isFinish) {
                                isFinish = true;
                                testTimer.cancel();
                                sendEventListener.sendDataToServer();
                                sendEventListener.nextFragment();
                            }
                        }
                    });
                }
            }
        };

//        testTimer.schedule(timerTask,0 ,1000);
        testTimer.schedule(timerTask,0 ,10);
    }

    public void stopTestTimerTask(){
        if(testTimer != null)
            testTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTestTimerTask();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.restart_btn:

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
                sendEventListener.restart();
                break;
            case R.id.pause_btn:
                Toast.makeText(getContext(), "클릭", Toast.LENGTH_LONG).show();
                Button pauseButton = view.findViewById(R.id.pause_btn);
                if(sendEventListener.isPause()){
                    pauseButton.setText("검사중지");
                    startTestTimerTask();
                }else{
                    pauseButton.setText("검사시작");
                    stopTestTimerTask();
                }

                sendEventListener.pause();
                break;
        }
    }

    public int getTestTimerSec() {
        return testTimerSec;
    }
}
