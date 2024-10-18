package co.kr.myfitnote.measurement.ui.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.ui.GradeView;
import co.kr.myfitnote.core.utils.MillisecondsToSecondsFormatter;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.SeatDownUp;
import co.kr.myfitnote.model.SingLegStance;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.views.HomeActivity;
import co.kr.myfitnote.views.parcel.ParcelablePointF;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MeasurementResultFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MeasurementResultFragment";
    private MeasurementEventListener sendEventListener;
    private View view;
    private TestResult testResult;
    private TextView testResultHeaderTV;
    private TextView measurementItemTV;

    private LinearLayout resultPanelGrade, resultPanelPnp, layout_pattern_analysis;

    private GradeView gradeUIView;
    private TextView gradeView;

    User user;

    boolean useSecond = false;
    boolean isExperience = false;

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

    public MeasurementResultFragment(){}

    public MeasurementResultFragment(boolean useSecond){
        this.useSecond = useSecond;
    }

    public MeasurementResultFragment(boolean useSecond, boolean isExperience){
        this.useSecond = useSecond;
        this.isExperience = isExperience;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            sendEventListener = (MeasurementEventListener) context;
        } catch (ClassCastException e) {
            new ClassCastException(context.toString() + " must implement SendEventListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_result, container, false);

        view.findViewById(R.id.home_btn).setOnClickListener(this);
//        view.findViewById(R.id.next_btn).setOnClickListener(this);

        TextView countView = view.findViewById(R.id.count_view);
//        TextView gradeView = view.findViewById(R.id.grade_view);
        TextView timerView = view.findViewById(R.id.timer_view);
        testResultHeaderTV = view.findViewById(R.id.test_result_header_tv);
        measurementItemTV = view.findViewById(R.id.measurement_item_tv);

        resultPanelPnp = view.findViewById(R.id.result_panel_pnp);
        resultPanelGrade = view.findViewById(R.id.result_panel_count_level);

        measurementItemTV.setText(sendEventListener.getExercise().getExerciseLabel());
        // 클라이언트 이름
        if (!isExperience) {
            testResultHeaderTV.setText(sendEventListener.getClient().getName());
        }else{
            testResultHeaderTV.setText("체험자");
        }

        testResult = sendEventListener.getTestResult();

        // 패턴 분석 레이아웃
        layout_pattern_analysis = view.findViewById(R.id.layout_pattern_analysis);

        gradeUIView = view.findViewById(R.id.grade_ui_view);
        gradeView = view.findViewById(R.id.grade_view);

        if (sendEventListener.getExercise().getExerciseLabel().equals("외발서기")){
            SingLegStance singLegStance = (SingLegStance) sendEventListener.getExercise().getFunctionTest(0);
            boolean isPass = singLegStance.getGrade();
            resultPanelPnp.setVisibility(View.VISIBLE);
            resultPanelGrade.setVisibility(View.GONE);
            layout_pattern_analysis.setVisibility(View.GONE);
            countView = view.findViewById(R.id.pnp_seconds_view);
            gradeView = view.findViewById(R.id.pnp_grade_view);
            gradeView.setText(isPass ? "Passed" : "Failed");
            gradeView.setBackgroundTintList(ColorStateList.valueOf(getResources()
                    .getColor(isPass ? R.color.n_blue : R.color.n_red)));
        }else{
//            gradeView.setText(testResult.getGrade()+"");
            gradeView.setText(testResult.getGradeResultLabel());
            gradeUIView.setGrade(testResult.getGrade());

//            if (testResult.getType().equals("WALK")){
//                String strGrade = "미달";
//                if (testResult.getGrade() == 1){
//                    strGrade = "상";
//                }else if (testResult.getGrade() == 2){
//                    strGrade = "적정";
//                }
//                gradeView.setText(strGrade);
//            }

            configChart(view.findViewById(R.id.line_chart));
        }

        if (useSecond) {
            countView.setText(String.format("%.2f", testResult.getSecond() / 1000.0) + "초");
//            tvResultLabel.setText("시간");
        }else{
            countView.setText(testResult.getCount() + "회");
            timerView.setText(testResult.getSecond() + "초");

        }


        // Find the ScatterChart views in your layout
        leftShoulderChart = view.findViewById(R.id.leftShoulderChart);
        rightShoulderChart = view.findViewById(R.id.rightShoulderChart);
        leftHipChart = view.findViewById(R.id.leftHipChart);
        rightHipChart = view.findViewById(R.id.rightHipChart);
        leftKneeChart = view.findViewById(R.id.leftKneeChart);
        rightKneeChart = view.findViewById(R.id.rightKneeChart);

        // Set up the charts
        setupChart(leftShoulderChart);
        setupChart(rightShoulderChart);
        setupChart(leftHipChart);
        setupChart(rightHipChart);
        setupChart(leftKneeChart);
        setupChart(rightKneeChart);

        // Update the charts with data
        try {
            updateChart(leftShoulderChart, testResult.getLeftShoulderData());
            updateChart(rightShoulderChart,  testResult.getRightShoulderData());
            updateChart(leftHipChart,  testResult.getLeftHipData());
            updateChart(rightHipChart,  testResult.getRightHipData());
            updateChart(leftKneeChart,  testResult.getLeftKneeData());
            updateChart(rightKneeChart,  testResult.getRightKneeData());
        }catch(Exception err){
            err.printStackTrace();
        }

        return view;
    }

    private void configChart(LineChart lineChart){

        Exercise exercise = sendEventListener.getExercise();
        if(exercise.getExercisies().get(0) instanceof SeatDownUp){
             sitUpGraph(lineChart, testResult.getLeftHipGraphData());
        }else{
            walkGraph(lineChart);
        }


    }

    private void walkGraph(LineChart lineChart){
        LineDataSet set1 = new LineDataSet(testResult.getLeftAngleGraphData(),"LEFT");
        LineDataSet set2 = new LineDataSet(testResult.getRightAngleGraphData(),"RIGHT");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        dataSets.add(set2);

        // left
        set1.setColor(Color.parseColor("#1bc8be"));
        set1.setCircleColor(Color.parseColor("#1bc8be"));
        set1.setValueTextColor(Color.parseColor("#1bc8be"));
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_main_color);
        set1.setFillDrawable(drawable);

        // right
        set2.setColor(Color.parseColor("#ffd25e"));
        set2.setCircleColor(Color.parseColor("#ffd25e"));
        set2.setValueTextColor(Color.parseColor("#ffd25e"));
        set2.setDrawValues(false);
        set2.setDrawCircles(false);
        set2.setDrawFilled(true);
        Drawable drawable2 = ContextCompat.getDrawable(getContext(), R.drawable.gradient_blue_2);
        set2.setFillDrawable(drawable2);


        LineData data = new LineData(dataSets);
        //set1.setDrawCircles();
        lineChart.getXAxis().setTextColor(Color.rgb(0, 0, 0));
        lineChart.getAxisLeft().setTextColor(Color.rgb(0, 0, 0));
        lineChart.getAxisRight().setTextColor(Color.rgb(0, 0, 0));

        lineChart.getLegend().setTextColor(Color.rgb(0, 0, 0));
        lineChart.getLegend().setTextSize(12f);

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
//        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setValueFormatter(new MillisecondsToSecondsFormatter());

        // set default zoom
        lineChart.zoom(1.5f, 1f, lineChart.getXChartMax()/2, lineChart.getYChartMax()/2);
        lineChart.setData(data);
    }

    private void sitUpGraph(LineChart lineChart,List<Entry> values){
        LineDataSet set1;
        set1 = new LineDataSet(values, "DataSet 1");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // black lines and points
        set1.setColor(Color.rgb(34, 206, 196));
        set1.setCircleColor(Color.rgb(34, 206, 196));
        set1.setValueTextColor(Color.rgb(34, 206, 196));
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        // draw filled with gradient
        set1.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_main_color);
        set1.setFillDrawable(drawable);

        //set1.setDrawCircles();
        lineChart.getXAxis().setTextColor(Color.rgb(217, 217, 217));
        lineChart.getAxisLeft().setTextColor(Color.rgb(217, 217, 217));
        lineChart.getAxisRight().setTextColor(Color.rgb(217, 217, 217));

        lineChart.getLegend().setTextColor(Color.rgb(217, 217, 217));

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.setData(data);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.home_btn: //홈으로 이동
                getActivity().finish();
                break;
