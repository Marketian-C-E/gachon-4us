package co.kr.myfitnote.model.pose;

import android.util.Log;

import com.google.mlkit.vision.pose.PoseLandmark;

public class SidePose extends Pose{
    private String logMessage = "";

    public SidePose(String type) {
        super(type);
    }

    @Override
    public void process() {

    }

    @Override
    public CheckConditionResult checkStartCondition(com.google.mlkit.vision.pose.Pose poseData) {
        float leftShoulderX = poseData.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x;

        // check user's nose x is in the center of screen
        Log.i(TAG, "shoulder x => " + leftShoulderX + " " + previewWidth / 2 * 1.15 + " " + previewWidth / 2 * 0.85);
        logMessage = "shoulder x => " + leftShoulderX + " " + previewWidth / 2 * 1.15 + " " + previewWidth / 2 * 0.85;

        if (leftShoulderX < previewWidth / 2 * 1.10 &&
                leftShoulderX > previewWidth / 2 * 0.90) {
            return new CheckConditionResult(true, "잘 하셨습니다.");
        } else {
            Log.e(TAG, "좌측 어깨를 중앙선에 맞추어 주세요.");
            return new CheckConditionResult(false, "좌측 어깨를 중앙선에\n맞추어 주세요.");
        }
    }

    @Override
    public String getLogMessage() {
        return logMessage;
    }
}
