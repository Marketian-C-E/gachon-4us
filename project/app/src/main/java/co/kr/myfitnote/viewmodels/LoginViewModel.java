package co.kr.myfitnote.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import co.kr.myfitnote.BR;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.model.LoginUser;
import co.kr.myfitnote.views.HandRaiseActivity;
import co.kr.myfitnote.views.RegisterActivity;
import retrofit2.Retrofit;

public class LoginViewModel extends BaseObservable {
    private LoginUser user;
    private String successMessage = "로그인 성공";
    private String errorMessage = "로그인 실패";

    @Bindable
    private String toastMessage = null;

    Context context;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    SharedPreferences.Editor pref_editor;

    public LoginViewModel(Context context, SharedPreferences.Editor pref_editor){
        this.context = context;
        this.pref_editor = pref_editor;
        user = new LoginUser("", "");
    }

    public void onLoginClicked() {
        Log.e("TAG", "로그인 버튼이 클릭되었습니다.");
        Intent it = new Intent(context, HandRaiseActivity.class);
        context.startActivity(it);
    }

    public void onRegisterClicked() {
        Log.e("TAG", "회원가입 버튼이 클릭되었습니다.");
        Intent it = new Intent(this.context, RegisterActivity.class);
        this.context.startActivity(it);
    }

    public String getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    @Bindable
    public String getPhone(){
        return user.getPhone();
    }

    @Bindable
    public String getPassword(){
        return user.getPassword();
    }

    public void setPhone(String phone){
        user.setPhone(phone);
        notifyPropertyChanged(BR.phone);
    }

    public void setPassword(String password){
        user.setPassword(password);
        notifyPropertyChanged(BR.password);
    }


}