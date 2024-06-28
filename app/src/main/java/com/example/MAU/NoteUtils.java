package com.example.MAU;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NoteUtils {

    public static String formatDate(Calendar calendar) {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static void setDate(Calendar calendar, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }
}
