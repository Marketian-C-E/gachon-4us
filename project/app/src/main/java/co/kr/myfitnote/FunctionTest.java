package co.kr.myfitnote;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mlkit.vision.pose.Pose;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.views.parcel.ParcelablePointF;

public interface FunctionTest {
    public void calculate(LandmarkProto.NormalizedLandmarkList landmarks);
    public int getCount();
    public void setCount(int count);
    public String getHint();

    public float getLeftLegAngle(LandmarkProto.NormalizedLandmark p1,
                                 LandmarkProto.NormalizedLandmark p2,
                                 LandmarkProto.NormalizedLandmark p3);
    public float getRightLegAngle(LandmarkProto.NormalizedLandmark p1,
                                  LandmarkProto.NormalizedLandmark p2,
                                  LandmarkProto.NormalizedLandmark p3);

    public boolean isPause();
    public void setPause(boolean isPause);
    public TestResult getTestResult();
    public int getStatus();
    public void setStatus(int status);
    public void addLandmark(LandmarkProto.NormalizedLandmarkList landmark);
    public double landmarksMean(List<LandmarkProto.NormalizedLandmarkList> list);

    public double poseDataMean(List<Pose> list);
    public void addLandmarkData(LandmarkProto.NormalizedLandmarkList landmark);

    public ArrayList<ParcelablePointF> getLeftShoulderData();
    public ArrayList<ParcelablePointF> getRightShoulderData();
    public ArrayList<ParcelablePointF> getRightHipData();
    public ArrayList<ParcelablePointF> getLeftHipData();
    public ArrayList<ParcelablePointF> getRightKneeData();
    public ArrayList<ParcelablePointF> getLeftKneeData();

    public String getLabel();

}
