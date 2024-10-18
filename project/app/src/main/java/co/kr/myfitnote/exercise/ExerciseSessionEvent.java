package co.kr.myfitnote.exercise;

import co.kr.myfitnote.core.ui.PausableCountDownTimer;

public interface ExerciseSessionEvent {

    public PausableCountDownTimer startTimer(int milsInFuture, int countDownInterval, int delayTimeUntilFinish, Runnable onFinish);

    public void showNotice(String message);

    /**
     * 운동 카운트 OR 초 업데이트
     * @param count
     * @param set_count
     */
    public void updateCount(int count, int set_count);

    /**
     * 액티비티 로깅 중재
     */
    public void displayLog(String message);

    /**
     * 운동 세션 종료 시
     */
    public void finishSession();

    /**
     * 액티비티 운동 제목 설정
     */
    public void setTitle(String title);

    /**
     * 사운드 출력
     */
    public void playSound(int soundId);

    public void setVideoView(String videoPath, boolean useVideo);
}

