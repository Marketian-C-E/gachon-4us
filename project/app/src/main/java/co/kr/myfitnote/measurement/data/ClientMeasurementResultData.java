package co.kr.myfitnote.measurement.data;

import co.kr.myfitnote.model.TestResult;

public class ClientMeasurementResultData {
    /**
     * 사용자의 정보와 사용자의 측정 정보를 담당하는 데이터 모델
     */
    public String name;
    public String phone;
    public String exercise;
    public TestResult testResult;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }
}
