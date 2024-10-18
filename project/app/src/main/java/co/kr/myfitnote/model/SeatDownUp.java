package co.kr.myfitnote.model;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.views.parcel.ParcelablePointF;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatDownUp implements FunctionTest {
    static final String TAG = "SeatDownUp";
    //    private final int DOWN = 0;
//    private final
    private String label = "하지 근기능";

    private int count = 0;
    private int leftDistance = 9999;
    public static final int LEFT_ANKLE = 28;
    public static final int LEFT_KNEE = 26;
    public static final int LEFT_HIP = 24;
    public static final int LEFT_SHOULDER = 12;
    public static final int RIGHT_KNEE = 25;
    public static final int RIGHT_HIP = 23;
    public static final int RIGHT_SHOULDER = 11;


    private boolean isPause = false;
    private int status = Exercise.READY;
    private double hipMean = -1;
    private double kneeMean = -1;
    private double leftHipY;
    private double kneeMin = 9999;
    private double kneeMax = -1;
    private double hipMin = 9999;
    private double hipMax = -1;

    private List<Pose> poseList = new ArrayList<>();
    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();

    private String hint = "앉기";
    private TestResult testResult = new TestResult();;

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

    private double second = 0;

    public SeatDownUp(){
    }

    private void storeAngleData(int count, int type, double leftHipY) {
        // this is for storing leg data by count.
        // valeusList has indexs of [count, up(1)|down(0), y position]
        ArrayList<Float> valueList = new ArrayList<>();
        valueList.add((float) count);
        valueList.add((float) type);
        valueList.add((float) leftHipY);
        logData.put(String.valueOf(System.currentTimeMillis()), valueList);
    }


    @Override
    public void calculate(LandmarkProto.NormalizedLandmarkList landmarks){

    }

    public boolean check(Pose pose){
        /**
         * 앉았을 경우 true를 반환하고, 서기가 된 경우 false를 반환한다.
         */

        return true;
    }

    @Override
    public float getLeftLegAngle(LandmarkProto.NormalizedLandmark p1,
                                 LandmarkProto.NormalizedLandmark p2,
                                 LandmarkProto.NormalizedLandmark p3) {
        return 0;
    }

    @Override
    public float getRightLegAngle(LandmarkProto.NormalizedLandmark p1,
                                  LandmarkProto.NormalizedLandmark p2,
                                  LandmarkProto.NormalizedLandmark p3) {
        return 0;
    }


    private boolean between(double a, double b, double c){
        return c >= a && c <= b;
    }


    @Override
    public TestResult getTestResult() {
        testResult.setCount(count);
        testResult.setGrade(getGrade());
        testResult.setType("SITUP");
        testResult.setLogData(this.logData);
        testResult.setLeftHipData(this.leftHipData);
        testResult.setRightHipData(this.rightHipData);
        testResult.setLeftKneeData(this.leftKneeData);
        testResult.setRightKneeData(this.rightKneeData);
        testResult.setLeftShoulderData(this.leftShoulderData);
        testResult.setRightShoulderData(this.rightShoulderData);
        return testResult;
    }

    private int getGrade(){
        Log.e("poapo","start grade");
        int age = 65;
        String gender ="남";

        if (count < 14){
            return 3;
        }else if (count >= 14 && count <= 19){
            return 2;
        }else{
            return 1;
        }
    }

    public double landmarksMean(List<Pose> list, int point){
        double sum =0;
        for(Pose pose : list){
            sum += pose.getPoseLandmark(point).getPosition().y;
        }
        return sum /list.size();
    }

    @Override
    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark) {
        landmarkList.add(landmark);

        landmarksMean(landmarkList);
        kneeMin = kneeMean - (kneeMean * 0.15);
        kneeMax = kneeMean + (kneeMean * 0.15);
        hipMin = hipMean - (hipMean * 0.03);
        hipMax = hipMean + (hipMean * 0.03);

        calcLandmarksMeans();
    }

    public double poseDataMean(List<Pose> list) {
        double hipSum =0;
        double kneeSum = 0;

        for(Pose landmarks : list){
            try{
                hipSum += landmarks.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y;
                kneeSum += landmarks.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y;
            }catch (Exception e){
                Log.e(TAG, "poseDataMean: " + e.getMessage());
            }
        }
        Log.e("sum", "sum: "+ hipSum);
        hipMean = hipSum / list.size();
        kneeMean = kneeSum / list.size();

        return 0.0;
    }


    @Override
    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list) {
        double hipSum =0;
        double kneeSum = 0;
        for(LandmarkProto.NormalizedLandmarkList landmarks : list){
            hipSum += landmarks.getLandmark(LEFT_HIP).getY();
            kneeSum += landmarks.getLandmark(LEFT_KNEE).getY();
        }
        Log.e("sum", "sum: "+ hipSum);
        hipMean = hipSum / list.size();
        kneeMean = kneeSum / list.size();

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

        leftShoulderData.add(new ParcelablePointF(leftShoulderX, leftShoulderY));
        rightShoulderData.add(new ParcelablePointF(rightShoulderX, rightShoulderY));
        leftHipData.add(new ParcelablePointF(leftHipX, leftHipY));
        rightHipData.add(new ParcelablePointF(rightHipX, rightHipY));
        leftKneeData.add(new ParcelablePointF(leftKneeX, leftKneeY));
        rightKneeData.add(new ParcelablePointF(rightKneeX, rightKneeY));
    }

    @Override
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
    public ArrayList<ParcelablePointF> getLeftKneeData() {
        return leftKneeData;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public ArrayList<ParcelablePointF> getRightKneeData() {
        return rightKneeData;
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



    private int  calcDistance(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2) {
        // Math.pow() <- 제곱
        // Math.sqrt() <- 루트

        return (int)(Math.abs(p1.getY() - p2.getY())*100);
    }

    public void setSecond(double second) {
        this.second = second;
    }

    public void addLandmark(Pose pose){
        poseList.add(pose);

        poseDataMean(poseList);
        kneeMin = kneeMean - (kneeMean * 0.15);
        kneeMax = kneeMean + (kneeMean * 0.15);
        hipMin = hipMean - (hipMean * 0.03);
        hipMax = hipMean + (hipMean * 0.03);

        calcLandmarksMeans();
    }
}
