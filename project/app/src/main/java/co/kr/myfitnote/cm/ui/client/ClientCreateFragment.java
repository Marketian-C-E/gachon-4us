package co.kr.myfitnote.cm.ui.client;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.CompanyManager;
import co.kr.myfitnote.cm.data.ClientCreationResult;
import co.kr.myfitnote.cm.viewmodel.ClientCreateViewModel;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.AgreeCheckboxView;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.core.utils.DateConverter;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.core.utils.ToolbarManager;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClientCreateFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private String TAG = "ClientCreateFragment";

    private View view;
    private EditText name, phone, birth_date, weight, height, address;
    private String selectedSexValue = "";
    private RadioGroup sexTypeRadioGroup, diseaseTypeRadioGroup;
    private Button enrollBtn;
    private ClientCreateViewModel viewModel;
    private HashMap<String, String> sexTypeMapping = new HashMap<String, String>() {{
        put("남성", "M");
        put("여성", "W");
    }};
    private String selectedDiseaseValue = "";
    private HashMap<String, String> diseaseTypeMapping = new HashMap<String, String>() {{
        put("고혈압", "고혈압");
        put("당뇨병", "당뇨병");
        put("암", "암");
        put("만성폐질환", "만성폐질환");
        put("심근경색", "심근경색");
        put("심부전", "심부전");
        put("협심증", "협심증");
        put("천식", "천식");
        put("관절염", "관절염");
        put("뇌경색", "뇌경색");
        put("신장질환", "신장질환");
        put("없음", "없음");
    }};

    private AgreeCheckboxView agree_policy, agree_privacy;
    private boolean isAgreePolicy = false, isAgreePrivacy = false;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    NormalDialogFragment normalDialogFragment;
    NavController navController;


    public ClientCreateFragment() {
        // Required empty public constructor
    }

    public static ClientCreateFragment newInstance() {
        ClientCreateFragment fragment = new ClientCreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_client_create, container, false);

//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.cm_toolbar);
//        ToolbarManager toolbarManager = new ToolbarManager((AppCompatActivity) getActivity(), toolbar);
//        toolbarManager.setToolbarTitle("신규 고객 등록");

        navController= NavHostFragment.findNavController(this);
        viewModel = new ViewModelProvider(this).get(ClientCreateViewModel.class);

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        birth_date = view.findViewById(R.id.birth_date);
        weight = view.findViewById(R.id.weight);
        height = view.findViewById(R.id.height);
        address = view.findViewById(R.id.address);
        sexTypeRadioGroup = view.findViewById(R.id.sex_type);
        sexTypeRadioGroup.setOnCheckedChangeListener(this);
        enrollBtn = view.findViewById(R.id.enroll_btn);
        enrollBtn.setOnClickListener(this);

        initDiseaseTypeRadioGroup();
        initAgreeCheckboxView();


        // Test
