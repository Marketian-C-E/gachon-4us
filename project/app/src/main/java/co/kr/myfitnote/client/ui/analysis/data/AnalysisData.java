package co.kr.myfitnote.client.ui.analysis.data;

import java.util.ArrayList;
import java.util.Date;

public class AnalysisData {
    private ArrayList<PrescriptionHistory> history;
    private int day;

    public int getDay() {
        return day;
    }

    public ArrayList<PrescriptionHistory> getHistory() {
        return history;
    }
}
