package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import co.kr.myfitnote.R;
import co.kr.myfitnote.databinding.ActivityRegisterBinding;
import co.kr.myfitnote.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity{

    DatePickerDialog dialog;
    DatePicker datePicker;

    RadioGroup sex_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        RegisterViewModel vm = new RegisterViewModel(this);
        datePicker = findViewById(R.id.birth_day);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(vm);
        }
        datePicker.updateDate(1960, 1, 1);
        sex_type = findViewById(R.id.sex_type);
        sex_type.setOnCheckedChangeListener(vm);
        activityRegisterBinding.setRegisterViewModel(vm);
        activityRegisterBinding.executePendingBindings();
    }


    @BindingAdapter({"toastMessage"})
    public static void runMe(View view, String message) {
        if (message != null)
            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}