package co.kr.myfitnote.core.utils;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.List;

/**
 * This class is for landmark
 */
public class landmarks {
    public static final int LEFT_KNEE = 26;
    public static final int LEFT_HIP = 24;
    public static final int RIGHT_KNEE = 25;
    public static final int RIGHT_HIP = 23;
    public static final int LEFT_SHOULDER = 12;
    public static final int RIGHT_SHOULDER = 11;
    public static final int LEFT_HAND_INDEX = 20;
    public static final int RIGHT_HAND_INDEX = 19;


    public static double calcLandmarksMean(List<LandmarkProto.NormalizedLandmarkList> landmarkList,
                                           int point,
                                           int type) {
        /*
        point : landmark position index
        type : one of (0, 1, 2) => 0 is x, 1 is y
         */

        double sum =0;

        for(LandmarkProto.NormalizedLandmarkList landmarks : landmarkList){
            if (type == 0) {
                sum += landmarks.getLandmark(point).getX();
            }else if(type == 1){
                sum += landmarks.getLandmark(point).getY();
            }
        }

        return sum / landmarkList.size();
    }


}
