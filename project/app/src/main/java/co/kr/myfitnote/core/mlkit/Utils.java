package co.kr.myfitnote.core.mlkit;

import static java.lang.Math.atan2;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class Utils {
    public static double getAngle(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
        if (firstPoint == null || midPoint == null || lastPoint == null) {
            return 0;
        }

        double result =
                Math.toDegrees(
                        atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                                lastPoint.getPosition().x - midPoint.getPosition().x)
                                - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                                firstPoint.getPosition().x - midPoint.getPosition().x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    // Get left shoulder angle
    public static double getLeftShoulderAngle(Pose pose) {
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        return getAngle(leftHip, leftShoulder, leftElbow);
    }

    // Get right shoulder angle
    public static double getRightShoulderAngle(Pose pose) {
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        return getAngle(rightHip, rightShoulder, rightElbow);
    }

    // Get left elbow angle
    public static double getLeftElbowAngle(Pose pose) {
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        return getAngle(leftShoulder, leftElbow, leftWrist);
    }

    // Get right elbow angle
    public static double getRightElbowAngle(Pose pose) {
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
        return getAngle(rightShoulder, rightElbow, rightWrist);
    }

    // Get left hip angle
    public static double getLeftKneeAngle(Pose pose) {
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
        return getAngle(leftHip, leftKnee, leftAnkle);
    }

    // Get right hip angle
    public static double getRightKneeAngle(Pose pose) {
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
        return getAngle(rightHip, rightKnee, rightAnkle);
    }

    // Get left hip angle
    public static double getLeftHipAngle(Pose pose) {
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        return getAngle(leftShoulder, leftHip, leftKnee);
    }

    // Get right hip angle
    public static double getRightHipAngle(Pose pose) {
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        return getAngle(rightShoulder, rightHip, rightKnee);
    }

    public static boolean isShowFullBody(Pose pose, float frameLikelihoodThreshold) {
        // if pose is null, return false (not show full body)
        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER) == null){
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.LEFT_HIP) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.RIGHT_HIP) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.LEFT_KNEE) == null) {
            return false;
        }else if (pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE) == null) {
            return false;
        }

        // check point -> shoulder, hip, knee
        if (pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getInFrameLikelihood() < frameLikelihoodThreshold ||
                pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getInFrameLikelihood() < frameLikelihoodThreshold) {
            return false;
        }

        return true;
    }
}
