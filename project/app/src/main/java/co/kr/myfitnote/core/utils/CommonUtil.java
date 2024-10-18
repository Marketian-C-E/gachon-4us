package co.kr.myfitnote.core.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {

    public static String getTodayDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        // Get the current date and format it
        String todayDate = sdf.format(new Date());
        return todayDate;
    }

    public static String getCurrentYearMonthDay() {
        int year, month, day;

        // Check Android version for method selection
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Using java.time for Android API level 26 and higher
            LocalDate currentDate = LocalDate.now();
            year = currentDate.getYear();
            month = currentDate.getMonthValue();
            day = currentDate.getDayOfMonth();
        } else {
            // Using Calendar for older Android versions
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1; // Months are zero-based
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        return String.format("%d년 %d월 %d일", year, month, day);
    }
}
