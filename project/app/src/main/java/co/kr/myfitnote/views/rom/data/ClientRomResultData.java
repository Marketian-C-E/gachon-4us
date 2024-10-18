package co.kr.myfitnote.views.rom.data;

import com.google.common.primitives.Bytes;

import co.kr.myfitnote.model.TestResult;

public class ClientRomResultData {
    public String name;
    public String phone;
    public String type; // 운동 항목
    public byte[] image;
    public long angle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setAngle(long angle) {
        this.angle = angle;
    }

    public long getAngle() {
        return angle;
    }
}
