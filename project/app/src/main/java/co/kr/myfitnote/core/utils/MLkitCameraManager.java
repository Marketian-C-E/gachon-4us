package co.kr.myfitnote.core.utils;

import static co.kr.myfitnote.core.mlkit.Variable.POSE_DETECTION;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.io.IOException;

import co.kr.myfitnote.core.mlkit.CameraSource;
import co.kr.myfitnote.core.mlkit.CameraSourcePreview;
import co.kr.myfitnote.core.mlkit.GraphicOverlay;
import co.kr.myfitnote.core.mlkit.posedetector.PoseDetectorProcessor;
import co.kr.myfitnote.core.mlkit.preference.PreferenceUtils;

public class MLkitCameraManager {
    private static final String TAG = "MLkitCameraManager";

    private Activity context;
    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    public MLkitCameraManager(Activity context,
                              CameraSource cameraSource,
                              CameraSourcePreview preview,
                              GraphicOverlay graphicOverlay){
        this.context = context;
        this.cameraSource = cameraSource;
        this.preview = preview;
        this.graphicOverlay = graphicOverlay;
    }

    public void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(context, graphicOverlay);
        }

        try {
            switch (model) {
                case POSE_DETECTION:
                    PoseDetectorOptionsBase poseDetectorOptions =
                            PreferenceUtils.getPoseDetectorOptionsForLivePreview(context);
                    Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);
                    boolean shouldShowInFrameLikelihood = false;
                    boolean visualizeZ = false;
                    boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(context);
                    boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(context);
                    cameraSource.setMachineLearningFrameProcessor(
                            new PoseDetectorProcessor(
                                    context,
                                    poseDetectorOptions,
                                    /* showInFrameLikehood */ shouldShowInFrameLikelihood,
                                    /* visualizeZ */ visualizeZ,
                                    /* rescaleZ */ rescaleZ,
                                    runClassification,
                                    /* isStreamMode = */ true));
                    break;
                default:
                    Log.e(TAG, "Unknown model: " + model);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Can not create image processor: " + model, e);
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    public void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }

                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
                preview.start(cameraSource, graphicOverlay);

            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

}
