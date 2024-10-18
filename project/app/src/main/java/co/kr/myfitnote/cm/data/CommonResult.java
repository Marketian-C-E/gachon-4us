package co.kr.myfitnote.cm.data;

import co.kr.myfitnote.account.data.model.Client;

public class CommonResult {
    /**
     * Success, Message가 담긴 일반 Data
     */

    boolean success;
    String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
