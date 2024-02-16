package com.example.ossigenow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Base64;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Calendario extends AppCompatActivity {
    private static CalendarView calendarView;
    private static Calendar calendar;

    public static void createCalendar(Context context, CalendarView c, ArrayList<Commit> commits, boolean isGroup, Person loggedUser) {
        calendarView = c;
        calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);

        if(!isGroup){

            // Imposta l'icona degli impegni
            VectorDrawableCompat drawable = VectorDrawableCompat.create(context.getResources(), R.drawable.commit, null);

            // Aggiunge gli impegni al calendario
            List<EventDay> eventDays = new ArrayList<>();
            for (int i = commits.size(); i > 0; i--) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(commits.get(i-1).getData().get(Calendar.YEAR), commits.get(i-1).getData().get(Calendar.MONTH), commits.get(i-1).getData().get(Calendar.DAY_OF_MONTH));

                eventDays.add(new EventDay(calendar1, drawable));
            }
            calendarView.setEvents(eventDays);

            // Listener per aggiungere un impegno
            calendarView.setOnDayClickListener(eventDay -> {
                //se clicco su un giorno con impegni mi apre la lista degli impegni
                Intent result;
                result = new Intent(context, CommitListActivity.class);
                result.putExtra("data", eventDay.getCalendar());
                context.startActivity(result);
            });

        }else {

            //recupero l'utente loggato attualmente
            Person user = loggedUser;
            ArrayList<Commit> loggedUserCommits = user.getImpegni();

            // Imposta le icone degli impegni dell'utente loggato e degli altri utenti del gruppo
            VectorDrawableCompat userCommit = VectorDrawableCompat.create(context.getResources(), R.drawable.commit, null);
            VectorDrawableCompat groupCommit = VectorDrawableCompat.create(context.getResources(), R.drawable.group_commit, null);


            // Aggiunge gli impegni al calendario
            List<EventDay> eventDays = new ArrayList<>();
            for (int i = commits.size(); i > 0; i--) {

                boolean flag = false;
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(commits.get(i-1).getData().get(Calendar.YEAR), commits.get(i-1).getData().get(Calendar.MONTH), commits.get(i-1).getData().get(Calendar.DAY_OF_MONTH));


                for(int j=loggedUserCommits.size(); j>0; j--){
                    if(commits.get(i-1).getData().equals(loggedUserCommits.get(j-1).getData()) && Objects.equals(commits.get(i - 1).getNome(), loggedUserCommits.get(j - 1).getNome()) && commits.get(i-1).getOraInizio().equals(loggedUserCommits.get(j-1).getOraInizio()) && commits.get(i-1).getOraFine().equals(loggedUserCommits.get(j-1).getOraFine())){
                        eventDays.add(new EventDay(calendar1,userCommit));
                        flag = true;
                    }
                }

                if(!flag) {
                    eventDays.add(new EventDay(calendar1, groupCommit));
                }
            }
            calendarView.setEvents(eventDays);
        }
    }




}
