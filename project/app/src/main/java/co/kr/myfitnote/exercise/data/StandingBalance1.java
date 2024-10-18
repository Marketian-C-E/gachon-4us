package co.kr.myfitnote.exercise.data;

import android.util.Log;

import com.google.mlkit.vision.pose.Pose;

import co.kr.myfitnote.exercise.Exercise;
import co.kr.myfitnote.exercise.ExerciseParam;
import co.kr.myfitnote.exercise.ExerciseSession;

/**
 * Standing Balance 1 서서 균형잡기 다리 벌려서
 */
public class StandingBalance1 extends Exercise {

    public StandingBalance1(ExerciseSession exerciseSession, ExerciseParam exerciseParam){
        super(exerciseSession, exerciseParam);
        this.exerciseName = "Standing Balance 1";
    }

    @Override
    public void process(Pose pose) {
        Log.e(exerciseName, exerciseName + " => process is called!" + " " + this.exerciseStatus);
        if (this.exerciseStatus == EXERCISE_STATUS.SETTING) {
            // 운동 준비 상태
            Log.e(exerciseName, "운동 준비 상태");
        }else if (this.exerciseStatus == EXERCISE_STATUS.COUNTING) {
            // 카운팅 중
            Log.e(exerciseName, "카운팅 중");
        }

    }

    @Override
    public void finish() {

    }

    @Override
    public boolean isClear() {
        return false;
    }
}
