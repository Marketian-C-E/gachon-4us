package co.kr.myfitnote.model.rom;

import java.util.ArrayList;
import java.util.HashMap;

import co.kr.myfitnote.core.utils.MediapipeUtil;
import co.kr.myfitnote.core.utils.ROMUtil;

public class ROM {
    private String type;
    private ArrayList<Integer> joinPoints;
    private String typeName;
    private String guideImageUrl;

    public ROM(String type){
        this.type = type;
        this.joinPoints = MediapipeUtil.getJointPointsByROMType(type);
        this.typeName = ROMUtil.ROMNAME.get(type);
        this.guideImageUrl = ROMUtil.ROM_GUIDE_IMAGE_URL.get(type);
    }

    public ArrayList<Integer> getJoinPoints() {
        return joinPoints;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getGuideImageUrl() {
        return guideImageUrl;
    }
}
