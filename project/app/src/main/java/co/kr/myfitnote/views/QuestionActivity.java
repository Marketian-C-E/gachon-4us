package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.views.view.ProgressCircleView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "Question";

    private Resources resources;

    public enum SurveyStatus {
        시작전,
        시작,
        종료,
        중도탈락
    }
    private SurveyStatus status;
    private String[] questions = null;
    private int questionCurrentIndex = 0;
    private HashMap<String, String> userData;

    private Button leftButton, rightButton;
    private TextView questionTitle, questionDescription;
    private LinearLayout question_progress_view;

    private Retrofit retrofit = RetrofitClient.getRetrofit();
    private RetrofitService service = retrofit.create(RetrofitService.class);

    private ProgressDialog progressDoalog;
    private ArrayList<ProgressCircleView> cvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        resources = getResources();
        leftButton = (Button) findViewById(R.id.button_left);
        rightButton = (Button) findViewById(R.id.button_right);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        questionTitle = (TextView) findViewById(R.id.question_title);
        questionDescription = (TextView) findViewById(R.id.question_description);
        userData = new HashMap<>();
        progressDoalog = new ProgressDialog(this);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setQuestions();
        question_progress_view = findViewById(R.id.question_progress_view);
        cvs = new ArrayList<>();
        for (int i=0; i<questions.length; i++){
            ProgressCircleView pcv = new ProgressCircleView(this, null);
            pcv.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            pcv.setFilled(false);
            cvs.add(pcv);
            question_progress_view.addView(pcv);
        }
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_left:
                switch (status){
                    case 시작전:
                        start();
                        Log.e(TAG, "시작하기");
                        break;
                    case 시작:
                        response(true);
                        Log.e(TAG, "예 클릭");
                        break;
                    case 종료:
                        sendServer();
                        Log.e(TAG, "제출하기");
                        break;
                }
                break;
            case R.id.button_right:
                switch (status){
                    case 시작:
                        response(false);
                        Log.e(TAG, "아니오 클릭");
                        break;
                    case 종료:
                    case 중도탈락:
                        init();
                        Log.e(TAG, "다시하기");
                        break;
                }
                break;
        }
    }

    /**
     * 설문 로직
     */
    void init(){
        status = SurveyStatus.시작전;
        questionCurrentIndex = 0;
        userData.clear();
        resetProgressCircle();
        renderQuestion();
        renderButtons();
    }

    void start(){
        status = SurveyStatus.시작;
        renderButtons();
        renderQuestion();
    }

    void response(boolean bool){
        /**
         * 설문에 응답한 경우
         */
        // 응답이 '예'인가?
        if (bool == true){
            end(false);
            return;
        }

        cvs.get(questionCurrentIndex).setFilled(true);
        userData.put(questions[questionCurrentIndex], String.valueOf(bool));

        questionCurrentIndex += 1;
        if (questionCurrentIndex >= questions.length){
            end(true);
            return;
        }
        renderQuestion();
    }


    void end(boolean isGoal){
        /**
         * isGoal: 중도 탈락 여부
         */
        status = isGoal ? SurveyStatus.종료 : SurveyStatus.중도탈락;
        renderQuestion();
        renderButtons();
    }

    void sendServer(){
        progressDoalog.setMessage("회원님의 정보를 저장 중이에요.");
        progressDoalog.show();

        service.createQuestionResult(new Gson().toJson(userData))
                .enqueue(new Callback<RetrofitStatus>() {
                    @Override
                    public void onResponse(Call<RetrofitStatus> call, Response<RetrofitStatus> response) {
                        progressDoalog.dismiss();
                        if (response.body().getSuccess()){
                            Toast.makeText(QuestionActivity.this,
                                            response.body().getMessage(),
                                            Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<RetrofitStatus> call, Throwable t) {
                        progressDoalog.dismiss();
                    }
                });
    }

    void renderQuestion(){
        if (status == SurveyStatus.종료){
            questionTitle.setText(resources.getString(R.string.question_after_title));
            questionDescription.setText(resources.getString(R.string.question_after_desc));
        }else if (status == SurveyStatus.시작) {
            String question = questions[questionCurrentIndex];
            questionTitle.setText(question);
            questionDescription.setText("");
        }else if (status == SurveyStatus.시작전){
            questionTitle.setText(resources.getString(R.string.question_before_title));
            questionDescription.setText(resources.getString(R.string.question_before_desc));
        }else if (status == SurveyStatus.중도탈락){
            questionTitle.setText(resources.getString(R.string.question_after_title));
            questionDescription.setText(resources.getString(R.string.question_fail_desc));
        }
    }

    void renderButtons(){
        switch (status) {
            case 시작전:
                leftButton.setText("시작하기");
                leftButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.GONE);
                break;
            case 시작:
                leftButton.setText("예");
                rightButton.setText("아니오");
                leftButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                break;
            case 종료:
                leftButton.setText("제출하기");
                rightButton.setText("다시하기");
                leftButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                break;
            case 중도탈락:
                leftButton.setText("제출하기");
                rightButton.setText("다시하기");
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    void resetProgressCircle(){
        for (int i=0; i<cvs.size(); i++){
            cvs.get(i).setFilled(false);
        }
    }


    void setQuestions(){
        if (questions == null) {
            questions = getResources().getStringArray(R.array.questions);
        }
    }
}