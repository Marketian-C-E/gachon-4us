package co.kr.myfitnote.client.ui.analysis;

import static co.kr.myfitnote.core.utils.DateConverter.convertStringToDate;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.kr.myfitnote.R;
import co.kr.myfitnote.account.data.model.Client;
import co.kr.myfitnote.client.ui.analysis.data.AnalysisData;
import co.kr.myfitnote.client.ui.analysis.data.PrescriptionHistory;
import co.kr.myfitnote.core.networks.RetrofitClient;
import co.kr.myfitnote.core.networks.RetrofitService;
import co.kr.myfitnote.core.utils.PreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 고객 운동 통계
 */
public class AnalysisFragment extends Fragment {
    private final static String TAG = "AnalysisFragment";
    private TextView selected_date;
    private TextView header_sub_title;

    private CalendarView calendarView;

    private int processDay = 0; // 운동 일차

    Retrofit retrofit = RetrofitClient.getRetrofit();
    RetrofitService service = retrofit.create(RetrofitService.class);

    private Client client;


    public AnalysisFragment() {
        // Required empty public constructor
    }

    public static AnalysisFragment newInstance(String param1, String param2) {
        AnalysisFragment fragment = new AnalysisFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        client = PreferencesManager.getInstance(getContext()).getClient();
        selected_date = view.findViewById(R.id.selected_date);
        header_sub_title = view.findViewById(R.id.header_sub_title);

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                int year = calendar.getYear();
                int month = calendar.getMonth();
                selected_date.setText(year + "년 " + month + "월");
            }
        });

        Date today = new Date();
        int todayYear = today.getYear() + 1900;
        int todayMonth = today.getMonth() + 1;
        Calendar calendar = new Calendar();
        calendar.setYear(todayYear);
        calendar.setMonth(todayMonth);
        calendar.setDay(today.getDate());
        selected_date.setText(todayYear+ "년 " + todayMonth+ "월");

        // Create a Scheme object for the event
        Calendar.Scheme scheme = new Calendar.Scheme();
        scheme.setShcemeColor(R.color.GREEN_1); // Set event color
        scheme.setScheme("Event"); // Set event text

        // Add the Scheme to the Calendar object
        calendar.addScheme(scheme);

        // Add the Calendar object to the CalendarView
        calendarView.addSchemeDate(calendar);

        getUserPrescriptionAnalysis();
        return view;
    }

    /**
     * 달력에 스키미 이벤트 추가
     */
    void setCalendarEvents(ArrayList<PrescriptionHistory> eventDates){
        if (eventDates == null){
            return;
        }

        for (PrescriptionHistory his : eventDates){
            Date dateObj = convertStringToDate(his.getDate());
            Calendar calendar = new Calendar();
            calendar.setYear(dateObj.getYear() + 1900);
            calendar.setMonth(dateObj.getMonth() + 1);
            calendar.setDay(dateObj.getDate());
            calendarView.addSchemeDate(calendar);
        }
    }

    void getUserPrescriptionAnalysis(){
        service.getPrescriptionAnalysis(client.getPhone()).enqueue(new Callback<AnalysisData>() {
            @Override
            public void onResponse(Call<AnalysisData> call, Response<AnalysisData> response) {
                if (response.isSuccessful()) {
                    AnalysisData result = response.body();
                    processDay = result.getDay();
                    setCalendarEvents(result.getHistory());
                    header_sub_title.setText("현재 고객님은 운동 " + processDay + "일차입니다.");
                } else {
                    Log.e(TAG, response.message());
                }
            }
            @Override
            public void onFailure(Call<AnalysisData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}