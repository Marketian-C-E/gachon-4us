package co.kr.myfitnote.client.ui.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.ui.GradeView;
import co.kr.myfitnote.core.ui.NormalDialogFragment;

public class ExerciseResult extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "ExerciseResult";
    Intent intent;

    private int count, totalCount;
    private String exercise;
    private GradeView level_grade_view, accuracy_grade_view;
    private TextView exercise_tv, exercise_feedback_tv;
    private LinearLayout easy_btn, good_btn, hard_btn;
    private Button home_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_result);

        intent = getIntent();

        exercise_tv = findViewById(R.id.exercise_tv);
        exercise_feedback_tv = findViewById(R.id.exercise_feedback_tv);

        home_btn = findViewById(R.id.home_btn);
        easy_btn = findViewById(R.id.easy_btn);
        good_btn = findViewById(R.id.good_btn);
        hard_btn = findViewById(R.id.hard_btn);
        home_btn.setOnClickListener(this);
        easy_btn.setOnClickListener(this);
        good_btn.setOnClickListener(this);
        hard_btn.setOnClickListener(this);

        count = intent.getIntExtra("count", 0);
        totalCount = intent.getIntExtra("totalCount", 0);
        exercise = intent.getStringExtra("exercise");

        exercise_tv.setText("고생하셨습니다!");
        exercise_feedback_tv.setText("오늘 운동을 통해 신체 능력이 좋아졌어요!");

        level_grade_view = findViewById(R.id.level_grade_view);
        accuracy_grade_view = findViewById(R.id.accuracy_grade_view);

        level_grade_view.setGrade(1);
        accuracy_grade_view.setGrade(1);

        Log.e(TAG, count + " " + totalCount + " " + exercise);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.easy_btn:
                new NormalDialogFragment("다음 운동에 조금 난이도를 올려 드릴게요.", null, null)
                        .show(getSupportFragmentManager(), "dialog");
                Log.e(TAG, "쉬웠다");
                break;
            case R.id.good_btn:
                new NormalDialogFragment("다음 운동에 비슷한 난이도로 설정할게요.", null, null)
                        .show(getSupportFragmentManager(), "dialog");
                Log.e(TAG, "좋았다");
                break;
            case R.id.hard_btn:
                new NormalDialogFragment("다음 운동을 조금 더 쉽게해 드릴게요.", null, null)
                        .show(getSupportFragmentManager(), "dialog");
                Log.e(TAG, "어려웠다.");
                break;
            case R.id.home_btn:
                finish();
                break;
        }
    }
}