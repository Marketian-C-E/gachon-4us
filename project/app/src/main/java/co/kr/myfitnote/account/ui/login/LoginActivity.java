package co.kr.myfitnote.account.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;

import co.kr.myfitnote.BR;
import co.kr.myfitnote.R;
import co.kr.myfitnote.account.ui.register.ClientRegisterActivity;
import co.kr.myfitnote.account.viewmodel.LoginViewModel;
import co.kr.myfitnote.client.ui.home.ClientHomeActivity;
import co.kr.myfitnote.cm.ui.home.CmHomeActivity;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.databinding.ActivityLoginBinding;
import co.kr.myfitnote.views.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";

    ActivityLoginBinding activityMainBinding;
    LoginViewModel loginViewModel;

    PreferencesManager preferencesManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesManager = PreferencesManager.getInstance(this);

        loginViewModel = new LoginViewModel(preferencesManager);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityMainBinding.setLoginViewModel(loginViewModel);
        activityMainBinding.executePendingBindings();
        activityMainBinding.setLifecycleOwner(this);

//        loginViewModel.setPassword("nf1yfa23");
//        loginViewModel.setUsername("manager");

        loginViewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.e(TAG, propertyId + " is sent!");

                if (propertyId == BR.loginSuccess){
                    if (loginViewModel.getLoginSuccess()){
                        // 사용자의 유형에 따라 다른 액티비티 이동
                        Intent it = null;
                        Log.e(TAG, String.valueOf(loginViewModel.getUserType()));
                        switch (loginViewModel.getUserType()){
                            case 1:
                                // if logged user is Client
                                it = new Intent(LoginActivity.this, ClientHomeActivity.class);
                                break;
                            case 2:
                                // if logged user is Manager
                                it = new Intent(LoginActivity.this, CmHomeActivity.class);
                                break;
                        }

                        if (it != null) {
                            startActivity(it);
                        }
                    }
                }else if(propertyId == BR.toastMessage){
//                    Toast.makeText(LoginActivity.this, loginViewModel.getToastMessage(), Toast.LENGTH_LONG).show();
                    // Show NormalDialog
                    NormalDialogFragment normalDialogFragment = new NormalDialogFragment(loginViewModel.getToastMessage(), null, null);
                    normalDialogFragment.setUseNegativeButton(false);
                    normalDialogFragment.show(getSupportFragmentManager(), "normalDialog");
                }else if(propertyId == BR.clickedRegisterBtn){
                    // when user clicked, register button.
                    Intent it = new Intent(LoginActivity.this, ClientRegisterActivity.class);
                    startActivity(it);
                }
            }
        });


    }
}