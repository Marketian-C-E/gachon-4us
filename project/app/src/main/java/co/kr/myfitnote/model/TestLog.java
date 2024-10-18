package co.kr.myfitnote.model;

public class TestLog {
    private String id;
    private String exercise;
    private String grade;
    private String value;
    private String created_at;
    private String user;

    public String getId() {
        return id;
    }

    public String getExercise() {
        return exercise;
    }

    public String getGrade() {
        return grade;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getValue() {
        return value;
    }

    public String getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
