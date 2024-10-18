package co.kr.myfitnote.measurement.data;

import android.os.Parcelable;

import java.io.Serializable;

public class ClientMeasurementData implements Serializable {
    /**
     * 사용자 검사 리스트 목록 모델
     */
    private int id;
    private int client;
    private String count;
    private String grade;
    private String exercise;
    private String created_at;

    private boolean isChecked = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient() {
        return client;
    }

    public void setClient(int client) {
        this.client = client;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
