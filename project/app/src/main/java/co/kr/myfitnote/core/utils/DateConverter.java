package co.kr.myfitnote.core.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class DateConverter {

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            // Parse the string and convert it to a Date object
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception, for example, by returning null or throwing a custom exception
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getAgeFromBirthYear(String birthYear) {
        int year = Integer.parseInt(birthYear);
        LocalDate birthdate = LocalDate.of(year, 1, 1); // assuming birthdate is January 1st
        LocalDate now = LocalDate.now();
        return Period.between(birthdate, now).getYears();
    }
}
