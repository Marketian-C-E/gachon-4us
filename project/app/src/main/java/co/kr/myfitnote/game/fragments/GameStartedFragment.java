package co.kr.myfitnote.game.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.game.Subscribe;
import co.kr.myfitnote.game.model.Game;
import co.kr.myfitnote.game.model.GameResult;
import co.kr.myfitnote.game.model.GreenBall;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.User;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GameStartedFragment extends Fragment implements Subscribe, View.OnTouchListener, View.OnClickListener {
    private  int GAME_TIME_SEC = 100;
    private int gameTimeSec;
    private Timer gameTimer;
    private Timer showTimer;
    private Timer hideTimer;

    private SendEventListener sendEventListener;
    private Game game;
    private String title;
    private View view;
    private int gridSize;

    private ImageView gridImageView;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private ViewTreeObserver viewTreeObserver;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    SharedPreferences pref;
    String userData;
    User user;


    public GameStartedFragment(String title){
        this.title = title;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "");
        user = gson.fromJson(userData, User.class);

        try{
            sendEventListener = (SendEventListener) context;
        }catch (ClassCastException e){
            new ClassCastException(context.toString()+" must implement SendEventListener");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game_stared, container, false);
        GAME_TIME_SEC = Integer.parseInt(getArguments().getString("timer"));
        gameTimeSec = GAME_TIME_SEC;

        TextView titleView = view.findViewById(R.id.title_view);
        titleView.setText(title);
        gridImageView = view.findViewById(R.id.grid_image_view);
        gridImageView.setOnTouchListener(this);

        view.findViewById(R.id.pause_btn).setOnClickListener(this);
        view.findViewById(R.id.restart_btn).setOnClickListener(this);

        gridSize = Integer.parseInt(String.valueOf(getArguments().getString("gridSize").charAt(0)));
        game = new Game(gridSize, gameTimeSec);
        game.subscribe(this);
        game.start();



        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onGlobalLayout() {

                if(game.getGridImageView() != null) return;

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                game.setDensity(displayMetrics.density);
                game.setGridImageView(gridImageView);
                game.draw();
                game.drawObject((int)(Math.random() * (gridSize * gridSize)));

            }
        };
        viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(mOnGlobalLayoutListener);


        return view;
    }

    private void startGameTimerTask(){

        sendEventListener.setStatus(Exercise.START);

        gameTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            TextView gameTimerView = view.findViewById(R.id.game_timer_view);
            ProgressBar progressBar = view.findViewById(R.id.progressbar);
            @Override
            public void run(){
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @SneakyThrows
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            double progress = (double) gameTimeSec / GAME_TIME_SEC * 100;
                            Log.e("poapo", progress + "");
                            progressBar.setProgress((int) progress, false);
                            gameTimerView.setText(gameTimeSec + "");
                            gameTimeSec--;
                            if (gameTimeSec == -1) {
                                gameTimer.cancel();

                                GameResult gameResult = game.getGameResult();
                                Bundle bundle = new Bundle();
                                bundle.putInt("successCount", gameResult.getSuccessCount());
                                bundle.putInt("greenBallCount", gameResult.getGreenBallCount());
                                bundle.putInt("failCount", gameResult.getFailCount());
                                bundle.putInt("totalTimeSEC", gameResult.getTotalTimeSEC());
//                            bundle.putIntegerArrayList("getClickedTime", gameResult.getClickedTime());
//                            bundle.putStringArrayList("clickedBall", gameResult.getClickedBall());

                                // send data to server
                                sendLogData(gameResult);

                                sendEventListener.nextFragment(bundle);

                            }
                        }
                    });
                }
            }
        };

        gameTimer.schedule(timerTask,0 ,1000);
    }

    private void startHideTimer(int delay){
         hideTimer = new Timer();

        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run(){
//                game.setGridId(-1);
                startShowTimer(game.getGameObject().getShowMS());
                game.hideObject();
                hideTimer.cancel();
            }
        };

        hideTimer.schedule(timerTask,delay );
    }

    private void stopHideTimer(){
        if(hideTimer != null) hideTimer.cancel();
     }

    private void startShowTimer(int delay){
        showTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run(){
                game.drawObject((int)(Math.random() * (gridSize * gridSize)));
                showTimer.cancel();
            }
        };

        showTimer.schedule(timerTask,delay );

    }

    public void stopShowTimer(){
        if(showTimer != null){
            showTimer.cancel();
        }
    }


    private void stopGameTimerTask(){
        if(gameTimer != null){
            gameTimer.cancel();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopGameTimerTask();
        stopShowTimer();
        stopHideTimer();

    }



    @Override
    public void update(int status ,String data) {
        switch (status){
            case Game.START:
                startGameTimerTask();

                break;
            case Game.PAUSE:

                break;

            case  Game.TIMER:
                startHideTimer(game.getGameObject().getHideMS());
                break;

            case  Game.COUNT:
                TextView textView = view.findViewById(R.id.count_view);
                textView.setText(game.getGameResult().getSuccessCount()+"");
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        GameResult gameResult = game.getGameResult();
        switch( event.getActionMasked() ){
            case MotionEvent.ACTION_UP:
                if(game.isHit(x,y)){

                    if(game.getGameObject() instanceof GreenBall){
                        game.setSuccessCount(gameResult.getSuccessCount() + 1);
                        gameResult.addClickedBall("GREEN");
                        gameResult.addClickedTime(System.currentTimeMillis());
                    }else{
                        game.setFailCount(gameResult.getFailCount() + 1);
                        gameResult.addClickedBall("RED");
                        gameResult.addClickedTime(System.currentTimeMillis());
                    }

                    stopShowTimer();
                    stopHideTimer();
                    game.hideObject();
                    game.setGridId(-1);
                    startShowTimer(game.getGameObject().getShowMS());
                }
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        Button pauseButton = view.findViewById(R.id.pause_btn);
        switch (v.getId()){
            case R.id.pause_btn:

                if(pauseButton.getText().toString().equals("검사중지")){
                    stopHideTimer();
                    stopGameTimerTask();

                    pauseButton.setText("검사시작");
                }else{
                    startGameTimerTask();
                    startHideTimer(game.getGameObject().getHideMS());
                    pauseButton.setText("검사중지");
                }

                break;
            case R.id.restart_btn:
                sendEventListener.restart();
                break;
        }
    }

    private void sendLogData(GameResult gameResult){
        if (gameResult != null){
            // if there is log data, send to server for stroing in DB
            new Thread(() -> {
                Call<RetrofitStatus> request = service.createExerciseResult(user.getToken(),
                                                                            new Gson().toJson(gameResult, GameResult.class));
                request.enqueue(new Callback<RetrofitStatus>() {
                    @Override
                    public void onResponse(Call<RetrofitStatus> call, Response<RetrofitStatus> response) {
                        Log.e("RESULT", "come to response");
                    }

                    @Override
                    public void onFailure(Call<RetrofitStatus> call, Throwable t) {
                        Log.e("RESULT", t.getMessage());
                    }
                });
            }).start();
        }
    }
}
