package co.kr.myfitnote.cm.ui.measurement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.client.ui.home.ClientHomeActivity;
import co.kr.myfitnote.cm.ui.client.ClientAdapter;
import co.kr.myfitnote.cm.ui.client.ClientResultAdapter;
import co.kr.myfitnote.cm.ui.home.CmHomeActivity;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.NormalDialogFragment;
import co.kr.myfitnote.core.utils.PreferencesManager;
import co.kr.myfitnote.measurement.data.ClientMeasurementData;
import co.kr.myfitnote.measurement.data.ClientMeasurementResultData;
import co.kr.myfitnote.model.RetrofitStatus;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MeasurementResultListFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MeasurementResultListFragment";
    private String ARG_NAME = "name";
    private String ARG_PHONE = "phone";

    private RecyclerView recyclerView;
    private NavController navController;
    private User user;
    private Client client;
    private String name, phone, username, token;
    private TextView headerTV;
    private View view;
    private Button resultBtn, deleteBtn, exerciseBtn;
    private PreferencesManager preferencesManager;

    private ArrayList<ClientMeasurementData> results = new ArrayList<>();

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);


    public MeasurementResultListFragment() {
        // Required empty public constructor
    }

    public static MeasurementResultListFragment newInstance(String param1, String param2) {
        MeasurementResultListFragment fragment = new MeasurementResultListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new Client();
        navController = NavHostFragment.findNavController(this);
        preferencesManager = PreferencesManager.getInstance(getContext());
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            phone = getArguments().getString(ARG_PHONE);
            username = getArguments().getString("username");
            token = getArguments().getString("token");
            client.setName(name);
            client.setPhone(phone);
            client.setUsername(username);
            client.setToken(token);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_measurement_result, container, false);

        headerTV = view.findViewById(R.id.cm_ms_result_header_tv);
        headerTV.setText(name + "님의 현재까지\n측정한 검사 리스트입니다.");

        resultBtn = view.findViewById(R.id.result_btn);
        deleteBtn = view.findViewById(R.id.delete_btn);
        resultBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

//        exerciseBtn = view.findViewById(R.id.exercise_btn);
//        exerciseBtn.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.clientResultRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ClientResultAdapter adapter = new ClientResultAdapter(results, navController);
        recyclerView.setAdapter(adapter);

        getClientMeasurements();

        return view;
    }

    public void getClientMeasurements(){
        user = PreferencesManager.getInstance(getContext()).getUser();

        service.getClientMeasurements("Token " + user.getToken(), name, phone)
                .enqueue(new Callback<ArrayList<ClientMeasurementData>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ClientMeasurementData>> call, Response<ArrayList<ClientMeasurementData>> response) {
                        results = response.body();
                        updateAdapter(results);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ClientMeasurementData>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        Sentry.captureMessage(t.getMessage());
                    }
                });

    }

    private void updateAdapter(ArrayList<ClientMeasurementData> results) {
        // Assuming you have a reference to your RecyclerView and adapter
        recyclerView = view.findViewById(R.id.clientResultRecyclerView);

        // Get the existing adapter and update its data
        ClientResultAdapter adapter = (ClientResultAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateData(results);
        }
    }

    private void deleteItems(){
        ClientResultAdapter adapter1 = (ClientResultAdapter) recyclerView.getAdapter();
        List<ClientMeasurementData> clickedData1 = adapter1.getCheckedClientMeasurements();
        ArrayList<Integer> clickedIds = new ArrayList<>();
        for (int i=0; i<clickedData1.size(); i++) {
            Log.e(TAG, "Item => " + clickedData1.get(i).getId());
            clickedIds.add(clickedData1.get(i).getId());
        }
        Log.e(TAG, "삭제 대사 측정 데이터 개수 =>" + clickedData1.size());

        // Send to clicked data to server
        service.deleteClientMeasurements("Token " + user.getToken(), clickedIds)
                .enqueue(new Callback<RetrofitStatus>() {
                    @Override
                    public void onResponse(Call<RetrofitStatus> call, Response<RetrofitStatus> response) {
                        RetrofitStatus result = response.body();
                        Log.e(TAG, "삭제 결과 => " + result.getMessage());
                        if (result.getSuccess()){
                            new NormalDialogFragment(result.getMessage(), null, null).show(getFragmentManager(), "message");
                            getClientMeasurements();
                        }else{
                            new NormalDialogFragment(result.getMessage(), null, null).show(getFragmentManager(), "message");
                        }
                    }

                    @Override
                    public void onFailure(Call<RetrofitStatus> call, Throwable t) {
//                                Log.e(TAG, t.getMessage());
//                                Sentry.captureMessage(t.getMessage());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.result_btn:
                Bundle bundle = new Bundle();
                ArrayList<Integer> checkedIds = new ArrayList<>();

                ClientResultAdapter adapter = (ClientResultAdapter) recyclerView.getAdapter();
                List<ClientMeasurementData> clickedData = adapter.getCheckedClientMeasurements();
                Log.e(TAG, "결과 산출 버튼 클릭 =>" + clickedData.size());

                if (clickedData.size() == 0){
                    // When clicked data is empty
                    new NormalDialogFragment("결과 조회를 위해 하나 이상 선택해 주세요.", null, null).show(getFragmentManager(), "message");
                    return;
                }

                for (int i=0; i<clickedData.size(); i++) {
                    Log.e(TAG, "Item => " + clickedData.get(i).getId());
                    checkedIds.add(clickedData.get(i).getId());
                }

                bundle.putIntegerArrayList("checkedIds", checkedIds);
                navController.navigate(R.id.action_measurementResultListFragment_to_measurementFinalResultFragment, bundle);
                break;
            case R.id.delete_btn:
                // When delete button is pressed
                Log.e(TAG, "삭제 버튼 클릭");
                deleteItems();


                break;
        }
    }
}