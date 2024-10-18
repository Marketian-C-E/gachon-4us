package co.kr.myfitnote.core.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MillisecondsToSecondsFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        // Convert milliseconds to seconds
        float seconds = value / 1000.0f;
        return String.format("%.2f", seconds) + "s";
    }
}