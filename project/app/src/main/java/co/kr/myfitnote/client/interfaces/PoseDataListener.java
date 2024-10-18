package co.kr.myfitnote.client.interfaces;

import com.google.mlkit.vision.pose.Pose;

import co.kr.myfitnote.core.mlkit.GraphicOverlay;

public interface PoseDataListener {
    void onPoseDataReceived(Pose poseData, GraphicOverlay graphicOverlay);
}
