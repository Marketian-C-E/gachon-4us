package co.kr.myfitnote.model;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class SingLegStance implements FunctionTest {
    private String type = "SINGLE_LEGSTANCE";
    private String label = "외발서기";
    private String side = ""; // 좌/우
    public static final int EXERCISE_READY = 1;
    public static final int EXERCISE_SUCCESS = 2;
    public static final int EXERCISE_FAIL = 3;

    private  int status = EXERCISE_READY;
    public static final int LEFT_KNEE = 26;
    public static final int LEFT_HIP = 24;
    public static final int RIGHT_KNEE = 25;
    public static final int LEFT_SHOULDER = 12;
    public static final int RIGHT_SHOULDER = 11;
    public static final int RIGHT_HIP = 23;

    private List<Pose> poseList = new ArrayList<>();
    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();
    private double leftMin;
    private double rightMin;

    private int failCount = 0;
    private double threshold ;
    private TestResult testResult = new TestResult();

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

    private int time = 0;
    private boolean grade = false;

    public void setFailCount(int failCount){
        this.failCount = failCount;
    }

    @Override
    public void calculate(LandmarkProto.NormalizedLandmarkList landmarks) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void setCount(int count) {

    }

    @Override
    public String getHint() {
        return null;
    }

    @Override
    public float getLeftLegAngle(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2, LandmarkProto.NormalizedLandmark p3) {
        return 0;
    }

    @Override
    public float getRightLegAngle(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2, LandmarkProto.NormalizedLandmark p3) {
        return 0;
    }

    @Override
    public boolean isPause() {
        return false;
    }

    @Override
    public void setPause(boolean isPause) {

    }

    public void setSide(String side) {
        this.side = side;
    }

    @Override
    public TestResult getTestResult() {
        testResult.setType("SINGLELEG");
        testResult.setCount(time);
        testResult.setSecond(time);
        testResult.setSide(side);
        testResult.setGrade(grade ? 1 : 0);
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

    public void setStatus(int status){
        this.status = status;
    }

    @Override
    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark) {

    }

    public int getFailCount(){
        return failCount;
    }

    public void setGrade(boolean grade) {
        this.grade = grade;
    }

    public boolean getGrade(){
        return this.grade;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public SingLegStance(double threshold){
        this.threshold = threshold;
    }

    public float getCalibrationValue(){
        return (float) leftMin;
    }

    public boolean isHoldingUp(Pose pose, boolean isLeft){
        /**
         * 사용자가 무릎을 위로 유지하고 있는지 판단하는 알고리즘
         */
        boolean isSuccess = true;
        return isSuccess;
    }

    public int calculate(LandmarkProto.NormalizedLandmarkList landmarks, boolean isLeft){
       return 1;
    }

    public void addLandmark(Pose pose){
        poseList.add(pose);
        double leftHipMean = landmarksMean(poseList, PoseLandmark.LEFT_HIP);
        double rightHipMean = landmarksMean(poseList, PoseLandmark.RIGHT_HIP);
        leftMin = leftHipMean + (leftHipMean * threshold);

        calcLandmarksMeans();
    }

//    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark) {
//        landmarkList.add(landmark);
//        double leftHipMean = landmarksMean(landmarkList, LEFT_HIP);
//        double rightHipMean = landmarksMean(landmarkList, RIGHT_HIP);
//        leftMin = leftHipMean + (leftHipMean * threshold);
//
//        calcLandmarksMeans();
//    }

    @Override
    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list) {
        return 0;
    }

    @Override
    public double poseDataMean(List<Pose> list) {
        return 0;
    }

    public double landmarksMean(List<Pose> list, int point){
        double sum =0;
        for(Pose pose : list){
            sum += pose.getPoseLandmark(point).getPosition().y;
        }
        return sum /list.size();
    }

//    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list, int point) {
//        double sum =0;
//
//        for(LandmarkProto.NormalizedLandmarkList landmarks : list){
//            sum += landmarks.getLandmark(point).getY();
//
//        }
//        return sum /list.size();
//    }

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
        Log.e("SinglegStance", "calcLandmarksMeans" + " " + meanLeftHipX + " " + meanLeftHipY);
    }

    public double calcLandmarksMean(int point, int type) {
        /**
         * calcLandmarksMean for pose data
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

    private boolean between(double a, double b, double c){
        return c >= a && c <= b;
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

        Log.e("TAG", leftShoulderY + " " + leftShoulderY + " " + rightShoulderX + " " + rightShoulderY + " " + leftHipX + " " + leftHipY + " " + rightHipX + " " + rightHipY + " " + leftKneeX + " " + leftKneeY + " " + rightKneeX + " " + rightKneeY);

        leftShoulderData.add(new ParcelablePointF(leftShoulderX, leftShoulderY));
        rightShoulderData.add(new ParcelablePointF(rightShoulderX, rightShoulderY));
        leftHipData.add(new ParcelablePointF(leftHipX, leftHipY));
        rightHipData.add(new ParcelablePointF(rightHipX, rightHipY));
        leftKneeData.add(new ParcelablePointF(leftKneeX, leftKneeY));
        rightKneeData.add(new ParcelablePointF(rightKneeX, rightKneeY));
    }

    public void addLandmarkData(LandmarkProto.NormalizedLandmarkList landmark){
        Log.e("ADD-DATA", "CALL ADD LANDMARK DATA");
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

        leftShoulderData.add(new ParcelablePointF(leftShoulderX, leftShoulderY));
        rightShoulderData.add(new ParcelablePointF(rightShoulderX, rightShoulderY));
        leftHipData.add(new ParcelablePointF(leftHipX, leftHipY));
        rightHipData.add(new ParcelablePointF(rightHipX, rightHipY));
        leftKneeData.add(new ParcelablePointF(leftKneeX, leftKneeY));
        rightKneeData.add(new ParcelablePointF(rightKneeX, rightKneeY));
    }

    public List<LandmarkProto.NormalizedLandmarkList> getLandmarkList() {
        return landmarkList;
    }

    public ArrayList<ParcelablePointF> getLeftShoulderData() {
        return leftShoulderData;
    }

    public ArrayList<ParcelablePointF> getRightShoulderData() {
        return rightShoulderData;
    }

    public ArrayList<ParcelablePointF> getRightHipData() {
        return rightHipData;
    }

    public ArrayList<ParcelablePointF> getLeftHipData() {
        return leftHipData;
    }

    public ArrayList<ParcelablePointF> getLeftKneeData() {
        return leftKneeData;
    }

    public ArrayList<ParcelablePointF> getRightKneeData() {
        return rightKneeData;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
