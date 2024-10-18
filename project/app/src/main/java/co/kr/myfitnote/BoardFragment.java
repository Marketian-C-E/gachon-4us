package co.kr.myfitnote;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardFragment extends Fragment {

    private RecyclerView recyclerView;
    private BoardAdapter adapter;
    private FloatingActionButton fabAddPost;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);


    public BoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoardFragment newInstance(String param1, String param2) {
        BoardFragment fragment = new BoardFragment();
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
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddPost = view.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 글 작성 페이지로 이동
                Navigation.findNavController(view).navigate(R.id.action_boardFragment_to_boardWriteFragment);
            }
        });
        // Get board list from server
        service.getBoardList(0).enqueue(new Callback<ArrayList<BoardItem>>() {
            @Override
            public void onResponse(Call<ArrayList<BoardItem>> call, Response<ArrayList<BoardItem>> response) {
                if (response.isSuccessful()) {
                    adapter = new BoardAdapter(getContext(), response.body(), new BoardAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BoardItem item) {
                            // 클릭한 아이템의 정보를 상세 페이지로 전달
                            Bundle bundle = new Bundle();
                            bundle.putString("title", item.getTitle());
                            bundle.putString("content", item.getContent());
                            // bundle.putString("thumbnail", item.getThumbnail());
                            bundle.putString("writer", item.getWriter());
                            bundle.putString("date", item.getDate());
                            Navigation.findNavController(view).navigate(R.id.action_boardFragment_to_boardDetailFragment, bundle);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "서버에서 데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BoardItem>> call, Throwable t) {

            }
        });

        return view;
    }
}