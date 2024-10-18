package co.kr.myfitnote.model;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.views.parcel.ParcelablePointF;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Walk implements FunctionTest {
    private static final String TAG = "Walk";
    private int count = 0;
    private String label = "심폐 기능";

    private final int LEFT = 0;
    private final int RIGHT = 1;
    public static final int LEFT_KNEE = 26;
    public static final int LEFT_HIP = 24;
    public static final int RIGHT_KNEE = 25;
    public static final int RIGHT_HIP = 23;

    private final int LEFT_ANKLE = 28;
    private final int RIGHT_ANKLE = 27;
    public static final int LEFT_SHOULDER = 12;
    public static final int RIGHT_SHOULDER = 11;


    private boolean isPause = false;
    private int status = -1;
    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();
    private double leftHipMean = -1;
    private double leftKneeMean = -1;
    private double rightHipMean = -1;
    private double rightKneeMean = -1;

    private double leftKneeHipMedian = 0;
    private double rightKneeHipMedian = 0;

    private double leftKneeMin = 9999;
    private double leftKneeMax = -1;
    private double rightKneeMin = 9999;
    private double rightKneeMax = -1;

    private double leftKnee = 9999;
    private double rightKnee = 9999;

    private List<GradeTable> gradeTables = new ArrayList<>();
    private int firstKnee = -1;
    private List<Integer> buffer = new ArrayList();
    private List<Integer> labelList = new ArrayList<>();
    private long leftPreMS;
    private long rightPreMS;

    private int hint = -1;

    private float leftLegAngle = 0f;
    private float rightLegAngle = 0f;
//    private final int ANGLE_THRESHOLD = 130;
//    private final int FULL_ANGLE_THRESHOLD = 165;
    private  int ANGLE_THRESHOLD = 135;
    private  int FULL_ANGLE_THRESHOLD = 165;

    private boolean isLeftLifted = false;
    private boolean isRightLifted = false;
    private TestResult testResult = new TestResult();

    // add variables for stroing logs
    private float leftMinAngle = 185, leftMaxAngle = 0,
                  rightMinAngle = 185, rightMaxAngle = 0;
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


    public Walk(int angleThreshold, int fullAngleThreshold){
        gradeTables.add(new GradeTable("남", 65, 69, 122, 109, 97));
        gradeTables.add(new GradeTable("남", 70, 74, 116, 103, 90));
        gradeTables.add(new GradeTable("남", 75, 79, 112, 97, 83));
        gradeTables.add(new GradeTable("남", 80, 84, 105, 89, 73));
        gradeTables.add(new GradeTable("남", 85, 200, 94, 77, 59));
        gradeTables.add(new GradeTable("여", 65, 69, 117, 104, 90));
        gradeTables.add(new GradeTable("여", 70, 74, 111, 97, 82));
        gradeTables.add(new GradeTable("여", 75, 79, 103, 87, 71));
        gradeTables.add(new GradeTable("여", 80, 84, 93, 75, 57));
        gradeTables.add(new GradeTable("여", 85, 200, 76, 58, 41));

        ANGLE_THRESHOLD = angleThreshold;
        FULL_ANGLE_THRESHOLD = fullAngleThreshold;

    }

    private void clearAngleData(){
        /*
         * clear angle data for storing logs.
         * e.g) if walk count up, set all angle datas to default
         */
        leftMinAngle = 185;
        leftMaxAngle = 0;
        rightMinAngle = 185;
        rightMaxAngle = 0;
    }

    private void storeAngleData() {
        // this is for storing leg data by count.
        ArrayList<Float> valueList = new ArrayList<>();
        Log.e(TAG, " " + leftMinAngle + " " +  leftMaxAngle+ " " + rightMinAngle+ " " +  rightMaxAngle+ " " +  count);
        valueList.add(Double.isNaN(leftMinAngle) ? 0 : leftMinAngle);
        valueList.add(Double.isNaN(leftMaxAngle) ? 0 : leftMaxAngle);
        valueList.add(Double.isNaN(rightMinAngle) ? 0 : rightMinAngle);
        valueList.add(Double.isNaN(rightMaxAngle) ? 0 : rightMaxAngle);
        logData.put(String.valueOf(count), valueList);
    }

    @Override
    public void calculate(LandmarkProto.NormalizedLandmarkList landmarks) {

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


    private void setHint(){
        if(buffer.size() > 0 && buffer.get(0) == labelList.get(0)){
            hint = labelList.get(1);
        }else{
            hint = labelList.get(0);
        }
    }

    public String getHint(){
        if(hint == LEFT){
            return "왼쪽";
        }else  if(hint == RIGHT){
            return "오른쪽";
        }
        return null;
    }

    @Override
    public float getLeftLegAngle(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2, LandmarkProto.NormalizedLandmark p3) {
        return calculateAngle(p1, p2, p3);
    }

    @Override
    public float getRightLegAngle(LandmarkProto.NormalizedLandmark p1, LandmarkProto.NormalizedLandmark p2, LandmarkProto.NormalizedLandmark p3) {
        return calculateAngle(p1, p2, p3);
    }

    private void addFirstKnee(int a, int b){
        if(labelList.size() == 0){
            labelList.add(a);
            labelList.add(b);
        }
    }


    private boolean isCheckLeft(double knee){
        if(this.leftKnee < leftKneeHipMedian
                && between(leftKneeMin, leftKneeMax, knee)){
            return true;
        }
        if(!(this.leftKnee < leftKneeHipMedian)){
            this.leftKnee = knee;

        }
        return false;
    }

    private  boolean isCheckRight(double knee){
        if(this.rightKnee < rightKneeHipMedian
                && between(rightKneeMin, rightKneeMax, knee)){
            return true;
        }

        if(!(this.rightKnee < rightKneeHipMedian)){
            this.rightKnee = knee;
        }
        return false;
    }


    private boolean between(double a, double b, double c){
        return c >= a && c <= b;
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


    private int getGrade(){
        //Log.e("poapo","start grade");
        int age = 60;
        String gender ="남";
//        GradeTable findGradeTable = null;
//        for(int i=0; i<gradeTables.size(); i++){
//            GradeTable gradeTable = gradeTables.get(i);
//            if(gradeTable.getGender().equals(gender) &&
//                    age >= gradeTable.getMinAge() && age <= gradeTable.getMaxAge()){
//
//                findGradeTable = gradeTable;
//                break;
//            }
//        }
//        if(findGradeTable == null) return -1;

        Log.e(TAG, "Walk count is " + count);

        if (count < 87){
            return 3;
        }else if (count >= 87 && count < 115){
            return 2;
        }else{
            return 1;
        }

//        if(count >= findGradeTable.getGrade1Count()){
//            return 1;
//        }else if(count >= findGradeTable.getGrade2Count()){
//            return 2;
//        }else if(count >= findGradeTable.getGrade3Count()){
//            return 3;
//        }else{
//            return 4;
//        }

    }

    @Override
    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark) {
        landmarkList.add(landmark);
        landmarksMean(landmarkList);
        double ratioKnee = 0.15;
        leftKneeHipMedian = (leftKneeMean + leftHipMean) / 2;
        rightKneeHipMedian = (rightKneeMean + rightHipMean) / 2;

        leftKneeMin = leftKneeMean - (leftKneeMean * ratioKnee);
        leftKneeMax = leftKneeMean + (leftKneeMean * ratioKnee);

        rightKneeMin = rightKneeMean - (rightKneeMean * ratioKnee);
        rightKneeMax = rightKneeMean +  (rightKneeMean * ratioKnee);

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

        leftKneeMean = leftKneeSum / list.size();
        leftHipMean = leftHipSum / list.size();
        rightKneeMean = rightKneeSum / list.size();
        rightHipMean =  rightHipSum / list.size();

        return 0;
    }

    @Override
    public double poseDataMean(List<Pose> list) {
        return 0;
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


}

