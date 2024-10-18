package co.kr.myfitnote.core.utils;

import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.kr.myfitnote.core.ui.PausableCountDownTimer;

public class UITimerManager {
    /**
     * Timer와 TImer에 시간에 따른 UI 관리하는 클래스
     */
    private String TAG = "UITimerManager";

    private String ZERO_TIME = "0";
    private ProgressBar progressBar;
    private TextView secondsView;

    public UITimerManager(ProgressBar progressBar, TextView secondsView){
        this.progressBar = progressBar;
        this.secondsView = secondsView;
    }

    public PausableCountDownTimer createCountDown(long milsInFuture, long countDownInterval, long delayTimeUntilFinish, Runnable onFinish){
        Log.e(TAG, milsInFuture + " " + countDownInterval + " " + delayTimeUntilFinish);
        progressBar.setMax((int) milsInFuture);

        PausableCountDownTimer countDownTimer = new PausableCountDownTimer(milsInFuture, countDownInterval, delayTimeUntilFinish) {
            @Override
            public void onTick(long millisUntilFinished) {
                // if remaining time is less than 0.10 second, show 0.00
                if (millisUntilFinished < 100) {
                    // when time is over
                    updateSecondView(ZERO_TIME);
                    updateProgressBarValue(0);
                } else {
                    // set text to seconds_view as "00:00"
                    updateSecondView(String.format("%d", millisUntilFinished / 1000));
                }
                updateProgressBarValue((int) (milsInFuture - millisUntilFinished));
            }

            @Override
            public void onFinish() {
                updateProgressBarValue(0);
                onFinish.run();
            }
        };
        return countDownTimer;
    }

    private void updateProgressBarValue(int value){
        /**
         * 주어진 value에 따라 progress bar의 진행도 업데이트
         *
         * @param value : progress bar value
         */
        progressBar.setProgress(value);
    }

    private void updateSecondView(String value){
        /**
         * 주어진 value에 따라 seconds view 업데이트
         *
         * @param value : String value for update value
         */
        secondsView.setText(value);
    }

}