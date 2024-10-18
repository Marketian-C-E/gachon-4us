package co.kr.myfitnote.cm.ui.client;

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
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.utils.PreferencesManager;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClientSearchFragment extends Fragment {
    private String TAG = "ClientSearchFragment";

    private View view;
    private User user;

    private ArrayList<Client> clientList = new ArrayList<>();

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    private NavController navController;

    private EditText searchEditText;
    private Button searchButton;
    private String searchText = "";

    public ClientSearchFragment() {
        // Required empty public constructor
    }

    public static ClientSearchFragment newInstance() {
        ClientSearchFragment fragment = new ClientSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_client_search, container, false);
        navController = NavHostFragment.findNavController(this);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            searchText = searchEditText.getText().toString();
            getClients(searchText);
        });

        RecyclerView recyclerView = view.findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 수직, 수평, 그리드와 같이 배치할 형태를 관리할 매니저

        ClientAdapter adapter = new ClientAdapter(clientList, navController, getContext());
        recyclerView.setAdapter(adapter);

        getClients(searchText);

        return view;
    }

    public void getClients(String searchText){
        user = PreferencesManager.getInstance(getContext()).getUser();
        service.getClientList("Token " + user.getToken(), searchText).enqueue(new Callback<ArrayList<Client>>() {
            @Override
            public void onResponse(Call<ArrayList<Client>> call, Response<ArrayList<Client>> response) {
                ArrayList<Client> clients = response.body();
                for (int i=0; i<clients.size(); i++){
                    Log.e(TAG, "Client => " + clients.get(i).getName());
                }
                updateAdapter(clients);
            }

            @Override
            public void onFailure(Call<ArrayList<Client>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Sentry.captureMessage(t.getMessage());
            }
        });
    }

    private void updateAdapter(ArrayList<Client> clients) {
        // Assuming you have a reference to your RecyclerView and adapter
        RecyclerView recyclerView = view.findViewById(R.id.clientRecyclerView);

        // Get the existing adapter and update its data
        ClientAdapter adapter = (ClientAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateData(clients);
        }
    }

}