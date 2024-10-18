package co.kr.myfitnote.views.rom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.utils.AnimationUtil;

public class RomResultFragment extends Fragment implements View.OnClickListener {
    static final private String TAG = "RomResultFragment";

    private ImageView result_image;
    private TextView tv_text, result_text;
    private Button btn_restart, btn_finish;
    private ListView result_list_view;

    private String type = "";
    private Bundle bundle;
    private ArrayList<RomResultData> resultData;

    private OnFragmentListener listener;

    public RomResultFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentListener){
            listener = (OnFragmentListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rom_result, container, false);

        if (getArguments() != null){
            bundle = getArguments();
        }
        tv_text = view.findViewById(R.id.tv_text);
        result_text = view.findViewById(R.id.result_text);
        result_image = view.findViewById(R.id.result_image);
        btn_restart = view.findViewById(R.id.btn_restart);
        btn_finish = view.findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(this);
        btn_restart.setOnClickListener(this);
        type = bundle.getString("type");

        // Update text needs to below process
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                AnimationUtil.applyBounceAnimationToView(img_header, 2);
                tv_text.setText(type + " 검사 결과");

                // Set bitmap image which passed from bundle to result_image
                if (bundle.getParcelable("resultImage") != null){
                    result_image.setImageBitmap(bundle.getParcelable("resultImage"));
                }

                if (bundle.getString("resultStr") != null){
                    // ROM, 자세 측정 검사 결과
                    resultData = (ArrayList<RomResultData>) bundle.getSerializable("resultData");
                    result_text.setText(bundle.getString("resultStr"));

                }
            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_finish:
                Log.e(TAG, "Pressed btn_finish!");
                listener.onMessage("action_finish");
                break;
            case R.id.btn_restart:
                Log.e(TAG, "Pressed btn_restart!");
                listener.onMessage("action_restart");
                break;
        }
    }

    public interface OnFragmentListener{
        void onMessage(String message);
    }
}