package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import co.kr.myfitnote.R;

public class TimedUpAndGoTestResultActivity extends AppCompatActivity {

    private TextView secondsView;
    private TextView gradeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_up_and_go_test_result);

        Intent intent = getIntent();
        int time = intent.getIntExtra("time", 0);
        secondsView = findViewById(R.id.seconds_view);
        secondsView.setText(String.format("%.2f", time / 1000.0));


    }
}