package co.kr.myfitnote.views.rom;

import java.io.Serializable;

public class RomResultData implements Serializable {
    public String type;
    public long value;

    public RomResultData(String type, long value){
        this.type = type;
        this.value = value;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
