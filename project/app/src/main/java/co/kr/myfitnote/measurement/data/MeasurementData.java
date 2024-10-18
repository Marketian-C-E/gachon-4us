package co.kr.myfitnote.measurement.data;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class MeasurementData {
    @SerializedName("upper")
    private MeasurementTypeData upper;

    @SerializedName("lower")
    private MeasurementTypeData lower;

    @SerializedName("singleleg_left")
    private MeasurementTypeData singleleg_left;

    @SerializedName("singleleg_right")
    private MeasurementTypeData singleleg_right;

    @SerializedName("cardio")
    private MeasurementTypeData cardio;

    private HashMap<String, MeasurementTypeData> rom;

    public MeasurementTypeData getUpper() {
        return upper;
    }

    public void setUpper(MeasurementTypeData upper) {
        this.upper = upper;
    }

    public MeasurementTypeData getLower() {
        return lower;
    }

    public void setLower(MeasurementTypeData lower) {
        this.lower = lower;
    }

    public MeasurementTypeData getCardio() {
        return cardio;
    }

    public void setCardio(MeasurementTypeData cardio) {
        this.cardio = cardio;
    }

    public HashMap<String, MeasurementTypeData> getRom() {
        return rom;
    }

    public void setRom(HashMap<String, MeasurementTypeData> rom) {
        this.rom = rom;
    }

    public MeasurementTypeData getSingleleg_left() {
        return singleleg_left;
    }

    public void setSingleleg_left(MeasurementTypeData singleleg_left) {
        this.singleleg_left = singleleg_left;
    }

    public MeasurementTypeData getSingleleg_right() {
        return singleleg_right;
    }

    public void setSingleleg_right(MeasurementTypeData singleleg_right) {
        this.singleleg_right = singleleg_right;
    }
}
