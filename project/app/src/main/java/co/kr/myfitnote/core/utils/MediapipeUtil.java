package co.kr.myfitnote.core.utils;

import static com.google.mlkit.vision.pose.PoseLandmark.LEFT_ELBOW;
import static com.google.mlkit.vision.pose.PoseLandmark.LEFT_HIP;
import static com.google.mlkit.vision.pose.PoseLandmark.LEFT_KNEE;
import static com.google.mlkit.vision.pose.PoseLandmark.LEFT_SHOULDER;
import static com.google.mlkit.vision.pose.PoseLandmark.RIGHT_ELBOW;
import static com.google.mlkit.vision.pose.PoseLandmark.RIGHT_HIP;
import static com.google.mlkit.vision.pose.PoseLandmark.RIGHT_KNEE;
import static com.google.mlkit.vision.pose.PoseLandmark.RIGHT_SHOULDER;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is for landmark
 */
public class MediapipeUtil {

    public static ArrayList<Integer> getJointPointsByROMType(String type){
        ArrayList<Integer> jointPoints = null;
        switch (type){
            case "Up|Left":
                // 상지 ROM 좌
                jointPoints = new ArrayList<>(Arrays.asList(LEFT_SHOULDER, LEFT_ELBOW));
                break;
            case "Up|Right":
                // 상지 ROM 우
                jointPoints = new ArrayList<>(Arrays.asList(RIGHT_SHOULDER, RIGHT_ELBOW));
                break;
            case "Down|Left":
                jointPoints = new ArrayList<>(Arrays.asList(LEFT_HIP, LEFT_KNEE));
                break;
            case "Down|Right":
                jointPoints = new ArrayList<>(Arrays.asList(RIGHT_HIP, RIGHT_KNEE));
                break;
        }
        return jointPoints;
    }

    public static ArrayList<Integer> getJointPointsByPoseType(String type){
        ArrayList<Integer> jointPoints = null;
        switch (type){
            case "Front":
                jointPoints = new ArrayList<>(Arrays.asList(LEFT_SHOULDER, RIGHT_SHOULDER));
                break;
//            case "Side":
//                jointPoints = new ArrayList<>(Arrays.asList(RIGHT_SHOULDER, RIGHT_ELBOW));
//                break;
        }
        return jointPoints;
    }

}
