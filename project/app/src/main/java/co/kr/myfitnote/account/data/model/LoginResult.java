package co.kr.myfitnote.account.data.model;

import android.util.Log;

import co.kr.myfitnote.model.User;

public class LoginResult {
    co.kr.myfitnote.account.data.model.User user;
    CompanyManager manager;
    Boolean success;
    Client client;

    public LoginResult(co.kr.myfitnote.account.data.model.User user, Boolean success){
        Log.e("LoginResult", user + " " + success);
        this.user = user;
        this.success = success;
    }

    public LoginResult(co.kr.myfitnote.account.data.model.User user, Boolean success, CompanyManager manager){
        /**
         * 해당 생성자는 기업 관리자가 로그인 할 경우 호출
         */
        Log.e("LoginResult", user + " " + success + " " + manager);
        this.user = user;
        this.success = success;
        this.manager = manager;
    }

    public co.kr.myfitnote.account.data.model.User getUser(){
        return this.user;
    }

    public Boolean getSuccess() {
        return success;
    }

    public CompanyManager getManager() {
        return manager;
    }

    public Client getClient(){
        return this.client;
    }
}
