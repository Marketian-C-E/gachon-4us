package co.kr.myfitnote.cm.data;

import co.kr.myfitnote.account.data.model.Client;

public class ClientCreationResult {
    /**
     * 기업 관리자가 고객 생성 시 반환하는 값
     */

    boolean success;
    String message;
    Client client;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
