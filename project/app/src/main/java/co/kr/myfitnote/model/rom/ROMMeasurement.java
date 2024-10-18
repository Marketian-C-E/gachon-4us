package co.kr.myfitnote.model.rom;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.core.utils.ROMUtil;

public class ROMMeasurement {
    static final private String TAG = "ROMMeasurement";
    public enum MEASUREMENT_STATUS {READY, CALIBRATION, START, STARTING, FINISH, END};
    private MEASUREMENT_STATUS STATUS = MEASUREMENT_STATUS.READY;

    private int CALIBRATION_TIME = 3;
    private int time = 0;
    private int startTime;
    private int endTime;
    private Timer timer;
    private TimerTask timerTask;

    private int calculatedAngle;


    private ROM rom;
    private PointF tCriterionPointF, t1PointF, t2PointF;

    public ROMMeasurement(){
        time = CALIBRATION_TIME * 1000;
    }

    public ROM getRom() {
        return rom;
    }

    public void setRom(ROM rom) {
        this.rom = rom;
    }

    public ArrayList<Integer> getJoinPoints() {
        return this.rom.getJoinPoints();
    }

    public ArrayList<PointF> getThreePointFList() {
        return new ArrayList<PointF>(Arrays.asList(tCriterionPointF, t1PointF, t2PointF));
    }


    public void calibration(){
        /***
         * 3초간 Calibration 진행
         */
        STATUS = MEASUREMENT_STATUS.CALIBRATION;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        time -= 10;
                        if (time < 0){
                            STATUS = MEASUREMENT_STATUS.START;
                            timer.cancel();
                            timer = null;
                        }
                    }
                };
                timer.schedule(timerTask, 0, 10);
            }
        });
    }

    public void start(){
        STATUS = MEASUREMENT_STATUS.STARTING;

        time = 5 * 1000;
        // do something -> change finish
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        time -= 10;
                        if (time < 0){
                            STATUS = MEASUREMENT_STATUS.FINISH;
                            timer.cancel();
                            timer = null;
                        }
                    }
                };
                timer.schedule(timerTask, 0, 10);
            }
        });
    }

    public void finish(){
        STATUS = MEASUREMENT_STATUS.END;
    }

    public void restart(){
        STATUS = MEASUREMENT_STATUS.READY;
        time = CALIBRATION_TIME * 1000;
        tCriterionPointF = null;
        t1PointF = null;
        t2PointF = null;
    }

    public MEASUREMENT_STATUS getSTATUS() {
        return STATUS;
    }

    public void calcStartPointF(Pose poseData, int height){
        /**
         * when height is served
         */
        try {
            tCriterionPointF = new PointF(poseData.getPoseLandmark(this.rom.getJoinPoints().get(0)).getPosition().x,
                    poseData.getPoseLandmark(this.rom.getJoinPoints().get(0)).getPosition().y);
            t1PointF = new PointF(poseData.getPoseLandmark(this.rom.getJoinPoints().get(0)).getPosition().x, height);
        } catch (NullPointerException err){
            err.printStackTrace();
        }
    }
    public void calcStartPointF(Pose poseData) {
        /**
         * Calibration 단계 (기준 점과 끝 점 측정)
         */
        try {
            tCriterionPointF = new PointF(poseData.getPoseLandmark(this.rom.getJoinPoints().get(0)).getPosition().x,
                    poseData.getPoseLandmark(this.rom.getJoinPoints().get(0)).getPosition().y);
            t1PointF = new PointF(poseData.getPoseLandmark(this.rom.getJoinPoints().get(0)).getPosition().x,
                    poseData.getPoseLandmark(this.rom.getJoinPoints().get(1)).getPosition().y);
        } catch (NullPointerException err){
            err.printStackTrace();
        }
    }

    public void calcEndPointF(Pose poseData){
        try {
            t2PointF = new PointF(poseData.getPoseLandmark(this.rom.getJoinPoints().get(1)).getPosition().x,
                    poseData.getPoseLandmark(this.rom.getJoinPoints().get(1)).getPosition().y);
        } catch (NullPointerException err){
            err.printStackTrace();
        }
    }

    public double calcAngle(){
        return ROMUtil.getAngle(tCriterionPointF, t1PointF, t2PointF);
//        return ROMUtil.calcAngleFromThreePoints(getThreePointFList());
//        return ROMUtil.calcAngleFromThreePoints(getThreePointFList());
    }

    public double getAngle(PointF p1, PointF p2, PointF p3){
        /**
         * Get angle from three points
         */
        return ROMUtil.getAngle(p1, p2, p3);

    }

    public void setCalculatedAngle(int calculatedAngle) {
        this.calculatedAngle = calculatedAngle;
    }

    public int getCalculatedAngle() {
        return calculatedAngle;
    }
}
