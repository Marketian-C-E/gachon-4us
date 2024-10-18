package co.kr.myfitnote.model.pose;

import android.graphics.PointF;
import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.core.utils.ROMUtil;

public class PoseMeasurement {
    static final private String TAG = "PoseMeasurement";
    public enum MEASUREMENT_STATUS {READY, CALIBRATION, DONE_CALIBRATION, FINISH, END};
    private MEASUREMENT_STATUS STATUS = MEASUREMENT_STATUS.READY;

    private int time = 0;
    private int startTime;
    private int endTime;
    private Timer timer;
    private TimerTask timerTask;

    private Pose pose;
    private PointF t1PointF, t2PointF, criteriaPointF;

    private boolean useRight;

    public PoseMeasurement(){
        time = 5 * 1000;
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(String pose) {

        if (pose.equals("Front")){
            this.pose = new FrontPose(pose);
        } else if (pose.equals("Side")) {
            this.pose = new SidePose(pose);
        } else {
            Log.e(TAG, "setPose: Pose is not defined");
        }
    }

    public ArrayList<PointF> getStartPoints() {
        /**
         * 자세 측정을 클릭했을 때 사용자의 좌표 정보
         */
        return new ArrayList<PointF>(Arrays.asList(criteriaPointF, t2PointF));
    }

    public ArrayList<PointF> getCriteriaPoints() {
        return new ArrayList<PointF>(Arrays.asList(t1PointF, criteriaPointF, t2PointF));
    }


    public void calibration(){

    }

    public void start(){

    }

    public void finish(){
        STATUS = MEASUREMENT_STATUS.END;
    }

    public void restart(){
        STATUS = MEASUREMENT_STATUS.READY;
        t1PointF = null;
        t2PointF = null;
    }

    public MEASUREMENT_STATUS getSTATUS() {
        return STATUS;
    }

    public double calcAngle(){
        return ROMUtil.calcAngleFromThreePoints(getCriteriaPoints());
    }


}
