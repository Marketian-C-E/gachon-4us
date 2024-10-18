package co.kr.myfitnote.cm.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.CompanyManager;
import co.kr.myfitnote.core.ui.AnimationManager;
import co.kr.myfitnote.core.ui.LogoManager;
import co.kr.myfitnote.core.utils.CommonUtil;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.webview.WebviewActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {
    static final private String TAG = "HomeFragment";

    private TextView tv_home_header, total_client_tv, new_client_tv, date_tv, date2_tv;
    private LinearLayout resultBtn, clientSearchBtn, clientCreateBtn, btn_experience_btn, btn_client_result;
    private ImageView logo;

    private CompanyManager companyManager;

    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        AnimationManager animationManager = new AnimationManager();

        companyManager = PreferencesManager.getInstance(getContext())
                                           .getCompanyManager();
        tv_home_header = rootView.findViewById(R.id.tv_home_header);
        tv_home_header.setText("오늘도 좋은 하루 되세요!");

        total_client_tv = rootView.findViewById(R.id.total_client_tv);
        new_client_tv = rootView.findViewById(R.id.new_client_tv);
        date2_tv = rootView.findViewById(R.id.date2_tv);
        date2_tv.setText(CommonUtil.getTodayDate());

        logo = rootView.findViewById(R.id.company_logo);
        LogoManager.setLogoToImageView(getContext(), logo);

        clientSearchBtn = rootView.findViewById(R.id.btn_client_search);
        clientCreateBtn = rootView.findViewById(R.id.btn_client_create);
        btn_experience_btn = rootView.findViewById(R.id.btn_experience_btn);
        btn_client_result = rootView.findViewById(R.id.btn_client_result);

        clientCreateBtn.setOnClickListener(this);
        clientSearchBtn.setOnClickListener(this);
        btn_experience_btn.setOnClickListener(this);
        btn_client_result.setOnClickListener(this);

        navController = NavHostFragment.findNavController(this);

        animationManager.animateTextView(0, companyManager.getTotal_client_cnt(), total_client_tv);
        animationManager.animateTextView(0, companyManager.getNew_clint_cnt(), new_client_tv);

        return rootView;
    }

    private void launchWebViewActivity() {
        /**
         * Launches the webview activity
         * 사용자 결과 페이지로 이동
         */
         Intent intent = new Intent(getContext(), WebviewActivity.class);
         String url = getString(R.string.host_url) + getString(R.string.client_result_uri);
         intent.putExtra("url", url);
         startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_client_search:
                navController.navigate(R.id.action_homeFragment2_to_clientSearchFragment);
                break;
            case R.id.btn_client_create:
                navController.navigate(R.id.action_homeFragment2_to_clientCreateFragment);
                break;
            case R.id.btn_experience_btn:
                navController.navigate(R.id.action_homeFragment2_to_experienceMenuFragment);
                break;
            case R.id.btn_client_result:
                launchWebViewActivity();
                break;
        }
    }
}