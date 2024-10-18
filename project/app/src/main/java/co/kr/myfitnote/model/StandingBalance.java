package co.kr.myfitnote.model;

import static co.kr.myfitnote.core.utils.landmarks.LEFT_HIP;
import static co.kr.myfitnote.core.utils.landmarks.LEFT_KNEE;
import static co.kr.myfitnote.core.utils.landmarks.LEFT_SHOULDER;
import static co.kr.myfitnote.core.utils.landmarks.RIGHT_HIP;
import static co.kr.myfitnote.core.utils.landmarks.RIGHT_KNEE;
import static co.kr.myfitnote.core.utils.landmarks.RIGHT_SHOULDER;
import static co.kr.myfitnote.core.utils.landmarks.calcLandmarksMean;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class StandingBalance {
    private String type = "TUG";
    private final int LEFT_HEEL = 30;
    private final int RIGHT_HEEL =  29;
    private final int LEFT_FOOT_INDEX = 32;
    private final int RIGHT_FOOT_INDEX = 31;
    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();

    private double thresholdDistance;
    private double min, max;

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

    private double second = 0;


    public StandingBalance(){}

    public StandingBalance(String type){
        this.type = type;
    }

    public boolean calculate(LandmarkProto.NormalizedLandmarkList landmarks){

         double distance = getTwoPointDistance(
                                 landmarks.getLandmark(LEFT_FOOT_INDEX).getX(),
                                 landmarks.getLandmark(LEFT_FOOT_INDEX).getY(),
                                 landmarks.getLandmark(RIGHT_FOOT_INDEX).getX(),
                                landmarks.getLandmark(RIGHT_FOOT_INDEX).getY()
                           );

        Log.d("StandingBalance", max +"<="+ distance + "<="+min);
         return between(max, min, distance);
    }

    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark){
        landmarkList.add(landmark);


        double x1 = landmarksMean(landmarkList, LEFT_FOOT_INDEX, false);
        double y1 = landmarksMean(landmarkList, LEFT_FOOT_INDEX, true);
        double x2 = landmarksMean(landmarkList, RIGHT_FOOT_INDEX, false);
        double y2 = landmarksMean(landmarkList, RIGHT_FOOT_INDEX, true);


        thresholdDistance = getTwoPointDistance(x1,y1, x2,y2);
        min = thresholdDistance + (thresholdDistance * 0.4);
        max = thresholdDistance - (thresholdDistance * 0.4);

        calcLandmarksMeans();
    }

    private double getTwoPointDistance(double x, double y,  double x2, double y2){
        double distance = Math.sqrt((y2 - y) * (y2 - y) + (x2 - x) * (x2 - x));
        return distance;
    }

    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list, int keyPoint, boolean isY) {
        double sumY =0;
        double sumX = 0;


        for(LandmarkProto.NormalizedLandmarkList landmarks : list){
            sumY += landmarks.getLandmark(keyPoint).getY();
            sumX += landmarks.getLandmark(keyPoint).getX();
        }

        if(isY){
            return sumY /list.size();
        }
        return sumX /list.size();
    }
    private boolean between(double a, double b, double c){
        return c >= a && c <= b;
    }

    public void calcLandmarksMeans(){
        meanLeftHipX = calcLandmarksMean(landmarkList, LEFT_HIP, 0);
        meanLeftHipY = calcLandmarksMean(landmarkList, LEFT_HIP, 1);
        meanRightHipX = calcLandmarksMean(landmarkList, RIGHT_HIP, 0);
        meanRightHipY = calcLandmarksMean(landmarkList, RIGHT_HIP, 1);
        meanLeftShoulderX = calcLandmarksMean(landmarkList, LEFT_SHOULDER, 0);
        meanLeftShoulderY = calcLandmarksMean(landmarkList, LEFT_SHOULDER, 1);
        meanRightShoulderX = calcLandmarksMean(landmarkList, RIGHT_SHOULDER, 0);
        meanRightShoulderY = calcLandmarksMean(landmarkList, RIGHT_SHOULDER, 1);
        meanLeftKneeX = calcLandmarksMean(landmarkList, LEFT_KNEE, 0);
        meanLeftKneeY = calcLandmarksMean(landmarkList, LEFT_KNEE, 1);
        meanRightKneeX = calcLandmarksMean(landmarkList, RIGHT_KNEE, 0);
        meanRightKneeY = calcLandmarksMean(landmarkList, RIGHT_KNEE, 1);
    }

    public void addLandmarkData(LandmarkProto.NormalizedLandmarkList landmark){
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

    public ArrayList<ParcelablePointF> getRightKneeData() {
        return rightKneeData;
    }

    public ArrayList<ParcelablePointF> getLeftKneeData() {
        return leftKneeData;
    }

    public ArrayList<ParcelablePointF> getLeftHipData() {
        return leftHipData;
    }

    public ArrayList<ParcelablePointF> getLeftShoulderData() {
        return leftShoulderData;
    }

    public ArrayList<ParcelablePointF> getRightHipData() {
        return rightHipData;
    }

    public ArrayList<ParcelablePointF> getRightShoulderData() {
        return rightShoulderData;
    }

    public void setSecond(double second) {
        this.second = second;
    }
}
