package co.kr.myfitnote.measurement.data;

import com.google.gson.annotations.SerializedName;

public class MeasurementTypeData {
    @SerializedName("value")
    private String value;

    @SerializedName("grade")
    private String grade;

    private String result_image;

    public String getValue() {
        return value;
    }

    public String getGrade() {
        return grade;
    }

    public String getResult_image() {
        return result_image;
    }
}
