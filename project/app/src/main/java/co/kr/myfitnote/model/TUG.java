package co.kr.myfitnote.model;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.core.utils.landmarks;

public class TUG {
    private final String TAG = "TUG";
    private String type = "TUG";
    private List<LandmarkProto.NormalizedLandmarkList> landmarkList = new ArrayList<>();

    private double range_threshold = 0.03;
    // 시작 기준 어깨 좌 and 골반 좌 (x, y)
    private double left_shoulder_criteria_x = 0;
    private double left_shoulder_criteria_y = 0;
    private double right_shoulder_criteria_x = 0;
    private double right_shoulder_criteria_y = 0;
    private double left_hip_criteria_x = 0;
    private double left_hip_criteria_y = 0;
    private double right_hip_criteria_x = 0;
    private double right_hip_criteria_y = 0;
    private double time = 0;

    private String booleanResultToString;
    private boolean isStandUp = false;

    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark){
        /**
         * 초기 준비 단계에서 사용자의 기준 위치를 Calculate 함.
         */
        landmarkList.add(landmark);

        left_shoulder_criteria_x = landmarksMean(landmarkList, landmarks.LEFT_SHOULDER, false);
        left_shoulder_criteria_y = landmarksMean(landmarkList, landmarks.LEFT_SHOULDER, true);
        right_shoulder_criteria_x = landmarksMean(landmarkList, landmarks.RIGHT_SHOULDER, false);
        right_shoulder_criteria_y = landmarksMean(landmarkList, landmarks.RIGHT_SHOULDER, true);
        right_hip_criteria_x = landmarksMean(landmarkList, landmarks.RIGHT_HIP, false);
        right_hip_criteria_y = landmarksMean(landmarkList, landmarks.RIGHT_HIP, true);
        left_hip_criteria_x = landmarksMean(landmarkList, landmarks.LEFT_HIP, false);
        left_hip_criteria_y = landmarksMean(landmarkList, landmarks.LEFT_HIP, true);
    }

    public boolean checkComeBackStartPosition(LandmarkProto.NormalizedLandmarkList landmark){
        /**
         * TUG에서 제자리로 돌아왔는지 확인
         */

        return true;
    }

    public String getBooleanResultToString(){
        return booleanResultToString;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        /**
         * 유클리드 유사도를 통한 두 점 거리 계산
         */
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private boolean isWithinRange(double userX, double userY, double criteriaX, double criteriaY) {
        /**
         * 유저의 현재 좌표가 특정 점 내에 있는지 확인
         */
        double distance = calculateDistance(userX, userY, criteriaX, criteriaY);
        return distance <= range_threshold;
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

    public double getLeft_hip_criteria_x() {
        return left_hip_criteria_x;
    }

    public double getLeft_hip_criteria_y() {
        return left_hip_criteria_y;
    }

    public double getLeft_shoulder_criteria_x() {
        return left_shoulder_criteria_x;
    }

    public double getLeft_shoulder_criteria_y() {
        return left_shoulder_criteria_y;
    }

    public double getRight_hip_criteria_x() {
        return right_hip_criteria_x;
    }

    public double getRight_hip_criteria_y() {
        return right_hip_criteria_y;
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
}
