package co.kr.myfitnote.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import co.kr.myfitnote.R;
import co.kr.myfitnote.databinding.ActivityMainBinding;
import co.kr.myfitnote.viewmodels.LoginViewModel;

public class MainActivity extends AppCompatActivity{
    SharedPreferences pref;
    SharedPreferences.Editor pref_editor;
    ActivityMainBinding activityMainBinding;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        pref_editor = pref.edit();

        // for test
        loginViewModel = new LoginViewModel(this, pref_editor);
        loginViewModel.setPhone("");
        loginViewModel.setPassword("");

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setLoginViewModel(loginViewModel);
        activityMainBinding.executePendingBindings();
    }


}