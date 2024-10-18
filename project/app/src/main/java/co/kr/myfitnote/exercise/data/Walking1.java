package co.kr.myfitnote.exercise.data;

import static co.kr.myfitnote.core.mlkit.Utils.getLeftKneeAngle;
import static co.kr.myfitnote.core.mlkit.Utils.getRightKneeAngle;
import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.exercise.Exercise;
import co.kr.myfitnote.exercise.ExerciseParam;
import co.kr.myfitnote.exercise.ExerciseSession;
import co.kr.myfitnote.measurement.data.WalkYCriteria;

/**
 * 제자리 걷기
 */
public class Walking1 extends Exercise {

    private long lastSaveTime = 0;
    private final long DEBOUNCE_TIME = 200;
    private long startTimesMS = -1;

    private WalkYCriteria walkYCriteria;


    public Walking1(ExerciseSession exerciseSession, ExerciseParam exerciseParam){
        super(exerciseSession, exerciseParam);
        this.exerciseName = "Walking1";
        walkYCriteria = new WalkYCriteria();
        walkYCriteria.setThreshold(0.3f);
    }

    @Override
    public void process(Pose poseData) {
        if (poseData == null){
            return;
        }

        /** Section Of SDK **/
    }

    @Override
    public void finish() {
        exerciseStatus = EXERCISE_STATUS.EXERCISE_FINISH;
        isClear = true;
    }

    @Override
    public boolean isClear() {
        return isClear;
    }
}
