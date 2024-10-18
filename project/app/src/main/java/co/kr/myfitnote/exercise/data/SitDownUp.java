package co.kr.myfitnote.exercise.data;

import static co.kr.myfitnote.core.mlkit.Utils.isShowFullBody;
import static co.kr.myfitnote.core.mlkit.Variable.FrameLikelihood;

import android.util.Log;
import android.widget.ProgressBar;

import com.github.mikephil.charting.data.Entry;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.ui.PausableCountDownTimer;
import co.kr.myfitnote.core.utils.SoundManager;
import co.kr.myfitnote.exercise.Exercise;
import co.kr.myfitnote.exercise.ExerciseParam;
import co.kr.myfitnote.exercise.ExerciseSession;
import co.kr.myfitnote.measurement.ui.seatdownup.SeatDownUpMeasurementActivity;
import co.kr.myfitnote.model.SeatDownUp;

/**
 * 앉았다 일어서기
 */
public class SitDownUp extends Exercise {

    private long startTimesMS = -1;
    private long lastSaveTime = 0;
    private final long DEBOUNCE_TIME = 500;

    private SeatDownUp seatDownUp;

    public SitDownUp(ExerciseSession exerciseSession, ExerciseParam exerciseParam) {
        super(exerciseSession, exerciseParam);
        this.exerciseName = "SitDownUp";
        seatDownUp = new SeatDownUp();
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
