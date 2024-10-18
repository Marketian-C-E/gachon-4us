package co.kr.myfitnote.measurement.data;

import static co.kr.myfitnote.model.SeatDownUp.LEFT_ANKLE;
import static co.kr.myfitnote.model.Walk.LEFT_HIP;
import static co.kr.myfitnote.model.Walk.LEFT_KNEE;
import static co.kr.myfitnote.model.Walk.LEFT_SHOULDER;
import static co.kr.myfitnote.model.Walk.RIGHT_HIP;
import static co.kr.myfitnote.model.Walk.RIGHT_KNEE;
import static co.kr.myfitnote.model.Walk.RIGHT_SHOULDER;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class WalkYCriteria implements FunctionTest {
    /**
     * Y 값 기준이 있는 걷기 측정 모델
     */
    private static final String TAG = "Walk";
    private int count = 0;

    private String label = "심폐 기능";

    private boolean isLeftLifted = false;
    private boolean isRightLifted = false;
    private TestResult testResult = new TestResult();
    private List<Pose> poseList = new ArrayList<>();

    private int yCriteria = -1;

    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();
    private HashMap<String, ArrayList<Float>> logData = new HashMap<>();

    // Arraylist for saving landmark data
    private ArrayList<ParcelablePointF> leftShoulderData = new ArrayList<>();
    private ArrayList<ParcelablePointF> rightShoulderData = new ArrayList<>();
    private ArrayList<ParcelablePointF> leftHipData = new ArrayList<>();
    private ArrayList<ParcelablePointF> rightHipData = new ArrayList<>();
    private ArrayList<ParcelablePointF> leftKneeData = new ArrayList<>();
    private ArrayList<ParcelablePointF> rightKneeData = new ArrayList<>();

    private double meanLeftShoulderX = 0;
    private double meanLeftShoulderY = 0;
    private double meanRightShoulderX = 0;
    private double meanRightShoulderY = 0;
    private double meanLeftHipX = 0;
    private double meanLeftHipY = 0;
    private double meanRightHipX = 0;
    private double meanRightHipY = 0;
    private double meanLeftKneeX = 0;
    private double meanLeftKneeY = 0;
    private double meanRightKneeX = 0;
    private double meanRightKneeY = 0;

    private double threshold;

    @Override
    public void calculate(LandmarkProto.NormalizedLandmarkList landmarks) {
        LandmarkProto.NormalizedLandmark leftKnee =  landmarks.getLandmark(LEFT_KNEE);
        LandmarkProto.NormalizedLandmark rightKnee =  landmarks.getLandmark(RIGHT_KNEE);

        if (leftKnee.getY() <= meanLeftKneeY - threshold && !isRightLifted){
            isLeftLifted = true;
        }

        if (rightKnee.getY() <= meanLeftKneeY - threshold && isLeftLifted){
            isRightLifted = true;
        }

        if (isLeftLifted && isRightLifted){
            count++;
            isLeftLifted = false;
            isRightLifted = false;
        }
    }

    public boolean check(Pose pose){
        /**
         * check pose data is valid
         */

        float leftKneeY = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y;
        float rightKneeY = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().y;
        float leftHipY = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y;
        float rightHipY = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y;

        // walk don't use calibration value
        float thresholdLineY = (float) (meanLeftKneeY - (meanLeftKneeY - meanLeftHipY) * 0.5);

        if (thresholdLineY >= leftKneeY && !isRightLifted){
            isLeftLifted = true;
        }

        if (thresholdLineY >= rightKneeY && isLeftLifted){
            isRightLifted = true;
        }

        if (isLeftLifted && isRightLifted){
            count++;
            isLeftLifted = false;
            isRightLifted = false;
            return true;
        }

        return false;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {

    }

    @Override
    public String getHint() {
        return "";
    }

    @Override
    public float getLeftLegAngle(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2, LandmarkProto.NormalizedLandmark p3) {
        return calculateAngle(p1, p2, p3);
    }

    @Override
    public float getRightLegAngle(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2, LandmarkProto.NormalizedLandmark p3) {
        return calculateAngle(p1, p2, p3);
    }

    @Override
    public boolean isPause() {
        return false;
    }

    @Override
    public void setPause(boolean isPause) {

    }

    @Override
    public TestResult getTestResult() {
        testResult.setCount(count);
        testResult.setGrade(getGrade());
        testResult.setType("WALK");
        testResult.setLogData(this.logData);
        testResult.setLeftHipData(this.leftHipData);
        testResult.setRightHipData(this.rightHipData);
        testResult.setLeftKneeData(this.leftKneeData);
        testResult.setRightKneeData(this.rightKneeData);
        testResult.setLeftShoulderData(this.leftShoulderData);
        testResult.setRightShoulderData(this.rightShoulderData);
        return testResult;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void setStatus(int status) {

    }

    @Override
    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark) {
        Log.e(TAG, "left knee =>" + landmark.getLandmark(LEFT_KNEE).getY());
        landmarkList.add(landmark);
        calcLandmarksMeans();

    }

    public void addLandmark(Pose pose){
        poseList.add(pose);
//        poseDataMean(poseList);
//        kneeMin = kneeMean - (kneeMean * 0.15);
//        kneeMax = kneeMean + (kneeMean * 0.15);
//        hipMin = hipMean - (hipMean * 0.03);
//        hipMax = hipMean + (hipMean * 0.03);

        calcLandmarksMeans();
    }

    @Override
    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list) {
        double leftKneeSum = 0;
        double leftHipSum = 0;
        double rightKneeSum =0;
        double rightHipSum =0;
        for(LandmarkProto.NormalizedLandmarkList landmark : list){
            leftKneeSum += landmark.getLandmark(LEFT_KNEE).getY();
            leftHipSum += landmark.getLandmark(LEFT_HIP).getY();
            rightKneeSum += landmark.getLandmark(RIGHT_KNEE).getY();
            rightHipSum += landmark.getLandmark(RIGHT_HIP).getY();

        }

//        leftKneeMean = leftKneeSum / list.size();
//        leftHipMean = leftHipSum / list.size();
//        rightKneeMean = rightKneeSum / list.size();
//        rightHipMean =  rightHipSum / list.size();

        return 0;
    }

    @Override
    public void addLandmarkData(LandmarkProto.NormalizedLandmarkList landmark) {
//        Log.e("ADD-DATA", "CALL ADD LANDMARK DATA");
        float leftShoulderX = landmark.getLandmark(LEFT_SHOULDER).getX();
        float leftShoulderY = landmark.getLandmark(LEFT_SHOULDER).getY();
        float rightShoulderX = landmark.getLandmark(RIGHT_SHOULDER).getX();
        float rightShoulderY = landmark.getLandmark(RIGHT_SHOULDER).getY();
        float leftHipX = landmark.getLandmark(LEFT_HIP).getX();
        float leftHipY = landmark.getLandmark(LEFT_HIP).getY();
        float rightHipX = landmark.getLandmark(RIGHT_HIP).getX();
        float rightHipY = landmark.getLandmark(RIGHT_HIP).getY();
        float leftKneeX = landmark.getLandmark(LEFT_KNEE).getX();
        float leftKneeY = landmark.getLandmark(LEFT_KNEE).getY();
        float rightKneeX = landmark.getLandmark(RIGHT_KNEE).getX();
        float rightKneeY = landmark.getLandmark(RIGHT_KNEE).getY();

        // 평균 값에서 측정 값 빼기 (캘리브레이션 기준으로 좌표 산출)
        leftShoulderX /= meanLeftShoulderX;
        leftShoulderY /= meanLeftShoulderY;
        rightShoulderX /= meanRightShoulderX;
        rightShoulderY /= meanRightShoulderY;
        leftHipX /= meanLeftHipX;
        leftHipY /= meanLeftHipY;
        rightHipX /= meanRightHipX;
        rightHipY /= meanRightHipY;
        leftKneeX /= meanLeftKneeX;
        leftKneeY /= meanLeftKneeY;
        rightKneeX /= meanRightKneeX;
        rightKneeY /= meanRightKneeY;

        leftShoulderData.add(new ParcelablePointF(leftShoulderX, leftShoulderY));
        rightShoulderData.add(new ParcelablePointF(rightShoulderX, rightShoulderY));
        leftHipData.add(new ParcelablePointF(leftHipX, leftHipY));
        rightHipData.add(new ParcelablePointF(rightHipX, rightHipY));
        leftKneeData.add(new ParcelablePointF(leftKneeX, leftKneeY));
        rightKneeData.add(new ParcelablePointF(rightKneeX, rightKneeY));
    }

    @Override
    public double poseDataMean(List<Pose> list) {
        double kneeSum = 0;

        for(Pose landmarks : list){
            try{
                kneeSum += landmarks.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y;
            }catch (Exception e){
                Log.e(TAG, "poseDataMean: " + e.getMessage());
            }
        }
        Log.e("sum", "sum: "+ kneeSum);
        return 0.0;
    }

    public void addLandmarkData(Pose pose){
        /**
         * addLandmarkdData for pose data
         */
        float leftShoulderX = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x;
        float leftShoulderY = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y;
        float rightShoulderX = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x;
        float rightShoulderY = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y;
        float leftHipX = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x;
        float leftHipY = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y;
        float rightHipX = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x;
        float rightHipY = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y;
        float leftKneeX = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().x;
        float leftKneeY = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y;
        float rightKneeX = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().x;
        float rightKneeY = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().y;

        // 평균 값에서 측정 값 빼기 (캘리브레이션 기준으로 좌표 산출)
        leftShoulderX -= meanLeftShoulderX;
        leftShoulderY -= meanLeftShoulderY;
        rightShoulderX -= meanRightShoulderX;
        rightShoulderY -= meanRightShoulderY;
        leftHipX -= meanLeftHipX;
        leftHipY -= meanLeftHipY;
        rightHipX -= meanRightHipX;
        rightHipY -= meanRightHipY;
        leftKneeX -= meanLeftKneeX;
        leftKneeY -= meanLeftKneeY;
        rightKneeX -= meanRightKneeX;
        rightKneeY -= meanRightKneeY;

        Log.e(TAG, leftShoulderX + " " + leftShoulderY);

        leftShoulderData.add(new ParcelablePointF(leftShoulderX, leftShoulderY));
        rightShoulderData.add(new ParcelablePointF(rightShoulderX, rightShoulderY));
        leftHipData.add(new ParcelablePointF(leftHipX, leftHipY));
        rightHipData.add(new ParcelablePointF(rightHipX, rightHipY));
        leftKneeData.add(new ParcelablePointF(leftKneeX, leftKneeY));
        rightKneeData.add(new ParcelablePointF(rightKneeX, rightKneeY));
    }

    @Override
    public ArrayList<ParcelablePointF> getLeftShoulderData() {
        return leftShoulderData;
    }

    @Override
    public ArrayList<ParcelablePointF> getRightShoulderData() {
        return rightShoulderData;
    }

    @Override
    public ArrayList<ParcelablePointF> getRightHipData() {
        return rightHipData;
    }

    @Override
    public ArrayList<ParcelablePointF> getLeftHipData() {
        return leftHipData;
    }

    @Override
    public ArrayList<ParcelablePointF> getRightKneeData() {
        return leftKneeData;
    }

    @Override
    public ArrayList<ParcelablePointF> getLeftKneeData() {
        return rightKneeData;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setyCriteria(int yCriteria) {
        this.yCriteria = yCriteria;
    }

    public double calcLandmarksMean(int point, int type) {
        /*
        point : landmark position index
        type : one of (0, 1, 2) => 0 is x, 1 is y
         */

        double sum =0;

        for(Pose landmarks : poseList){
            if (type == 0) {
                sum += landmarks.getPoseLandmark(point).getPosition().x;
            }else if(type == 1){
                sum += landmarks.getPoseLandmark(point).getPosition().y;
            }
        }

        return sum / poseList.size();
    }

    public void calcLandmarksMeans(){
        meanLeftHipX = calcLandmarksMean(PoseLandmark.LEFT_HIP, 0);
        meanLeftHipY = calcLandmarksMean(PoseLandmark.LEFT_HIP, 1);
        meanRightHipX = calcLandmarksMean(PoseLandmark.RIGHT_HIP, 0);
        meanRightHipY = calcLandmarksMean(PoseLandmark.RIGHT_HIP, 1);
        meanLeftShoulderX = calcLandmarksMean(PoseLandmark.LEFT_SHOULDER, 0);
        meanLeftShoulderY = calcLandmarksMean(PoseLandmark.LEFT_SHOULDER, 1);
        meanRightShoulderX = calcLandmarksMean(PoseLandmark.RIGHT_SHOULDER, 0);
        meanRightShoulderY = calcLandmarksMean(PoseLandmark.RIGHT_SHOULDER, 1);
        meanLeftKneeX = calcLandmarksMean(PoseLandmark.LEFT_KNEE, 0);
        meanLeftKneeY = calcLandmarksMean(PoseLandmark.LEFT_KNEE, 1);
        meanRightKneeX = calcLandmarksMean(PoseLandmark.RIGHT_KNEE, 0);
        meanRightKneeY = calcLandmarksMean(PoseLandmark.RIGHT_KNEE, 1);
    }

    public float getCalibrationKneeY(){
        return (float) (meanLeftKneeY - (meanLeftKneeY - meanLeftHipY) * 0.5);
    }

    public void setThreshold(float threshold){
        this.threshold = threshold;
    }

    public double getMeanLeftKneeY() {
        return meanLeftKneeY;
    }

    public double getThreshold() {
        return threshold;
    }

    public float calculateAngle(LandmarkProto.NormalizedLandmark p1,
                                LandmarkProto.NormalizedLandmark p2,
                                LandmarkProto.NormalizedLandmark p3){
        /**
         * calculate angle from 3 points.
         * p2 is center point.
         */
        float p12 = (float) Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
        float p23 = (float) Math.sqrt(Math.pow(p2.getX() - p3.getX(), 2) + Math.pow(p2.getY() - p3.getY(), 2));
        float p31 = (float) Math.sqrt(Math.pow(p3.getX() - p1.getX(), 2) + Math.pow(p3.getY() - p1.getY(), 2));
        float radian = (float) Math.acos((p12*p12 + p23*p23 - p31*p31) / (2 * p12 * p23));
        float degree = (float) (radian / Math.PI * 180);

        degree = !Double.isNaN(degree) ? degree : 0;
        return degree;
    }

    private int getGrade(){
        int age = 60;
        String gender ="남";
        Log.e(TAG, "Walk count is " + count);
        if (count < 87){
            return 3;
        }else if (count >= 87 && count < 115){
            return 2;
        }else{
            return 1;
        }
    }
}
