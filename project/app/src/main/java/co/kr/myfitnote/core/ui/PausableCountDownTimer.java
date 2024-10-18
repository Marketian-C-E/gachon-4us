package co.kr.myfitnote.core.ui;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;

public abstract class PausableCountDownTimer {
    private long setMillisInFuture; // 설정 시간
    private long millisInFuture;
    private long countDownInterval;
    private long stopTimeInFuture;
    private long delayTimeUntilFinish = 0;
    private boolean paused = true;

    private CountDownTimer countDownTimer;

    public PausableCountDownTimer(long millisInFuture, long countDownInterval) {
        // millisInFuture : 카운트 다운 초
        // countDownInterval : 카운트 다운 간격
        this.setMillisInFuture = millisInFuture;
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
    }

    public PausableCountDownTimer(long millisInFuture, long countDownInterval, long delayTimeUntilFinish) {
        /**
         * @param millisInFuture : 카운트 다운 초
         * @param countDownInterval : 카운트 다운 간격
         * @param delayTimeUntilFinish : OnFinsh 함수 호출까지의 딜레이 시간
         */
        this.setMillisInFuture = millisInFuture;
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.delayTimeUntilFinish = delayTimeUntilFinish;
    }

    public final void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        this.paused = true;
    }

    public final void pause() {
        if (paused) return;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        this.paused = true;
    }

    public void resume() {
        if (!paused) return;

        paused = false;
        if (millisInFuture <= 0) {
            onFinish();
            return;
        }
        stopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture;

        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                millisInFuture = millisUntilFinished;
                PausableCountDownTimer.this.onTick(millisUntilFinished);
            }

            public void onFinish() {
                // 딜레이 시간이 있을 경우 딜레이 시간만큼 대기 후 onFinish 호출
                if (delayTimeUntilFinish > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PausableCountDownTimer.this.onFinish();
                        }
                    }, delayTimeUntilFinish);
                } else
                    PausableCountDownTimer.this.onFinish();
                }
        }.start();
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();

    public boolean isPaused() {
        return paused;
    }

    // get remain time
    public long getRemainTime() {
        return millisInFuture;
    }

    // get passed time
    public long getPassedTime() {
        return setMillisInFuture - millisInFuture;
    }
}