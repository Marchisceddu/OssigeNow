package com.example.ossigenow;

import android.widget.CalendarView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class Calendario {
    private static CalendarView calendarView;
    private static Calendar calendar;
    public static void createCalendar(Context context, CalendarView c) {
        calendarView = c;
        calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String msg = "Selected date Day: " + i2 + " Month : " + (i1 + 1) + " Year " + i;
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
