package co.kr.myfitnote.cm.ui.measurement;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.kr.myfitnote.R;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.views.fragments.result.ExerciseSeriesData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 변화 결과
 */
public class ChangingResult extends Fragment {
    private String TAG = "ResultPage3";

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    Map<String, ArrayList<ExerciseSeriesData>> data;

    LineChart walkLineChart, upperLineChart;
    LineChart seatedUpLineChart;
    LineChart singleLegLineChart;

    // Recycler View
    RecyclerView pose_front_recycler_view, pose_side_recycler_view, rom_shoulder_left_recycler_view,
            rom_shoulder_right_recycler_view, rom_hip_left_recycler_view, rom_hip_right_recycler_view;

    Bundle bundle;

    private String clientId;

    // 외발서기 좌, 우 데이터 셋
    private List<ILineDataSet> singleLegDataSets = new ArrayList<>();
    private List<ILineDataSet> upperDataSets = new ArrayList<>();

    private HashMap<String, List<ILineDataSet>> dataSetsMap = new HashMap<String, List<ILineDataSet>>(){
        {
            put("외발서기 (좌)", singleLegDataSets);
            put("외발서기 (우)", singleLegDataSets);
            put("상지 근기능 (좌)", upperDataSets);
            put("상지 근기능 (우)", upperDataSets);
        }
    };
    private int ORNAGE_COLOR = Color.parseColor("#FFBC64");
    private int GREEN_COLOR = Color.parseColor("#018786");
    private static int SINGLE_LEG_MAX_VALUE = 30000;
    private static int SINGLE_LEG_MIN_VALUE = 0;


    // 차트 컬러 정보
    private HashMap<String, Integer> chartColorMap = new HashMap<String, Integer>(){
        {
            put("외발서기 (좌)", ORNAGE_COLOR);
            put("외발서기 (우)", GREEN_COLOR);
            put("상지 근기능 (좌)", ORNAGE_COLOR);
            put("상지 근기능 (우)", GREEN_COLOR);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null){
            Log.e(TAG, bundle.getString("client_id"));
            clientId = bundle.getString("client_id");
        }
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_page3, container, false);

        // Set up charts
        walkLineChart = view.findViewById(R.id.walk_chart); // 걷기 수
        seatedUpLineChart = view.findViewById(R.id.seatedup_chart); // 횟수
        singleLegLineChart = view.findViewById(R.id.singleleg_chart); // 초
        singleLegLineChart.getAxisLeft().setAxisMaximum(SINGLE_LEG_MAX_VALUE);
        singleLegLineChart.getAxisLeft().setAxisMinimum(SINGLE_LEG_MIN_VALUE);
        upperLineChart = view.findViewById(R.id.upper_chart);

        // Recycler views
        pose_front_recycler_view = view.findViewById(R.id.pose_front_recycler_view);
        pose_side_recycler_view = view.findViewById(R.id.pose_side_recycler_view);
        rom_shoulder_left_recycler_view = view.findViewById(R.id.rom_shoulder_left_recycler_view);
        rom_shoulder_right_recycler_view = view.findViewById(R.id.rom_shoulder_right_recycler_view);
        rom_hip_left_recycler_view = view.findViewById(R.id.rom_hip_left_recycler_view);
        rom_hip_right_recycler_view = view.findViewById(R.id.rom_hip_right_recycler_view);

