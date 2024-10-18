package co.kr.myfitnote.model;

import java.util.HashMap;

public class QuestionResult {
    private HashMap<String, String> questionData;

    public void setQuestionData(HashMap<String, String> questionData) {
        this.questionData = questionData;
    }

    public HashMap<String, String> getQuestionData() {
        return questionData;
    }
}
