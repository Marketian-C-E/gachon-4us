package co.kr.myfitnote.account.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.IOException;

import co.kr.myfitnote.BR;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.User;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClientRegisterViewModel extends BaseObservable implements DatePicker.OnDateChangedListener, RadioGroup.OnCheckedChangeListener {
    private User user;

    Context context;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    Handler handler;

    private String passwordConfirm;

    public ClientRegisterViewModel(
            Context context
    ){
        this.context = context;
        user = new User("", "", "", "", "", "", "", "");

        handler = new Handler(Looper.getMainLooper());
    }

    public void onRegisterClicked(){
        Log.e("TAG", "회원가입을 시도합니다.");
        if (validate()) {
            new Thread(() -> {
                Log.e("TAG", "회원가입 던져요!");
                Call<RetrofitStatus> user = service.createUser(this.user);
                try {
                    Response<RetrofitStatus> response = user.execute();
                    String responseMessage = response.body().getMessage();
                    boolean isSuccess = response.body().getSuccess();
//                    Toast.makeText(context, responseMessage, Toast.LENGTH_LONG);
                    setToastMessage(responseMessage);
                    if (isSuccess){
                        ((Activity) (this.context)).finish();
                    }
                    Log.e("TAG", String.valueOf(response.code()));
                    Log.e("TAG", String.valueOf(response.body().getMessage()));
                    Log.e("TAG", String.valueOf(response.body().getSuccess()));
//                    Toast.makeText(this.context, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG);
//                    ((Activity) (this.context)).finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    @Bindable
    public String getPhone(){
        return user.getPhone();
    }

    @Bindable
    public String getPassword(){
        return user.getPassword();
    }

    @Bindable
    public String getBirthDate(){
        return user.getBirthDate();
    }

    @Bindable
    public String getWeight(){
        return user.getWeight();
    }

    @Bindable
    public String getHeight(){
        return user.getHeight();
    }

    @Bindable
    public String getName(){
        return user.getName();
    }

    @Bindable
    private String toastMessage = null;

    public String getToastMessage(){
        return  toastMessage;
    }

    public void setToastMessage(String toastMessage)
    {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    public void setPhone(String phone){
        user.setPhone(phone);
        notifyPropertyChanged(BR.phone);
    }

    public void setName(String name){
        user.setName(name);
        notifyPropertyChanged(BR.name);
    }

    public void setWeight(String weight){
        user.setWeight(weight);
        notifyPropertyChanged(BR.weight);
    }

    public void setHeight(String height){
        user.setHeight(height);
        notifyPropertyChanged(BR.height);
    }

    public void setBirthDate(String birthDate){
        user.setBirthDate(birthDate);
        notifyPropertyChanged(BR.birthDate);
    }

    public void setPassword(String password){
        user.setPassword(password);
        notifyPropertyChanged(BR.password);
    }

    public void setPasswordConfirm(String passwordConfirm){
        this.passwordConfirm = passwordConfirm;
        notifyPropertyChanged(BR.passwordConfirm);
    }

    @Bindable
    public String getPasswordConfirm(){
        return passwordConfirm;
    }

    private boolean validate(){
        Log.e("TAG", "회원가입 유효성 검사");

        if (user.getPhone().equals("")) {
            Toast.makeText(this.context, "아이디를 입력해 주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (user.getPassword().equals("")) {
            Toast.makeText(this.context, "비밀번호를 입력해 주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!user.getPassword().equals(passwordConfirm)) {
            Toast.makeText(this.context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            return false;
        }

//        if (user.getName().equals("")) {
//            Toast.makeText(this.context, "이름을 입력해 주세요.", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if (user.getBirthDate().equals("")) {
//            Toast.makeText(this.context, "생년월일을 선택해 주세요.", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if (user.getHeight().equals("")) {
//            Toast.makeText(this.context, "신장을 선택해 주세요.", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if (user.getWeight().equals("")) {
//            Toast.makeText(this.context, "체중을 선택해 주세요.", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if (user.getSex().equals("")) {
//            Toast.makeText(this.context, "성별을 선택해 주세요.", Toast.LENGTH_LONG).show();
//            return false;
//        }
        return true;
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
        Log.e("TAG", "데이터 들어옵니다.");
        user.setBirthDate(String.format("%s-%s-%s", i, i1, i2));
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        Log.e("TAG", "Radio Group value" + " " + i);
        switch (i){
            case co.kr.myfitnote.R.id.man:
                user.setSex("M");
                break;
            case co.kr.myfitnote.R.id.woman:
                user.setSex("W");
                break;
        }
    }
}