        return view;
    }

    /**
     * 데이터를 기반으로 값만 가지고 있는 차트를 그림 (라인 차트)
     * @param data
     * @param key
     * @param chart
     */
    private void drawGraph(
            Map<String, ArrayList<ExerciseSeriesData>> data, String key, LineChart chart, boolean isMultiple) {
        Log.d(TAG, key + " " + isMultiple);
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < data.get(key).size(); i++) {
            String date = data.get(key).get(i).getDate(); // 2024-01-08
            // date to float
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            float timestamp = 0;
            try {
                // Chagne year, month, day to timestamp
                Date dateObj = sdf.parse(date);
                timestamp = dateObj.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            float value = Float.parseFloat(data.get(key).get(i).getValue().replaceAll("[^\\d.]", ""));
            entries.add(new Entry(timestamp, value));
        }

        LineData lineData;
        if (!isMultiple){
            LineDataSet lineDataSet = new LineDataSet(entries, key);
            lineDataSet.setCircleRadius(4f);
            lineDataSet.setLineWidth(4f);
            lineDataSet.setValueTextSize(12);
            lineDataSet.setColor(GREEN_COLOR);
            lineData = new LineData(lineDataSet);
        }else{
            // 좌/우가 구분되어 있는 경우 두 개의 데이터 셋을 생성해야 함
            LineDataSet lineDataSet = new LineDataSet(entries, key);
            lineDataSet.setCircleRadius(4f);
            lineDataSet.setLineWidth(4f);
            lineDataSet.setValueTextSize(12);
            lineDataSet.setColor(chartColorMap.get(key));
            // 해당 운동의 Dataset에 추가
            dataSetsMap.get(key).add(lineDataSet);
            lineData = new LineData(dataSetsMap.get(key));
        }

        drawChart(chart, lineData, data.get(key).size());
    }

    private void setRecyclerView(RecyclerView recyclerView, ArrayList<ExerciseSeriesData> data){
        RomResultCardAdapter adapter = new RomResultCardAdapter(data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void processData(Map<String, ArrayList<ExerciseSeriesData>> data){
        for (String key : data.keySet()){
            switch (key) {
                case "심폐 기능":
                    drawGraph(data, key, walkLineChart, false);
                    break;
                case "상지 근기능":
                    drawGraph(data, key, upperLineChart, false);
                    break;
                case "하지 근기능":
                    drawGraph(data, key, seatedUpLineChart, false);
                    break;
                case "외발서기 (좌)":
                case "외발서기 (우)":
                    drawGraph(data, key, singleLegLineChart, true);
                    break;
                case "정면":
                    setRecyclerView(pose_front_recycler_view, data.get(key));
                    break;
                case "측면":
                    setRecyclerView(pose_side_recycler_view, data.get(key));
                    break;
                case "어깨 외전 (우)":
                    setRecyclerView(rom_shoulder_left_recycler_view, data.get(key));
                    break;
                case "어깨 외전 (좌)":
                    setRecyclerView(rom_shoulder_right_recycler_view, data.get(key));
                    break;
                case "고관절 외전 (우)":
                    setRecyclerView(rom_hip_left_recycler_view, data.get(key));
                    break;
                case "고관절 외전 (좌)":
                    setRecyclerView(rom_hip_right_recycler_view, data.get(key));
                    break;
            }
        }
    }

    private void getData(){
        service.getExerciseSeriesData(clientId).enqueue(new Callback<Map<String, ArrayList<ExerciseSeriesData>>>() {
            @Override
            public void onResponse(Call<Map<String, ArrayList<ExerciseSeriesData>>> call, Response<Map<String, ArrayList<ExerciseSeriesData>>> response) {
                data = response.body();
                processData(data);
            }

            @Override
            public void onFailure(Call<Map<String, ArrayList<ExerciseSeriesData>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void drawChart(LineChart chart, LineData lineData, int labelCount){
        // Set the data to the line chart
        chart.setData(lineData);

        // Customize the chart as needed
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelRotationAngle(90);
        xAxis.setValueFormatter(new DateValueFormatter());
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(labelCount, true);
//        xAxis.setSpaceMax(4f);
//        xAxis.setSpaceMin(4f);
//        xAxis.setTextSize(12);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }


    private class DateValueFormatter extends ValueFormatter {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {
            Log.e(TAG, "Value is " + value);
            return dateFormat.format(new Date((long) value));
        }
    }

}