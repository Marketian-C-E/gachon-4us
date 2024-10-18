package co.kr.myfitnote.exercise;

import android.util.Log;

import com.google.mlkit.vision.pose.Pose;

import java.util.ArrayList;

import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.ui.NoticeView;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.exercise.enums.ExerciseSessionStatus;

/**
 * 운동 세션
 * - 운동 세션은 여러 운동을 가진 개념으로서, 각 운동 진행을 관리함
 */
public class ExerciseSession {
    private static final String TAG = "ExerciseSession";
    private static int BREAK_TIME = 30000;
    private ExerciseSessionEvent context;
    private int currentExerciseIndex = 0; // 현재 운동 인덱스
    private ExerciseSessionStatus status = ExerciseSessionStatus.READY; // 운동 세션 종료 여부
    public NoticeView noticeView;

    public PausableCountDownTimer currentTimer;

    /**
     * 생성자
     * - 운동 세션을 생성할 때, 컨텍스트를 활용 (해당 컨텍스트는 클래스의 호출에서 타이머, 텍스트 뷰를 제어하기 위함)
     *
     * @param context
     */
    public ExerciseSession(ExerciseSessionEvent context){
        this.context = context;
    }

    public ExerciseSession(ExerciseSessionEvent context, NoticeView noticeView){
        this.context = context;
        this.noticeView = noticeView;
    }


    // 해당 운동에서의 수행 운동 목록
    private ArrayList<Exercise> exerciseList = new ArrayList<>();

    public void process(Pose pose, GraphicOverlay graphicOverlay){

        if (status == ExerciseSessionStatus.READY){
            readyForExercise();
            return;
        }

        if (status == ExerciseSessionStatus.FINISH || status == ExerciseSessionStatus.BREAK){
            displayLog("ExerciseSession is " + status);
            return;
        }

        if (pose == null){
            return;
        }

        Exercise currentExercise = getCurrentExercise();

        currentExercise.process(pose);

        // 운동이 종료되었을 경우 다음 운동으로 진행
        if (currentExercise.isClear()){
            nextExercise();
        }
    }

    /**
     * 쉬는 시간
     */
    public void breakTime(){
        this.status = ExerciseSessionStatus.BREAK;
        showNotice("30초간 휴식을 취해 주세요.");
        startTimer(BREAK_TIME, 100, 0, () -> {
//            this.status = ExerciseSessionStatus.START;
            status = ExerciseSessionStatus.READY;
            showNotice(getCurrentExercise().exerciseName + " 운동을 시작합니다.");
        });
    }

    /**
     * 운동 추가
     * @param exercise
     */
    public void addExercise(Exercise exercise){
        exerciseList.add(exercise);
    }

    /**
     * 현재 운동 반환
     */
    public Exercise getCurrentExercise(){
        return exerciseList.get(currentExerciseIndex);
    }

    /**
     * 다음 운동으로 변경
     */
    public void nextExercise() {
        currentExerciseIndex++;
        if (currentExerciseIndex >= exerciseList.size()){
            finishSession();
        } else {
            // readyForExercise();
            // 다음 운동이 있는 경우 준비 처리
            breakTime();
        }
    }

    /**
     * 운동 준비
     * N번 운동 종료 시, 운동 정보 초기화 (카운트 등)
     */
    public void readyForExercise() {
        updateCount(0, getCurrentExercise().exerciseParam.getCount());
        setTitle(getCurrentExercise().exerciseParam.getExerciseName());
        setVideoView("test", true);
        status = ExerciseSessionStatus.START;

//        if (currentExerciseIndex == 0){
//            // 처음 진입한 경우 운동 시작 처리, 처음이 아닌 경우에는 nextExercise에서 처리
//            status = ExerciseSessionStatus.START;
//        }
    }

    /**
     * 운동 세션 종료
     */
    public void finishSession(){
        this.status = ExerciseSessionStatus.FINISH;
        showNotice("금일 모든 운동이 끝났습니다.\n수고하셨습니다!");
        // 결과 페이지 이동
        context.finishSession();
    }

    /**
     * Activity - subclass 사이 중개 함수
     */
    public void startTimer(int milsInFuture, int countDownInterval, int delayTimeUntilFinish, Runnable onFinish){
        Log.e(TAG, "startTimer called in ExerciseSession class");
        currentTimer = context.startTimer(milsInFuture, countDownInterval, delayTimeUntilFinish, onFinish);
        currentTimer.resume();
    }

    public void showNotice(String message){
        context.showNotice(message);
    }

    public void updateCount(int currentCount, int setCount){
        context.updateCount(currentCount, setCount);
    }

    public void displayLog(String message){
        context.displayLog(message);
    }

    public void setTitle(String title){
        context.setTitle(title);
    }

    public void playSound(int soundId){
        context.playSound(soundId);
    }

    public void setVideoView(String videoPath, boolean useVideo){
        context.setVideoView(videoPath, useVideo);
    }

}
