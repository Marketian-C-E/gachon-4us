package co.kr.myfitnote.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.kr.myfitnote.views.parcel.ParcelablePointF;
import lombok.Getter;
import lombok.Setter;


public class TestResult {
    private int grade; //등급
    private int count; //횟수
    private double second;
    private String side; // 좌/우
//    private List<List<Graph>> graphData = new ArrayList<>();

    private List<Entry> leftHipGraphData = new ArrayList<>();

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
    }

    public List<Entry> getLeftHipGraphData() {
        return leftHipGraphData;
    }

    public void setLeftHipGraphData(List<Entry> leftHipGraphData) {
        this.leftHipGraphData = leftHipGraphData;
    }

    public List<Entry> getLeftAngleGraphData() {
        return leftAngleGraphData;
    }

    public void setLeftAngleGraphData(List<Entry> leftAngleGraphData) {
        this.leftAngleGraphData = leftAngleGraphData;
    }

    public List<Entry> getRightAngleGraphData() {
        return rightAngleGraphData;
    }

    public void setRightAngleGraphData(List<Entry> rightAngleGraphData) {
        this.rightAngleGraphData = rightAngleGraphData;
    }

    private List<Entry> leftAngleGraphData = new ArrayList<>();
    private List<Entry> rightAngleGraphData = new ArrayList<>();

    private HashMap<String, ArrayList<Float>> logData = new HashMap<>();

    private String type; // exercise type

    private ArrayList<ParcelablePointF> leftShoulderData = new ArrayList<>();
    private ArrayList<ParcelablePointF> rightShoulderData = new ArrayList<>();
    private ArrayList<ParcelablePointF> leftHipData = new ArrayList<>();
    private ArrayList<ParcelablePointF> rightHipData = new ArrayList<>();
    private ArrayList<ParcelablePointF> leftKneeData = new ArrayList<>();
    private ArrayList<ParcelablePointF> rightKneeData = new ArrayList<>();

    // Wrist data
    private ArrayList<ParcelablePointF> wristData = new ArrayList<>();


    public void addLeftHipGraphData(Entry entry){
        leftHipGraphData.add(entry);
    }

    public void addLeftAngleGraphData(Entry entry){
      leftAngleGraphData.add(entry);

    }

    public void addRightAngleGraphData(Entry entry){
        rightAngleGraphData.add(entry);
    }

    public void setLogData(HashMap<String, ArrayList<Float>> logData) {
        this.logData = logData;
    }

    public HashMap<String, ArrayList<Float>> getLogData() {
        return logData;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

    public void setLeftShoulderData(ArrayList<ParcelablePointF> leftShoulderData) {
        this.leftShoulderData = leftShoulderData;
    }

    public void setRightShoulderData(ArrayList<ParcelablePointF> rightShoulderData) {
        this.rightShoulderData = rightShoulderData;
    }

    public void setLeftKneeData(ArrayList<ParcelablePointF> leftKneeData) {
        this.leftKneeData = leftKneeData;
    }

    public void setRightKneeData(ArrayList<ParcelablePointF> rightKneeData) {
        this.rightKneeData = rightKneeData;
    }

    public void setLeftHipData(ArrayList<ParcelablePointF> leftHipData) {
        this.leftHipData = leftHipData;
    }

    public void setRightHipData(ArrayList<ParcelablePointF> rightHipData) {
        this.rightHipData = rightHipData;
    }

    public void setWristData(ArrayList<ParcelablePointF> wristData) {
        this.wristData = wristData;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public ArrayList<ParcelablePointF> getWristData() {
        return wristData;
    }

    public String getGradeResultLabel(){
        /**
         * 등급별 결과 라벨 반환"
         */
        String result = "";
        switch (this.grade){
            case 1:
                result = "1등급 (매우 좋음)";
                break;
            case 2:
                result = "2등급 (좋음)";
                break;
            case 3:
                result = "3등급 (보통)";
                break;
            default:
                result = "4등급 (저체력)";
                break;
        }
        return result;
    }
}
