package co.kr.myfitnote.cm.ui.prescription;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.cm.data.CommonResult;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.prescription.data.PrescriptionDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class makePrescriptionFragment extends Fragment implements View.OnClickListener {
    private String TAG = "makePrescriptionFragment";
    private String clientName;
    private ArrayList<Integer> checkedIds;

    private TextView prescription_title;
    // ai_btn : premium 처방, basic_btn : 베이직 등급 처방
    private LinearLayout prescription_ai_btn, prescription_self_btn, prescription_basic_btn;

    private User user;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    public makePrescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = PreferencesManager.getInstance(getContext()).getUser();
        if (getArguments() != null){
            clientName = getArguments().getString("userName");
            checkedIds = getArguments().getIntegerArrayList("checkedIds");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_make_prescription, container, false);

        prescription_ai_btn = view.findViewById(R.id.prescription_ai_btn);
        prescription_self_btn = view.findViewById(R.id.prescription_self_btn);
        prescription_basic_btn = view.findViewById(R.id.prescription_basic_btn);

        prescription_ai_btn.setOnClickListener(this);
        prescription_self_btn.setOnClickListener(this);
        prescription_basic_btn.setOnClickListener(this);

        prescription_title = view.findViewById(R.id.prescription_title);
        prescription_title.setText(clientName + "님의 운동 프로그램을\n발급합니다.");

        return view;
    }

    /**
     * AI를 통하여 처방전을 발급합니다.
     *
     * type : 1 => 프리미엄 처방
     * type : 2 => 베이직 등급 처방
     * @param type
     */
    public void makePrescriptionWithAi(int type) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("AI가 처방전을 발급 중입니다.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        service.makePrescriptionWithAI("Token " + user.getToken(), checkedIds, type).enqueue(new Callback<CommonResult>() {
            @Override
            public void onResponse(Call<CommonResult> call, Response<CommonResult> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful()){
                    CommonResult result = response.body();
                    new NormalDialogFragment(result.getMessage(), null, null).show(getFragmentManager(), "NormalDialogFragment");
                }
            }

            @Override
            public void onFailure(Call<CommonResult> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prescription_ai_btn:
                Log.e(TAG, "AI를 통하여 발급합니다.");
                makePrescriptionWithAi(1);
                break;
            case R.id.prescription_self_btn:
                Log.e(TAG, "수동 운동 프로그램 처방");
                new NormalDialogFragment("수동 운동 프로그램 처방 서비스는 준비 중입니다.", null, null)
                        .show(getFragmentManager(), "NormalDialogFragment");
                break;
            case R.id.prescription_basic_btn:
                Log.e(TAG, "베이직 운동 프로그램 처방");
                makePrescriptionWithAi(2);
                break;
        }
    }
}