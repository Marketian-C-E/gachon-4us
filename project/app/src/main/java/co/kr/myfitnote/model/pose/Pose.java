package co.kr.myfitnote.model.pose;

import java.util.ArrayList;

import co.kr.myfitnote.core.utils.MediapipeUtil;
import co.kr.myfitnote.core.utils.ROMUtil;

public abstract class Pose {
    static final public String TAG = "Pose";

    private String type;
    private String typeName;
    public float previewWidth = 480;

    public Pose(String type){
        this.type = type;
        this.typeName = ROMUtil.POSENAME.get(type);
    }

    public abstract void process();

    public abstract CheckConditionResult checkStartCondition(com.google.mlkit.vision.pose.Pose poseData);

    public abstract String getLogMessage();

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }
}