//            case R.id.next_btn: // 다음 fragment
//                sendEventListener.nextFragment();
//                break;
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
        xAxis.setAxisMinimum(-200f);
        xAxis.setAxisMaximum(200f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceMin(0f);
        xAxis.setSpaceMax(0f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(-200f);
        yAxis.setAxisMaximum(200f);
        yAxis.setSpaceBottom(0f);
        yAxis.setSpaceTop(0f);

        chart.getAxisRight().setEnabled(false);

        xAxis.setDrawAxisLine(false);
        yAxis.setDrawAxisLine(false);



        // Custom x and y values for the vertical and horizontal lines
        float verticalXValue = 0f; // Example value
        float horizontalYValue = 0f; // Example value

        // Draw vertical line
//        LimitLine verticalLimitLine = new LimitLine(verticalXValue);
//        verticalLimitLine.setLineColor(Color.GRAY);
//        verticalLimitLine.setLineWidth(2f);
//        chart.getXAxis().addLimitLine(verticalLimitLine);

        // Draw horizontal line
//        LimitLine horizontalLimitLine = new LimitLine(horizontalYValue);
//        horizontalLimitLine.setLineColor(Color.GRAY);
//        horizontalLimitLine.setLineWidth(2f);
//        chart.getAxisLeft().addLimitLine(horizontalLimitLine);
    }

    private void updateChart(ScatterChart chart, ArrayList<ParcelablePointF> dataList) {
        ArrayList<Entry> entries = new ArrayList<>();

        // Iterate over the dataList and convert ParcelablePointF objects to Entry objects
        Iterator<ParcelablePointF> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            ParcelablePointF point = iterator.next();
            // entries.add(new Entry(point.x, point.y));
            // Change sign of x, y value because ml-kit data is reverse each of x, y coordination
            entries.add(new Entry(-point.x, -point.y));
        }

        Collections.sort(entries, new EntryXComparator());

        // Create a ScatterDataSet with the list of Entries
        ScatterDataSet dataSet = new ScatterDataSet(entries, null);
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        dataSet.setScatterShapeSize(10f);
        dataSet.setColor(Color.rgb(0, 176, 166));
        dataSet.setDrawValues(false);

        // Create a ScatterData object with the ScatterDataSet
        ScatterData scatterData = new ScatterData(dataSet);
        chart.setData(scatterData);

        chart.invalidate();
    }
}
