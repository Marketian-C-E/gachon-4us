package co.kr.myfitnote.core.utils;

import static java.lang.Math.atan2;

import android.graphics.PointF;
import android.util.Log;

import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.HashMap;

public class ROMUtil {

    public static HashMap<String, String> ROM_GUIDE_IMAGE_URL = new HashMap<String, String>() {{
        put("Up|Right", "http://marketian.co.kr:7575/media/asset/ic_abd.png");
        put("Up|Left", "http://marketian.co.kr:7575/media/asset/ic_abd_right.png");
        put("Down|Right", "http://marketian.co.kr:7575/media/asset/ic_rom_leg_left.png");
        put("Down|Left", "http://marketian.co.kr:7575/media/asset/ic_rom_leg_right.png");
    }};


    public static HashMap<String, String> ROMNAME = new HashMap<String, String>() {{
        put("Up|Left", "어깨 외전 (좌)");
        put("Up|Right", "어깨 외전 (우)");
        put("Down|Left", "고관절 외전 (좌)");
        put("Down|Right", "고관절 외전 (우)");
    }};

    public static HashMap<String, String> POSENAME = new HashMap<String, String>() {{
        put("Front", "정면");
        put("Side", "측면");
    }};

    public static double calcAngleFromThreePoints(ArrayList<PointF> pointList) {
        // Calculate vectors from point1 to point2 and from point1 to point3

        Log.e("Angle", pointList.get(0).x + " " + pointList.get(0).y);
        Log.e("Angle", pointList.get(1).x + " " + pointList.get(1).y);
        Log.e("Angle", pointList.get(2).x + " " + pointList.get(2).y);
        float vector1X = pointList.get(0).x - pointList.get(1).x;
        float vector1Y = pointList.get(0).y - pointList.get(1).y;
        float vector2X = pointList.get(2).x - pointList.get(0).x;
        float vector2Y = pointList.get(2).y - pointList.get(0).y;

        // Calculate the dot product of the two vectors
        double dotProduct = vector1X * vector2X + vector1Y * vector2Y;

        // Calculate the magnitudes of the vectors
        double magnitude1 = Math.sqrt(vector1X * vector1X + vector1Y * vector1Y);
        double magnitude2 = Math.sqrt(vector2X * vector2X + vector2Y * vector2Y);

        // Calculate the cosine of the angle between the vectors
        double cosAngle = dotProduct / (magnitude1 * magnitude2);

        // Calculate the angle in radians
        double angleRad = Math.acos(cosAngle);

        // Convert the angle from radians to degrees
        double angleDegrees = Math.toDegrees(angleRad);

        // TODO: Why it needed to 180 - angle?
        return angleDegrees;
//        return 180 - angleDegrees;
    }

    public static double getAngle(PointF firstPoint, PointF midPoint, PointF lastPoint) {
        double result =
                Math.toDegrees(
                        atan2(lastPoint.y - midPoint.y,
                                lastPoint.x - midPoint.x)
                                - atan2(firstPoint.y - midPoint.y,
                                firstPoint.x - midPoint.x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }


}
