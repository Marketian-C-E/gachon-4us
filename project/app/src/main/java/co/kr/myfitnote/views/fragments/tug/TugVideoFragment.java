package co.kr.myfitnote.views.fragments.tug;

//import android.media.session.MediaController;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;

/**
 * 설명영상 프래그먼트
 */
public class TugVideoFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener{
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private View view;
    private String title;
    private String videoUri;
    private SendEventListener sendEventListener;
    private int timer = 30;
    private int containerBackgroundColor = Color.parseColor("#000000ff");
    private int buttonBackgroundColor = Color.parseColor("#00A99F");
    private int visible = View.GONE;


    public TugVideoFragment(String title, String videoUri){
        this.title = title;
        this.videoUri = videoUri;
    }

    public TugVideoFragment(String title, String videoUri, int timer){
        this.title = title;
        this.videoUri = videoUri;
        this.timer = timer;
    }

    //게임용 프로그먼트 생성자
    public TugVideoFragment(String title, String videoUri, int containerBackgroundColor, int buttonBackgroundColor, int timer){
        this.title = title;
        this.videoUri = videoUri;
        this.containerBackgroundColor = containerBackgroundColor;
        this.buttonBackgroundColor = buttonBackgroundColor;
        this.timer = timer;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tug_video, container, false);

        view.findViewById(R.id.leg_test_start_btn).setOnClickListener(this);

        TextView titleView = view.findViewById(R.id.title_view);
        titleView.setText(title);

        view.findViewById(R.id.container).setBackgroundColor(containerBackgroundColor);
        view.findViewById(R.id.leg_test_start_btn).setBackgroundTintList(ColorStateList.valueOf(buttonBackgroundColor));

        surfaceView = view.findViewById(R.id.explanation_video);
        surfaceHolder = surfaceView.getHolder();
        surfaceView.setZOrderOnTop(true);
        surfaceHolder.addCallback(this);
        return view;
    }

    public void setGridSizeVisible(int visible){
        this.visible = visible;
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try{
            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/" + videoUri);
            mediaPlayer.setDataSource(getContext(), uri);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){

    }
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(getContext(), "비디오 재생", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.leg_test_start_btn:
                Bundle bundle = new Bundle();
                sendEventListener.nextFragment(bundle);
                break;
        }
    }
}
