package co.kr.myfitnote.model;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.FunctionTest;
import co.kr.myfitnote.model.pose.Pose;
import lombok.Getter;
import lombok.Setter;

public class Exercise {
    public static  final int READY = 1000;
    public static final int START = 1001;
    private FunctionTest functionTest;
    private int index = 0;
    private int status = -1;
    private List<FunctionTest> exercisies = new ArrayList<>();

    public FunctionTest getFunctionTest(int index) {
        return exercisies.get(index);
    }

    public void setFunctionTest(FunctionTest functionTest) {
        this.functionTest = functionTest;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FunctionTest> getExercisies() {
        return exercisies;
    }

    public void setExercisies(List<FunctionTest> exercisies) {
        this.exercisies = exercisies;
    }

    public void calculate(LandmarkProto.NormalizedLandmarkList landmarks){
        exercisies.get(index).calculate(landmarks);
    }

    public void add(FunctionTest functionTest){
        exercisies.add(functionTest);
    }

    public int getCount(){
        return exercisies.get(index).getCount();
    }

    public String getHint(){return exercisies.get(index).getHint();}

    public void setCount(int count){
        exercisies.get(index).setCount(count);
    }

    public boolean isPause(){
        return exercisies.get(index).isPause();
    }
    public void setPause(boolean isPause){
        exercisies.get(index).setPause(isPause);
    }

    public TestResult getTestResult(){
        return exercisies.get(0).getTestResult();
    }

    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark){
        exercisies.get(index).addLandmark(landmark);
    }

    public void addLandmarkData(LandmarkProto.NormalizedLandmarkList landmark){
        exercisies.get(index).addLandmarkData(landmark);
    }


//    public void

    public float getLeftLegAngle(LandmarkProto.NormalizedLandmark p1,
                                 LandmarkProto.NormalizedLandmark p2,
                                 LandmarkProto.NormalizedLandmark p3){
        return exercisies.get(index).getLeftLegAngle(p1,p2,p3);
    }

    public float getRightLegAngle(LandmarkProto.NormalizedLandmark p1,
                                  LandmarkProto.NormalizedLandmark p2,
                                  LandmarkProto.NormalizedLandmark p3){
        return exercisies.get(index).getRightLegAngle(p1,p2,p3);
    }

    public String getExerciseLabel(){
        return exercisies.get(0).getLabel();
    }


}
