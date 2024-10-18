package co.kr.myfitnote.account.ui.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.viewmodel.ClientRegisterViewModel;
import co.kr.myfitnote.databinding.ActivityClientRegisterBinding;
import co.kr.myfitnote.databinding.ActivityRegisterBinding;
import co.kr.myfitnote.viewmodels.RegisterViewModel;

public class ClientRegisterActivity extends AppCompatActivity {

    ActivityClientRegisterBinding activityClientRegisterBinding;

    DatePickerDialog dialog;
    DatePicker datePicker;

    RadioGroup sex_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_register);

        ClientRegisterViewModel viewModel = new ClientRegisterViewModel(this);

        activityClientRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_client_register);
        activityClientRegisterBinding.setRegisterViewModel(viewModel);
        activityClientRegisterBinding.executePendingBindings();
        activityClientRegisterBinding.setLifecycleOwner(this);

//        datePicker = findViewById(R.id.birth_day);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            datePicker.setOnDateChangedListener(viewModel);
//        }
//        datePicker.updateDate(1960, 1, 1);
//
//        sex_type = findViewById(R.id.sex_type);
//        sex_type.setOnCheckedChangeListener(viewModel);


    }
}