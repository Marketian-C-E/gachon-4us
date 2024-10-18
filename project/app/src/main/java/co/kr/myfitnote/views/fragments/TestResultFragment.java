package co.kr.myfitnote.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.SendEventListener;
import co.kr.myfitnote.model.Exercise;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.SeatDownUp;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.views.HomeActivity;
import co.kr.myfitnote.views.parcel.ParcelablePointF;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TestResultFragment extends Fragment implements View.OnClickListener {
    private SendEventListener sendEventListener;
    private View view;
    private TestResult testResult;

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    SharedPreferences pref;
    String userData;
    User user;

    boolean useSecond = false;

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

    public TestResultFragment(){}

    public TestResultFragment(boolean useSecond){
        this.useSecond = useSecond;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userData = pref.getString("userData", "");
        user = gson.fromJson(userData, User.class);

        try {
            sendEventListener = (SendEventListener) context;
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
        TextView gradeView = view.findViewById(R.id.grade_view);
        TextView timerView = view.findViewById(R.id.timer_view);
//        TextView tvResultLabel = view.findViewById(R.id.tv_result_label);

        testResult = sendEventListener.getTestResult();

        if (useSecond) {
            countView.setText(String.format("%.2f", testResult.getSecond() / 1000.0) + "초");
//            tvResultLabel.setText("시간");
        }else{
            countView.setText(testResult.getCount() + "회");
            timerView.setText(testResult.getSecond() + "초");

        }
        gradeView.setText(testResult.getGrade()+"");
        if (testResult.getType().equals("WALK")){
            String strGrade = "미달";
            if (testResult.getGrade() == 1){
                strGrade = "상";
            }else if (testResult.getGrade() == 2){
                strGrade = "적정";
            }
            gradeView.setText(strGrade);
        }

        sendLogData();

//        Toast toast = Toast.makeText(getContext(), "수고하셨습니다.검사가 종료되었습니다.", Toast.LENGTH_LONG);
//        ViewGroup group = (ViewGroup)toast.getView();
//        TextView msgTextView = (TextView)group.getChildAt(0);
//        msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
//        toast.show();

        configChart(view.findViewById(R.id.line_chart));

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
             sitUpGraph(lineChart,testResult.getLeftHipGraphData());

        }else{
            walkGraph(lineChart);
        }


    }

    private void walkGraph(LineChart lineChart){
        Log.e("poapo", "left size: "+ testResult.getLeftAngleGraphData().size());
        Log.e("poapo", "right size: "+ testResult.getRightAngleGraphData().size());
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
        set2.setColor(Color.parseColor("#00a3ff"));
        set2.setCircleColor(Color.parseColor("#00a3ff"));
        set2.setValueTextColor(Color.parseColor("#00a3ff"));
        set2.setDrawValues(false);
        set2.setDrawCircles(false);
        set2.setDrawFilled(true);
        Drawable drawable2 = ContextCompat.getDrawable(getContext(), R.drawable.gradient_blue_2);
        set2.setFillDrawable(drawable2);


        LineData data = new LineData(dataSets);
        //set1.setDrawCircles();
        lineChart.getXAxis().setTextColor(Color.rgb(217, 217, 217));
        lineChart.getAxisLeft().setTextColor(Color.rgb(217, 217, 217));
        lineChart.getAxisRight().setTextColor(Color.rgb(217, 217, 217));

        lineChart.getLegend().setTextColor(Color.rgb(217, 217, 217));

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
//        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
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
                Intent intent = new Intent(getContext(), HomeActivity.class);
                getActivity().startActivity(intent);
                break;
//            case R.id.next_btn: // 다음 fragment
//                sendEventListener.nextFragment();
//                break;
        }

    }

    private void sendLogData(){
        if (testResult.getLogData() != null){
            // if there is log data, send to server for stroing in DB
            new Thread(() -> {
                try {
                    Call<RetrofitStatus> request = service.createExerciseResult(user.getToken(),
                            new Gson().toJson(testResult, TestResult.class));
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
                }catch(Exception err){
                    err.printStackTrace();
                }
            }).start();
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
            entries.add(new Entry(point.x, point.y));
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
