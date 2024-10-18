package co.kr.myfitnote;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends Fragment {

    RecyclerView classRecyclerView;
    ArrayList<ClassItem> data;
    ClassAdapter adapter;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance(String param1, String param2) {
        InformationFragment fragment = new InformationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_information, container, false);
        classRecyclerView = view.findViewById(R.id.classRecyclerView);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get class list from server
        service.getClassList(0).enqueue(new Callback<ArrayList<ClassItem>>() {
            @Override
            public void onResponse(Call<ArrayList<ClassItem>> call, Response<ArrayList<ClassItem>> response) {
                if (response.isSuccessful()) {
                    ArrayList<ClassItem> items = response.body();
                    for (ClassItem item : items) {
                        data.add(new ClassItem(item.getName(), item.getDate(), item.getpartipants(), item.getThumbnailUrl()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ClassItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        adapter = new ClassAdapter(data, getContext());
        classRecyclerView.setAdapter(adapter);

        return view;
    }
}