//        name.setText("안주영");
//        phone.setText("01080094936");
//        birth_date.setText("19950623");
//        weight.setText("60");
//        height.setText("180");
//        address.setText("수원시 권선구");

        return view;
    }

    private void initAgreeCheckboxView(){
        /**
         * 이용약관, 개인정보 처리 방침 컴포넌트 초기화
         */
        agree_policy = view.findViewById(R.id.agree_policy);
        agree_privacy = view.findViewById(R.id.agree_privacy);

        agree_policy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e(TAG, "Agree Policy: " + b);
                isAgreePolicy = b;
            }
        });
        agree_policy.setOnClickShowDocumentListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_clientCreateFragment_to_policyFragment);
            }
        });

        agree_privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e(TAG, "Agree Privacy: " + b);
                isAgreePrivacy = b;
            }
        });

        agree_privacy.setOnClickShowDocumentListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stack privacy fragment with fade
                navController.navigate(R.id.action_clientCreateFragment_to_privacyFragment);
            }
        });

    }

    private void initDiseaseTypeRadioGroup(){
        /**
         * 질병 관련 문항의 보기 초기화
         *
         * Text style:
         *      android:textSize="26sp"
         *      android:textColor="#B9CAC9"
         *      android:letterSpacing="-0.1"
         */
        diseaseTypeRadioGroup = view.findViewById(R.id.disease_type);
        diseaseTypeRadioGroup.setOnCheckedChangeListener(this);

        for (Map.Entry<String, String> entry : diseaseTypeMapping.entrySet()) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(entry.getKey());
            radioButton.setTextSize(15);
            radioButton.setTextColor(Color.parseColor("#B9CAC9"));
            radioButton.setLetterSpacing(-0.1f);
            // Add radioButtons to radioGroup with layout bottom 5dp
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 10);
            radioButton.setLayoutParams(layoutParams);
            diseaseTypeRadioGroup.addView(radioButton);
        }
    }

    private boolean validate(){
        /**
         * 회원 등록 Validation
         */

        boolean isPass = true;
        String message = "";

        if (name.getText().toString().equals("")){
//            Toast.makeText(getContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            message = "이름을 입력해 주세요.";
            isPass = false;
        }

        if (phone.getText().toString().equals("")){
//            Toast.makeText(getContext(), "연락처를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            message = "연락처를 입력해 주세요.";
            isPass = false;
        }

        if (birth_date.getText().toString().equals("")){
//            Toast.makeText(getContext(), "생년월일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            message = "생년월일을 입력해 주세요.";
            isPass = false;
        }

        if (height.getText().toString().equals("")){
//            Toast.makeText(getContext(), "신장을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            message = "신장을 입력해 주세요.";
            isPass = false;
        }

        if (weight.getText().toString().equals("")){
//            Toast.makeText(getContext(), "체중을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            message = "체중을 입력해 주세요.";
            isPass = false;
        }

        if (address.getText().toString().equals("")){
//            Toast.makeText(getContext(), "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            message = "주소를 입력해 주세요.";
            isPass = false;
        }

        if (selectedSexValue.equals("")){
//            Toast.makeText(getContext(), "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            message = "성별을 선택해 주세요.";
            isPass = false;
        }

        // Check diease type
        if (selectedDiseaseValue.equals("")){
            message = "질환 여부 문항에 응답해 주세요.";
            isPass = false;
        }

        // Check if agree policy and privacy
        if (!isAgreePolicy){
            message = "이용약관에 동의해 주세요.";
            isPass = false;
        }

        if (!isAgreePrivacy){
            message = "개인정보 처리 방침에 동의해 주세요.";
            isPass = false;
        }

        // if isPass is false, show dialog
        if (!isPass){
            normalDialogFragment = new NormalDialogFragment(message, null, null);
            normalDialogFragment.show(getParentFragmentManager(), "normalDialog");
        }

        return isPass;
    }

    private void register(){
        boolean isPass = validate();

        if (isPass){
            CompanyManager manager = PreferencesManager.getInstance(getContext()).getCompanyManager();
            Client client = new Client(name.getText().toString(),
                                       phone.getText().toString(),
                                       birth_date.getText().toString(),
                                       selectedSexValue,
                                       height.getText().toString(),
                                       weight.getText().toString(),
                                       address.getText().toString(),
                                       manager,
                                       selectedDiseaseValue);
            Log.e(TAG, "회원의 정보를 서버로 전송합니다.");

            service.createClient(client).enqueue(new Callback<ClientCreationResult>() {
                @Override
                public void onResponse(Call<ClientCreationResult> call, Response<ClientCreationResult> response) {
                    ClientCreationResult result = response.body();

                    if (result.isSuccess()){
//                        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

                        // Show Dialog and when presss okay button popBackStack
                        normalDialogFragment = new NormalDialogFragment(result.getMessage(),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        normalDialogFragment.dismiss();
                                        navController.popBackStack();
                                    }
                                }, null);
                        normalDialogFragment.show(getParentFragmentManager(), "normalDialog");
                    }else{
                        normalDialogFragment = new NormalDialogFragment(result.getMessage(), null, null);
                        normalDialogFragment.show(getParentFragmentManager(), "normalDialog");
                    }
                }

                @Override
                public void onFailure(Call<ClientCreationResult> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    normalDialogFragment = new NormalDialogFragment("회원가입 도중 오류가 발생하였습니다.", null, null);
                    normalDialogFragment.show(getParentFragmentManager(), "normalDialog");
                    Sentry.captureMessage(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enroll_btn:
                register();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton selectedRadioButton = view.findViewById(checkedId);

        if (group.getId() == R.id.sex_type) {
            // Check if any RadioButton is selected
            if (selectedRadioButton != null) {
                // Get the text of the selected RadioButton
                selectedSexValue = sexTypeMapping.get(selectedRadioButton.getText().toString());
            }
        }else if (group.getId() == R.id.disease_type){
            // Check if any RadioButton is selected
            if (selectedRadioButton != null) {
                // Get the text of the selected RadioButton
                selectedDiseaseValue = diseaseTypeMapping.get(selectedRadioButton.getText().toString());
                Log.e(TAG, "Selected Disease: " + selectedDiseaseValue);
            }
        }


    }
}