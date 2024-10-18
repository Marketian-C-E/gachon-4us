package co.kr.myfitnote.prescription.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import co.kr.myfitnote.measurement.data.Exercise;

public class PrescriptionDetail {
    private Prescription prescription;
    private Exercise exercise;
    private String should_do_date;
    private boolean did_exercise;
    private String did_at;
    private int set;
    private int count;
    private int interval;
    private String memo;
    private String summary;
    private int prescription_id;

    public int getPrescription_id() {
        return prescription_id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public String getShould_do_date() {
        return should_do_date;
    }

    public void setShould_do_date(String should_do_date) {
        this.should_do_date = should_do_date;
    }

    public boolean isDid_exercise() {
        return did_exercise;
    }

    public void setDid_exercise(boolean did_exercise) {
        this.did_exercise = did_exercise;
    }

    public String getDid_at() {
        return did_at;
    }

    public void setDid_at(String did_at) {
        this.did_at = did_at;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public String getSummary() {
        return summary;
    }


}
