package co.kr.myfitnote.exercise;

import android.util.Log;

import com.google.mlkit.vision.pose.Pose;

/**
 * 각 운동 항목의 추상 클래스
 */
abstract public class Exercise {
    protected String exerciseName; // 운동 이름
    protected ExerciseSession exerciseSession; // 운동 세션
    protected boolean isClear = false;

    protected ExerciseParam exerciseParam; // 운동 파라미터

    public Exercise(ExerciseSession exerciseSession, ExerciseParam exerciseParam){
        logging("Exercise constructor called!");
        this.exerciseSession = exerciseSession;
        this.exerciseParam = exerciseParam;
    }

    // 현재 운동 진행 상태 (설정, 카운팅, 캘리브레이션)
    protected enum EXERCISE_STATUS {
        SETTING,
        COUNTING,
        EXERCISE_READY,
        CALIBRATION,
        CALIBRATIONING,
        EXERCISE_START,
        EXERCISE_STARTING,
        EXERCISE_PAUSE,
        EXERCISE_FINISH
    }
    protected EXERCISE_STATUS exerciseStatus = EXERCISE_STATUS.EXERCISE_READY;

    // 종료 조건 유형 : TIME일 경우 시간 (홀딩), COUNT일 경우 카운트로 종료 조건 판단
    private enum FINISH_CONDITION_TYPE {
        TIME,
        COUNT
    };
    private boolean useCalibration; // 캘리브레이션 사용 여부
    private int calibrationTime; // 캘리브레이션 시간

    private int exerciseTime = 0; // 총 운동 시간
    private int exercisePurposeTime = 0; // 목표 운동 시간

    /**
     * PoseData 값을 넘겨 받아 운동에 대한 로직을 수행하는 함수
     */
    abstract public void process(Pose pose);

    /**
     * 해당 운동 종료 시 처리 로직
     */
    abstract public void finish();

    /**
     * 운동 로직에 의해 운동이 종료되었는지를 판단하는 함수
     * @return boolean
     */
    abstract public boolean isClear();

    public String getExerciseName(){
        return exerciseName;
    }

    public void logging(String message){
        Log.d(this.exerciseName, message);
    }
}
