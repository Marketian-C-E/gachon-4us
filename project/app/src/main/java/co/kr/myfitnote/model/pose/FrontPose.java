package co.kr.myfitnote.model.pose;

import android.util.Log;

import com.google.mlkit.vision.pose.PoseLandmark;

/**
 * 포즈 정면
 */
public class FrontPose extends Pose {
    private String logMessage = "";

    public FrontPose(String type) {
        super(type);
    }

    @Override
    public void process() {

    }

    @Override
    public CheckConditionResult checkStartCondition(com.google.mlkit.vision.pose.Pose poseData) {
        try {
            float noseX = poseData.getPoseLandmark(PoseLandmark.NOSE).getPosition().x;
            // check user's nose x is in the center of screen
            if (noseX < previewWidth / 2 * 1.15 &&
                    noseX > previewWidth / 2 * 0.85) {
                return new CheckConditionResult(true, "잘 하셨습니다.");
            } else {
                Log.e(TAG, "코를 중앙선에 맞추어 주세요.");
                return new CheckConditionResult(false, "코를 중앙선에 맞추어 주세요.");
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "checkStartCondition: " + e.getMessage());
            return new CheckConditionResult(false, "코를 중앙선에 맞추어 주세요.");
        }

    }

    @Override
    public String getLogMessage() {
        return logMessage;
    }
}