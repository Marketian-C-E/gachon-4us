package co.kr.myfitnote.views.fragments.standing_balance;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;


public class FeatTogetherSettingFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback {

    private String mTitle;
    private String type;
    private EditText timeEdit;
    private SendEventListener sendEventListener;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    private String videoFileName;

    public FeatTogetherSettingFragment(String title){
        mTitle = title;
    }

    public FeatTogetherSettingFragment(String title, String type){
        mTitle = title;
        type = type;

        switch (type){
            case "exercise_balance_line_test":
                videoFileName = "video_sppb_balance_test";
                break;
            case "exercise_balance_reverse_line_test":
                videoFileName = "video_sppb_balance_normal_half";
                break;
            case "exercise_balance_test":
                videoFileName = "video_sppb_balance_normal";
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            sendEventListener = (SendEventListener) context;
        }catch (ClassCastException e){
            new ClassCastException(context.toString()+" must implement SendEventListener");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feat_together, container, false);
        TextView titleView = view.findViewById(R.id.title_view);
        titleView.setText(mTitle);
        timeEdit = view.findViewById(R.id.time_edit);

        view.findViewById(R.id.test_start_btn).setOnClickListener(this);

        surfaceView = view.findViewById(R.id.explanation_video);
        surfaceHolder = surfaceView.getHolder();
        surfaceView.setZOrderOnTop(true);
        surfaceHolder.addCallback(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.test_start_btn:
                Bundle bundle = new Bundle();
                bundle.putInt("timeSec", Integer.parseInt(timeEdit.getText().toString()))       ;
                sendEventListener.nextFragment(bundle);
                break;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try{
            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/" + videoFileName);
            mediaPlayer.setDataSource(getContext(), uri);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}