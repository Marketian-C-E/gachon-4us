package co.kr.myfitnote.views.fragments.standing_balance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.ScatterChart;

import java.util.ArrayList;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.utils.ChartManager;
import co.kr.myfitnote.views.HomeActivity;
import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class BalanceTestResultActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView secondsView;

    private ArrayList<ParcelablePointF> leftShoulderData;
    private ArrayList<ParcelablePointF> rightShoulderData;
    private ArrayList<ParcelablePointF> leftHipData;
    private ArrayList<ParcelablePointF> rightHipData;
    private ArrayList<ParcelablePointF> leftKneeData;
    private ArrayList<ParcelablePointF> rightKneeData;

    private ScatterChart leftShoulderChart;
    private ScatterChart rightShoulderChart;
    private ScatterChart leftHipChart;
    private ScatterChart rightHipChart;
    private ScatterChart leftKneeChart;
    private ScatterChart rightKneeChart;

    private ChartManager chartManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_leg_stance_test_result);

        chartManager = new ChartManager();

        Intent intent = getIntent();
        int time = intent.getIntExtra("time", 0);

        secondsView = findViewById(R.id.seconds_view);
        secondsView.setText(String.format("%.2f", time / 1000.0));

        findViewById(R.id.home_btn).setOnClickListener(this);


        leftShoulderData = getIntent().getParcelableArrayListExtra("leftShoulderData");
        rightShoulderData = getIntent().getParcelableArrayListExtra("rightShoulderData");
        leftHipData = getIntent().getParcelableArrayListExtra("leftHipData");
        rightHipData = getIntent().getParcelableArrayListExtra("rightHipData");
        leftKneeData = getIntent().getParcelableArrayListExtra("leftKneeData");
        rightKneeData = getIntent().getParcelableArrayListExtra("rightKneeData");

        // Find the ScatterChart views in your layout
        leftShoulderChart = findViewById(R.id.leftShoulderChart);
        rightShoulderChart = findViewById(R.id.rightShoulderChart);
        leftHipChart = findViewById(R.id.leftHipChart);
        rightHipChart = findViewById(R.id.rightHipChart);
        leftKneeChart = findViewById(R.id.leftKneeChart);
        rightKneeChart = findViewById(R.id.rightKneeChart);

        // Set up the charts
        try {
            chartManager.setupChart(leftShoulderChart);
            chartManager.setupChart(rightShoulderChart);
            chartManager.setupChart(leftHipChart);
            chartManager.setupChart(rightHipChart);
            chartManager.setupChart(leftKneeChart);
            chartManager.setupChart(rightKneeChart);
        }catch(Exception err){
            err.printStackTrace();
        }

        // Update the charts with data
        try {
            chartManager.updateChart(leftShoulderChart, leftShoulderData);
            chartManager.updateChart(rightShoulderChart, rightShoulderData);
            chartManager.updateChart(leftHipChart, leftHipData);
            chartManager.updateChart(rightHipChart, rightHipData);
            chartManager.updateChart(leftKneeChart, leftKneeData);
            chartManager.updateChart(rightKneeChart, rightKneeData);
        }catch(Exception err){
            err.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_btn:
                Intent it = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(it);
                break;
        }
    }
}