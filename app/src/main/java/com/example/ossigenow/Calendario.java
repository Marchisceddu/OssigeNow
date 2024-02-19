package com.example.ossigenow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Base64;
import android.widget.Toast;
import android.content.Context;
import androidx.lifecycle.Lifecycle;
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

    private static ArrayList<Commit>commits = new ArrayList<>();

    private static ArrayList<Commit> loggedUserCommits = new ArrayList<>();

    // Aggiunge gli impegni al calendario
    private static List<EventDay> eventDays = new ArrayList<>();

    private static Person user = null;

    public static void createCalendar(Context context, CalendarView c, ArrayList<Commit> impegni, boolean isGroup, Person loggedUser) {
        calendarView = c;
        calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);

        user = loggedUser;
        commits = impegni;

        // Pulisce gli impegni quando si crea la funzione
        eventDays. clear();

        if(!isGroup){

            // Imposta l'icona degli impegni
            VectorDrawableCompat drawable = VectorDrawableCompat.create(context.getResources(), R.drawable.commit, null);

            for (int i = commits.size(); i > 0; i--) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(commits.get(i-1).getData().get(Calendar.YEAR), commits.get(i-1).getData().get(Calendar.MONTH), commits.get(i-1).getData().get(Calendar.DAY_OF_MONTH));

                eventDays.add(new EventDay(calendar1, drawable));
            }
            calendarView.setEvents(eventDays);

            // Listener per i giorni con impegni
            calendarView.setOnDayClickListener(eventDay -> {
                //se clicco su un giorno con impegni mi apre la lista degli impegni
                Intent result;
                result = new Intent(context, CommitListActivity.class);
                result.putExtra("data", eventDay.getCalendar());
                context.startActivity(result);
            });

        }else {

            //recupero l'utente loggato attualmente
            loggedUserCommits = user.getImpegni();

            // Imposta le icone degli impegni dell'utente loggato e degli altri utenti del gruppo
            VectorDrawableCompat userCommit = VectorDrawableCompat.create(context.getResources(), R.drawable.commit, null);
            VectorDrawableCompat groupCommit = VectorDrawableCompat.create(context.getResources(), R.drawable.group_commit, null);

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

            // Listener per i giorni con impegni
            calendarView.setOnDayClickListener(eventDay -> {
                //se clicco su un giorno con impegni mi apre la lista degli impegni
                Intent result;
                result = new Intent(context, CommitListActivity.class);
                result.putExtra("data", eventDay.getCalendar());
                result.putExtra("isGroup", true);
                result.putExtra("impegni",commits);
                context.startActivity(result);
            });
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("Impegno già presente");
        user = recuperaUtenteLoggato();
        ArrayList<Commit> newUserCommits = user.getImpegni();



        for(Commit commit: loggedUserCommits) {
            for (Commit newCommit : newUserCommits) {
                if (commit.getData().equals(newCommit.getData()) && commit.getNome().equals(newCommit.getNome()) && commit.getOraInizio().equals(newCommit.getOraInizio()) && commit.getOraFine().equals(newCommit.getOraFine())) {
                    System.out.println("Impegno già presente");
                    eventDays.remove(commit);
                }
            }
        }
    }


    private Person recuperaUtenteLoggato() {
        Person utenteLoggato = null;

        // Recupera l'utente dalle SharedPreferences
        SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

        // Recupera la rappresentazione di byte come stringa dalle SharedPreferences
        String datiUtente = sharedPreferencesUtente.getString("User", "");

        // Converte la stringa in un array di byte
        byte[] utenteBytes = Base64.decode(datiUtente, Base64.DEFAULT);

        // Converte l'array di byte in un oggetto Persona
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(utenteBytes))) {
            utenteLoggato = (Person) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return utenteLoggato;
    }
}
