package co.kr.myfitnote.core.utils;

import android.graphics.Color;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import co.kr.myfitnote.views.parcel.ParcelablePointF;

public class ChartManager {

    public void setupChart(ScatterChart chart) {
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

    public void updateChart(ScatterChart chart, ArrayList<ParcelablePointF> dataList) {
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
}
