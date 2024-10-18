package co.kr.myfitnote.model;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class SingLegStanceDisabled {
    private String type = "SINGLE_LEGSTANCE";
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

    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();
    private double leftMin;
    private double rightMin;

    private int failCount = 0;

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

    public void setStatus(int status){
        this.status = status;
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

    public int calculate(LandmarkProto.NormalizedLandmarkList landmarks, boolean isLeft){
        return status = EXERCISE_SUCCESS;
    }


    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark) {
        landmarkList.add(landmark);
        double leftKneeMean = landmarksMean(landmarkList, LEFT_KNEE);
//        double rightHipMean = landmarksMean(landmarkList, RIGHT_HIP);
        leftMin = leftKneeMean - (leftKneeMean * 0.2);

        calcLandmarksMeans();
    }

    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list, int point) {
        double sum =0;

        for(LandmarkProto.NormalizedLandmarkList landmarks : list){
            sum += landmarks.getLandmark(point).getY();

        }
        return sum /list.size();
    }

    public void calcLandmarksMeans(){
        meanLeftHipX = calcLandmarksMean(LEFT_HIP, 0);
        meanLeftHipY = calcLandmarksMean(LEFT_HIP, 1);
        meanRightHipX = calcLandmarksMean(RIGHT_HIP, 0);
        meanRightHipY = calcLandmarksMean(RIGHT_HIP, 1);
        meanLeftShoulderX = calcLandmarksMean(LEFT_SHOULDER, 0);
        meanLeftShoulderY = calcLandmarksMean(LEFT_SHOULDER, 1);
        meanRightShoulderX = calcLandmarksMean(RIGHT_SHOULDER, 0);
        meanRightShoulderY = calcLandmarksMean(RIGHT_SHOULDER, 1);
        meanLeftKneeX = calcLandmarksMean(LEFT_KNEE, 0);
        meanLeftKneeY = calcLandmarksMean(LEFT_KNEE, 1);
        meanRightKneeX = calcLandmarksMean(RIGHT_KNEE, 0);
        meanRightKneeY = calcLandmarksMean(RIGHT_KNEE, 1);
        Log.e("SinglegStance", "calcLandmarksMeans" + " " + meanLeftHipX + " " + meanLeftHipY);
    }

    public double calcLandmarksMean(int point, int type) {
        /*
        point : landmark position index
        type : one of (0, 1, 2) => 0 is x, 1 is y
         */

        double sum =0;

        for(LandmarkProto.NormalizedLandmarkList landmarks : landmarkList){
            if (type == 0) {
                sum += landmarks.getLandmark(point).getX();
            }else if(type == 1){
                sum += landmarks.getLandmark(point).getY();
            }
        }

        return sum / landmarkList.size();
    }




    private boolean between(double a, double b, double c){
        return c >= a && c <= b;
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



}
