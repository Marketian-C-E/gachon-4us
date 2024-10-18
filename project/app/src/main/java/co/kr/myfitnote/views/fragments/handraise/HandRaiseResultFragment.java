package co.kr.myfitnote.views.fragments.handraise;

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
import co.kr.myfitnote.core.ui.GradeView;
import co.kr.myfitnote.measurement.MeasurementEventListener;
import co.kr.myfitnote.model.HandRaise;
import co.kr.myfitnote.model.RetrofitStatus;
import co.kr.myfitnote.model.TestResult;
import co.kr.myfitnote.model.User;
import co.kr.myfitnote.views.HomeActivity;
import co.kr.myfitnote.views.parcel.ParcelablePointF;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HandRaiseResultFragment extends Fragment implements View.OnClickListener {
    private MeasurementEventListener sendEventListener;
    private View view;
    private TestResult testResult;
    private TextView testResultHeaderTV;
    private TextView measurementItemTV;
    private LineChart chart;

    private GradeView gradeUIView;
    private TextView gradeView;

    public HandRaiseResultFragment(){}

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
        view = inflater.inflate(R.layout.fragment_hand_raise_result, container, false);

        view.findViewById(R.id.home_btn).setOnClickListener(this);

        TextView countView = view.findViewById(R.id.count_view);
//        TextView gradeView = view.findViewById(R.id.grade_view);
        TextView timerView = view.findViewById(R.id.timer_view);

        gradeView = view.findViewById(R.id.grade_view);
        gradeUIView = view.findViewById(R.id.grade_ui_view);

        testResultHeaderTV = view.findViewById(R.id.test_result_header_tv);
        measurementItemTV = view.findViewById(R.id.measurement_item_tv);
        chart = view.findViewById(R.id.line_chart);
        lineGraph(chart, sendEventListener.getTestResult().getLeftAngleGraphData());

        testResult = sendEventListener.getTestResult();

        measurementItemTV.setText("상지 근기능");
        // 클라이언트 이름 측정
        testResultHeaderTV.setText(sendEventListener.getClient().getName());

        countView.setText(testResult.getCount() + "회");
        timerView.setText(testResult.getSecond() + "초");
        gradeView.setText(testResult.getGradeResultLabel());
        gradeUIView.setGrade(testResult.getGrade());

        try {
            updateChart(view.findViewById(R.id.handChart), testResult.getWristData());
        }catch(Exception err){
            err.printStackTrace();
        }

        return view;
    }

    public void lineGraph(LineChart lineChart, List<Entry> values){
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

    private void updateChart(ScatterChart chart, ArrayList<ParcelablePointF> dataList) {
        ArrayList<Entry> entries = new ArrayList<>();

        chart.getDescription().setEnabled(false);
        chart.setNoDataText("");
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setAxisMinimum(-200f);
        xAxis.setAxisMaximum(200f);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(-200f);
        yAxis.setAxisMaximum(200f);
        yAxis.setDrawGridLines(false);

        chart.getAxisRight().setEnabled(false);

        xAxis.setDrawAxisLine(false);
        yAxis.setDrawAxisLine(false);

        // Iterate over the dataList and convert ParcelablePointF objects to Entry objects
        Iterator<ParcelablePointF> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            ParcelablePointF point = iterator.next();
            // entries.add(new Entry(point.x, point.y));
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

}
