package co.kr.myfitnote.core.ui.client;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import co.kr.myfitnote.R;

public class HomeTodayExerciseView extends LinearLayout  {
    private TextView classfication_tv, exercise_name_tv;

    public HomeTodayExerciseView(Context context) {
        super(context);
        init(context);
    }

    public HomeTodayExerciseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTodayExerciseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.home_today_exercise_layout, this, true);

        classfication_tv = findViewById(R.id.classfication_tv);
        exercise_name_tv = findViewById(R.id.exercise_name_tv);
    }

    public void setClassfication(String classfication) {
        classfication_tv.setText(classfication);
    }

    public void setExerciseName(String exercise_name) {
        exercise_name_tv.setText(exercise_name);
    }
}
