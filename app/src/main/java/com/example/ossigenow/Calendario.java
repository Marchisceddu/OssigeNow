package com.example.ossigenow;

import android.graphics.Color;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Calendario {
    private static CalendarView calendarView;
    private static Calendar calendar;
    public static void createCalendar(Context context, CalendarView c) {
        calendarView = c;
        calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);

        // Imposta l'icona degli impegni
        VectorDrawableCompat drawable = VectorDrawableCompat.create(context.getResources(), R.drawable.add_icon, null);
        drawable.setTint(ContextCompat.getColor(context, R.color.red));

        // Aggiunge deglio impegni in 2 giorni di febbraio 2024
        List<EventDay> eventDays = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(2024, Calendar.FEBRUARY, i);

            eventDays.add(new EventDay(calendar1, drawable));

            System.out.println(eventDays.get(i-1).getCalendar().getTime());
        }
        calendarView.setEvents(eventDays);

        // Listener per aggiungere un impegno
        calendarView.setOnDayClickListener(eventDay -> {
            eventDays.add(new EventDay(eventDay.getCalendar(), drawable));
            calendarView.setEvents(eventDays);
        });
    }
}
