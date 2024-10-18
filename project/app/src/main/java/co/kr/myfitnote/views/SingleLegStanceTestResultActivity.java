package co.kr.myfitnote.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import co.kr.myfitnote.R;
import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class SingleLegStanceTestResultActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Single-Result";
    private TextView secondsView;
    private TextView gradeView;

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

    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_leg_stance_test_result);

        Intent intent = getIntent();
        int time = intent.getIntExtra("time", 0);
        boolean isPass = intent.getBooleanExtra("isPass", false);

        secondsView = findViewById(R.id.seconds_view);
        secondsView.setText(String.format("%.2f초", time / 1000.0));

        gradeView = findViewById(R.id.grade_view);
        gradeView.setText(isPass ? "Passed" : "Failed");
        gradeView.setBackgroundTintList(ColorStateList.valueOf(getResources()
                                                              .getColor(isPass ? R.color.n_blue : R.color.n_red)));

        findViewById(R.id.home_btn).setOnClickListener(this);

        leftShoulderData = getIntent().getParcelableArrayListExtra("leftShoulderData");
        rightShoulderData = getIntent().getParcelableArrayListExtra("rightShoulderData");
        leftHipData = getIntent().getParcelableArrayListExtra("leftHipData");
        rightHipData = getIntent().getParcelableArrayListExtra("rightHipData");
        leftKneeData = getIntent().getParcelableArrayListExtra("leftKneeData");
        rightKneeData = getIntent().getParcelableArrayListExtra("rightKneeData");

        Log.d(TAG, leftShoulderData.size() + " ");


        // Find the ScatterChart views in your layout
        leftShoulderChart = findViewById(R.id.leftShoulderChart);
        rightShoulderChart = findViewById(R.id.rightShoulderChart);
        leftHipChart = findViewById(R.id.leftHipChart);
        rightHipChart = findViewById(R.id.rightHipChart);
        leftKneeChart = findViewById(R.id.leftKneeChart);
        rightKneeChart = findViewById(R.id.rightKneeChart);

        // Set up the charts
        try {
            setupChart(leftShoulderChart);
            setupChart(rightShoulderChart);
            setupChart(leftHipChart);
            setupChart(rightHipChart);
            setupChart(leftKneeChart);
            setupChart(rightKneeChart);
        }catch(Exception err){
            err.printStackTrace();
        }

        // Update the charts with data
        try {
            updateChart(leftShoulderChart, leftShoulderData);
            updateChart(rightShoulderChart, rightShoulderData);
            updateChart(leftHipChart, leftHipData);
            updateChart(rightHipChart, rightHipData);
            updateChart(leftKneeChart, leftKneeData);
            updateChart(rightKneeChart, rightKneeData);
        }catch(Exception err){
            err.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_btn:
                this.finish();
//                Intent it = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(it);
                break;
        }
    }

    private void setupChart(ScatterChart chart) {

        chart.getDescription().setEnabled(false);
        chart.setNoDataText("");
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(-0.6f);
        xAxis.setAxisMaximum(0.6f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceMin(0f);
        xAxis.setSpaceMax(0f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(-0.6f);
        yAxis.setAxisMaximum(0.6f);
        yAxis.setSpaceBottom(0f);
        yAxis.setSpaceTop(0f);

        chart.getAxisRight().setEnabled(false);

        // Custom x and y values for the vertical and horizontal lines
        float verticalXValue = 0f; // Example value
        float horizontalYValue = 0f; // Example value

        // Draw vertical line
        LimitLine verticalLimitLine = new LimitLine(verticalXValue);
        verticalLimitLine.setLineColor(Color.GRAY);
        verticalLimitLine.setLineWidth(2f);
        chart.getXAxis().addLimitLine(verticalLimitLine);

        // Draw horizontal line
        LimitLine horizontalLimitLine = new LimitLine(horizontalYValue);
        horizontalLimitLine.setLineColor(Color.GRAY);
        horizontalLimitLine.setLineWidth(2f);
        chart.getAxisLeft().addLimitLine(horizontalLimitLine);
    }
    private void updateChart(ScatterChart chart, ArrayList<ParcelablePointF> dataList) {
        ArrayList<Entry> entries = new ArrayList<>();

        try{
            // Iterate over the dataList and convert ParcelablePointF objects to Entry objects
            Iterator<ParcelablePointF> iterator = dataList.iterator();
            while (iterator.hasNext()) {
                ParcelablePointF point = iterator.next();
                entries.add(new Entry(point.x, point.y));
            }

            Collections.sort(entries, new EntryXComparator());

            // Create a ScatterDataSet with the list of Entries
            ScatterDataSet dataSet = new ScatterDataSet(entries, null);
            dataSet.setDrawValues(false);
            dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            dataSet.setScatterShapeSize(10f);
            dataSet.setColor(Color.rgb(0, 176, 166));

            // Create a ScatterData object with the ScatterDataSet
            ScatterData scatterData = new ScatterData(dataSet);
            chart.setData(scatterData);

            chart.invalidate();
        }catch (NegativeArraySizeException err){
            err.printStackTrace();
        }
    }

    private String formatAxisValue(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(value);
    }

}