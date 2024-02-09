package com.example.ossigenow;

import com.example.ossigenow.CustomCalendarView;
import android.content.Context;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Calendario2 {
    private static CustomCalendarView calendarView;
    private static Calendar calendar;
    public static void createCalendar(Context context, CustomCalendarView c) {
        calendarView = c;
        calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);

        calendarView.setSpecialDay(2024, 1, 1);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String msg = "Selected date Day: " + i2 + " Month : " + (i1 + 1) + " Year " + i;
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
