package co.kr.myfitnote;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import co.kr.myfitnote.account.data.model.CompanyManager;
import co.kr.myfitnote.account.data.model.User;
import co.kr.myfitnote.cm.data.CommonResult;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.utils.PreferencesManager;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoardWriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardWriteFragment extends Fragment {
    private static final String TAG = "BoardWriteFragment";

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    FloatingActionButton fabSavePost;

    EditText etTitle, etContent;

    User user;

    public BoardWriteFragment() {
        // Required empty public constructor
    }


    public static BoardWriteFragment newInstance(String param1, String param2) {
        BoardWriteFragment fragment = new BoardWriteFragment();
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
        View view = inflater.inflate(R.layout.fragment_board_write, container, false);

        user = PreferencesManager.getInstance(getContext()).getUser();
        Log.d(TAG, user.getToken() + "<= this is a user token");

        etTitle = view.findViewById(R.id.editTextTitle);
        etContent = view.findViewById(R.id.editTextContent);

        fabSavePost = view.findViewById(R.id.fabSavePost);
        fabSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 글 저장
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();

                // if title is empty, return
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                BoardItem item = new BoardItem(title, content, "");

                // Send to Server using Retrofit in background service
                service.writeBoard("Token " + user.getToken(), item).enqueue(new retrofit2.Callback<CommonResult>() {
                    @Override
                    public void onResponse(retrofit2.Call<CommonResult> call, retrofit2.Response<CommonResult> response) {
                        Log.d(TAG, "onResponse: " + response.code());
                        if (response.code() == 201) {
                            // 글 저장 성공
                            Navigation.findNavController(view).popBackStack();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<CommonResult> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        });
        return view;
    }
}