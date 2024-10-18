package co.kr.myfitnote.exercise;

import java.io.Serializable;

public class ExerciseParam implements Serializable {

    private String exerciseName;
    private int set;
    private int count;
    private int interval;
    private int videoUrl;

    public int getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(int videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
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
}
