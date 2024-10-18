package co.kr.myfitnote.cm.ui.my;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.CompanyManager;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.core.utils.PreferencesManager;


public class MyScreenFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "MyScreenFragment";

    private TextView tvTitle;
    private Button logoutButton, connectBLEButton;
    private CompanyManager companyManager;
    private PreferencesManager preferencesManager;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_screen, container, false);
        navController = NavHostFragment.findNavController(this);

        preferencesManager = PreferencesManager.getInstance(getContext());

        companyManager = PreferencesManager.getInstance(getContext())
                .getCompanyManager();

        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(String.format("%s님, 환영합니다.", companyManager.getName()));

        logoutButton = view.findViewById(R.id.logoutButton);
        connectBLEButton = view.findViewById(R.id.connectBLEButton);

        logoutButton.setOnClickListener(this);
        connectBLEButton.setOnClickListener(this);

        return view;
    }

    private void logout(){
        // Go to login screen after clear data
        View.OnClickListener positiveButtonEvent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferencesManager.clearData(new String[]{"MANAGER_DATA", "LOGO_PATH"});
                getActivity().finish();
            }
        };

        // Show dialog for check whether user really want to logout
        new NormalDialogFragment("정말 로그아웃 하시겠어요?", positiveButtonEvent, null).show(getParentFragmentManager(), "logout");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logoutButton:
                logout();
                break;
            case R.id.connectBLEButton:
                // BLE 연결 페이지 이동
                navController.navigate(R.id.action_myScreenFragment2_to_connectBluetoothDeviceFragment);
                break;
        }
    }
}