/**
 * 아령 들기
 */
package co.kr.myfitnote.model;

import static co.kr.myfitnote.core.utils.DateConverter.getAgeFromBirthYear;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.Entry;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.core.utils.landmarks;
import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class HandRaise {
    private final String TAG = "HandRaise";
    private String type = "HandRaise";
    private String exerciseLabel = "상지 근기능";
    private String side = ""; // 좌/우
    private List<Pose> landmarkList = new ArrayList<>();
    private double distance = 0f;
    private double range_threshold = 0.03;
    // 시작 기준 어깨 좌 and 골반 좌 (x, y)
    private double left_shoulder_criteria_x = 0;
    private double left_shoulder_criteria_y = 0;
    private double right_shoulder_criteria_x = 0;
    private double right_shoulder_criteria_y = 0;
    private double left_hand_criteria_x = 0;
    private double left_hand_criteria_y = 0;
    private double right_hand_criteria_x = 0;
    private double right_hand_criteria_y = 0;
    private double time = 0;
    private int repetitionCount = 0;

    private String booleanResultToString;
    private boolean isStandUp = false;

    private ArrayList<Entry> chartData = new ArrayList<Entry>();
    private ArrayList<ParcelablePointF> scatterData = new ArrayList<ParcelablePointF>();

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void addLandmark(Pose pose){
        /**
         * 초기 준비 단계에서 사용자의 기준 위치를 Calculate 함.
         */
        landmarkList.add(pose);

        left_shoulder_criteria_x = landmarksMean(landmarkList, PoseLandmark.LEFT_SHOULDER, false);
        left_shoulder_criteria_y = landmarksMean(landmarkList, PoseLandmark.LEFT_SHOULDER, true);
        right_shoulder_criteria_x = landmarksMean(landmarkList, PoseLandmark.RIGHT_SHOULDER, false);
        right_shoulder_criteria_y = landmarksMean(landmarkList, PoseLandmark.RIGHT_SHOULDER, true);
        right_hand_criteria_x = landmarksMean(landmarkList, PoseLandmark.RIGHT_WRIST, false);
        right_hand_criteria_y = landmarksMean(landmarkList, PoseLandmark.RIGHT_WRIST, true);
        left_hand_criteria_x = landmarksMean(landmarkList, PoseLandmark.LEFT_WRIST, false);
        left_hand_criteria_y = landmarksMean(landmarkList, PoseLandmark.LEFT_WRIST, true);
    }

    public boolean checkLine(Pose landmark, int type){
        /**
         * 신체 터치 여부
         * type 1 : 어깨 라인 터치 여부
         * type 2 : 손 라인 터치 여부
         */
        boolean isCheck = false;

        double left_shoulder_x = landmark.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x;
        double left_shoulder_y = landmark.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y;
        double left_hand_x = landmark.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition().x;
        double left_hand_y = landmark.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition().y;
        double right_shoulder_x = landmark.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x;
        double right_shoulder_y = landmark.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y;
        double right_hand_x = landmark.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition().x;
        double right_hand_y = landmark.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition().y;

        // if side is 왼쪽, use left
        if (side.equals("왼손")) {
            if (type == 1) {
                isCheck = rangeCheck(left_hand_y, left_shoulder_criteria_y, 1);
            } else if (type == 2) {
                isCheck = rangeCheck(left_hand_y, left_hand_criteria_y, 2);
            }
        }else{
            if (type == 1) {
                isCheck = rangeCheck(right_hand_y, right_shoulder_criteria_y, 1);
            } else if (type == 2) {
                isCheck = rangeCheck(right_hand_y, right_hand_criteria_y, 2);
            }
        }

        return isCheck;
    }

    public String getBooleanResultToString(){
        return booleanResultToString;
    }

    private double calculateDistance(double y1, double y2) {
        /**
         * 유클리드 유사도를 통한 두 점 거리 계산
         */
        distance = y1 - y2;
        return Math.abs(distance);
    }

    private boolean rangeCheck(double userY, double criteriaY, int type) {
        /**
         * 유저의 현재 좌표가 특정 점 내에 있는지 확인
         * type 1: UP, 위로 올라갔는지 체크
         * type 2  DOWN, 아래로 내려갔는지 체크
         */
        boolean check = false;

        if (type == 1){
            check = userY < criteriaY;
        }else if (type == 2){
            check = userY > criteriaY;
        }

        return check;
//
//        double distance = calculateDistance(userY, criteriaY);
//        Log.e(TAG, "distance" + distance);
//        return distance <= range_threshold;
    }


    public double landmarksMean(List<Pose> list, int keyPoint, boolean isY) {
        double sumY =0;
        double sumX = 0;

        for(Pose landmarks : list){
            sumY += landmarks.getPoseLandmark(keyPoint).getPosition().y;
            sumX += landmarks.getPoseLandmark(keyPoint).getPosition().x;
        }

        if(isY){
            return sumY /list.size();
        }

        return sumX /list.size();
    }

    public double getLeft_hand_criteria_x() {
        return left_hand_criteria_x;
    }

    public double getLeft_hand_criteria_y() {
        return left_hand_criteria_y;
    }

    public double getLeft_shoulder_criteria_x() {
        return left_shoulder_criteria_x;
    }

    public double getLeft_shoulder_criteria_y() {
        return left_shoulder_criteria_y;
    }

    public double getRight_hand_criteria_x() {
        return right_hand_criteria_x;
    }

    public double getRight_hand_criteria_y() {
        return right_hand_criteria_y;
    }

    public double getRight_shoulder_criteria_x() {
        return right_shoulder_criteria_x;
    }

    public double getRight_shoulder_criteria_y() {
        return right_shoulder_criteria_y;
    }

    public boolean isStandUp() {
        return isStandUp;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public int getRepetitionCount() {
        return repetitionCount;
    }

    public void setRepetitionCount(int repetitionCount) {
        this.repetitionCount = repetitionCount;
    }

    public void countUp() {
        this.repetitionCount += 1;
    }

    public String getExerciseLabel() {
        return exerciseLabel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getGrade(Client client){

        String gender = client.getGender();
        String sAge = client.getBirth_date();
        int age = getAgeFromBirthYear(sAge.substring(0, 4));
        age = Math.max(60, age);

        Log.e(TAG, "[ClientInformation]" + gender + " " + age);

        // Make if else condition
        if (gender.equals("M")){
            if (age >= 60 && age <= 64) {
                if (repetitionCount > 22){
                    return 1;
                }else if (repetitionCount > 15 && repetitionCount <= 22){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 65 && age <= 69) {
                if (repetitionCount > 21){
                    return 1;
                }else if (repetitionCount > 14 && repetitionCount <= 21){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 70 && age <= 74) {
                if (repetitionCount > 21){
                    return 1;
                }else if (repetitionCount > 13 && repetitionCount <= 21){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 75 && age <= 79) {
                if (repetitionCount > 19){
                    return 1;
                }else if (repetitionCount > 12 && repetitionCount <= 19){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 80 && age <= 84) {
                if (repetitionCount > 19){
                    return 1;
                }else if (repetitionCount > 12 && repetitionCount <= 19){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 85 && age <= 89) {
                if (repetitionCount > 17){
                    return 1;
                }else if (repetitionCount > 10 && repetitionCount <= 17){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 90 && age <= 94) {
                if (repetitionCount > 14){
                    return 1;
                }else if (repetitionCount > 9 && repetitionCount <= 14){
                    return 2;
                }else{
                    return 3;
                }
            }
        } else {
            if (age >= 60 && age <= 64) {
                if (repetitionCount > 19){
                    return 1;
                }else if (repetitionCount > 12 && repetitionCount <= 19){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 65 && age <= 69) {
                if (repetitionCount > 18){
                    return 1;
                }else if (repetitionCount > 11 && repetitionCount <= 18){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 70 && age <= 74) {
                if (repetitionCount > 17){
                    return 1;
                }else if (repetitionCount > 11 && repetitionCount <= 17){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 75 && age <= 79) {
                if (repetitionCount > 17){
                    return 1;
                }else if (repetitionCount > 10 && repetitionCount <= 17){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 80 && age <= 84) {
                if (repetitionCount > 16){
                    return 1;
                }else if (repetitionCount > 10 && repetitionCount <= 16){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 85 && age <= 89) {
                if (repetitionCount > 15){
                    return 1;
                }else if (repetitionCount > 10 && repetitionCount <= 15){
                    return 2;
                }else{
                    return 3;
                }
            } else if (age >= 90 && age <= 94) {
                if (repetitionCount > 13){
                    return 1;
                }else if (repetitionCount > 7 && repetitionCount <= 13){
                    return 2;
                }else{
                    return 3;
                }
            }
        }

        return 1;

//        if (repetitionCount < 16){
//            return 3;
//        }else if (repetitionCount >= 16 && repetitionCount <22){
//            return 2;
//        }
//        return 1;
    }

    public void addChartData(float time, Pose pose){
        float value;
        if (side.equals("왼손")) {
            value = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition().y;
            value = (float) (value / left_hand_criteria_y);
        }else{
            value = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition().y;
            value = (float) (value / right_hand_criteria_y);
        }
        Log.e(TAG, value + "is a normalized value");
        chartData.add(new Entry(time, value));
    }

    public ArrayList<Entry> getChartData(){
        return chartData;
    }

    public double getHandY(){
        if (side.equals("왼손")) {
            return left_hand_criteria_y;
        }else{
            return right_hand_criteria_y;
        }
    }

    public double getShoulderY(){
        if (side.equals("왼손")) {
            return left_shoulder_criteria_y;
        }else{
            return right_shoulder_criteria_y;
        }
    }

    public void addScatterData(Pose pose){
        float valueX, valueY;
        float normalizedX, normalizedY;

        if (side.equals("왼손")) {
            valueX = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition().x;
            valueY = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX).getPosition().y;
            valueX = valueX - (float) left_hand_criteria_x;
            valueY = valueY - (float) left_hand_criteria_y;
        }else{
            valueX = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition().x;
            valueY = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX).getPosition().y;
            valueX = valueX - (float) right_hand_criteria_x;
            valueY = valueY - (float) right_hand_criteria_y;
        }
        Log.e(TAG, valueX + "," + valueY + " " + "is a normalized value");
        scatterData.add(new ParcelablePointF(valueX, valueY));
    }

    public ArrayList<ParcelablePointF> getScatterData() {
        return scatterData;
    }

}
