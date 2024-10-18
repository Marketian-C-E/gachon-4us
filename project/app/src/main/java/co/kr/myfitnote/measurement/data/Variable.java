package co.kr.myfitnote.measurement.data;

import com.google.mediapipe.components.CameraHelper;

public class Variable {
    public static final String TAG = "Measurement";
    public static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";
    public static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    public static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    public static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";
    public static final boolean FLIP_FRAMES_VERTICALLY = true;
    public static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
}
