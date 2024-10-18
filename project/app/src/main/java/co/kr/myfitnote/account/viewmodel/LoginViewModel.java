package co.kr.myfitnote.account.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.Gson;

import co.kr.myfitnote.BR;
import co.kr.myfitnote.account.data.model.LoginResult;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.core.utils.PreferencesManager;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginViewModel extends BaseObservable {
    private String TAG = "LoginViewModel";
    private User user;
    private String successMessage = "로그인 성공";
    private String errorMessage = "로그인 실패";

    @Bindable
    private String toastMessage = null;

    @Bindable
    private boolean loginSuccess = false;

    @Bindable
    private boolean clickedRegisterBtn = false;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    PreferencesManager preferencesManager;

    public LoginViewModel(){
        user = new User();
    }

    public LoginViewModel(PreferencesManager preferencesManager){
        user = new User();
        this.preferencesManager = preferencesManager;
    }

    public void onLoginClicked() {
        login(user);
    }

    public void onRegisterClicked() {
        setClickedRegisterBtn(true);
    }

    private void login(User user){
        /***
         * 사용자 로그인 로직
         */

        service.login(user).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                LoginResult loginResult = response.body();
                if (loginResult.getSuccess()){
                    Gson gson = new Gson();
                    String userJson = gson.toJson(loginResult.getUser());

                    if (loginResult.getUser().getUserType() == 2){
                        String managerJson = gson.toJson(loginResult.getManager());
                        preferencesManager.saveData("MANAGER_DATA", managerJson);
                        preferencesManager.saveData("LOGO_PATH", loginResult.getManager().getCompany().getLogo());
                    }else if (loginResult.getUser().getUserType() == 1){
                        String clientJson = gson.toJson(loginResult.getClient());
                        preferencesManager.saveData("CLIENT_DATA", clientJson);
                    }

                    updateUserType(loginResult.getUser());
                    preferencesManager.saveData("USER_DATA", userJson);
                    setLoginSuccess(true);
                }else{
                    setLoginSuccess(false);
                    setToastMessage("아이디 혹은 비밀번호를 확인해 주세요.");
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                setLoginSuccess(false);
                setToastMessage("로그인 도중 문제가 발생하였습니다.\n지속적으로 발생할 경우에 관리자에게 문의해 주세요.");
                Sentry.captureMessage(t.getMessage());
            }
        });

    }

    public void setLoginSuccess(boolean loginSucess) {
        loginSuccess = loginSucess;
        notifyPropertyChanged(BR.loginSuccess);
    }

    public boolean getLoginSuccess(){
        return loginSuccess;
    }

    public String getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    @Bindable
    public String getUsername(){
        return user.getUsername();
    }

    @Bindable
    public String getPassword(){
        return user.getPassword();
    }

    public void setUsername(String username){
        user.setUsername(username);
        notifyPropertyChanged(BR.username);
    }

    public void setPassword(String password){
        user.setPassword(password);
        notifyPropertyChanged(BR.password);
    }

    public void setClickedRegisterBtn(boolean clickedRegisterBtn) {
        this.clickedRegisterBtn = clickedRegisterBtn;
        notifyPropertyChanged(BR.clickedRegisterBtn);
    }

    public int getUserType(){
        return user.getUserType();
    }

    private void updateUserType(User user){
        /**
         * 초기 생성된 User의 속성 값을 로그인 성공 후 받아온 User의 값으로 갱신
         */
        this.user.setUserType(user.getUserType());
    }

}