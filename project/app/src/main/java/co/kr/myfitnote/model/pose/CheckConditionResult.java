package co.kr.myfitnote.model.pose;

public class CheckConditionResult {
    private boolean conditionMet;
    private String message;

    public CheckConditionResult(boolean conditionMet, String message) {
        this.conditionMet = conditionMet;
        this.message = message;
    }

    public boolean isConditionMet() {
        return conditionMet;
    }

    public String getMessage() {
        return message;
    }
}
