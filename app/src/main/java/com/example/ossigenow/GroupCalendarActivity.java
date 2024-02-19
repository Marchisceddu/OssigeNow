package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;

import com.applandeo.materialcalendarview.CalendarView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class GroupCalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ImageButton back;
    private Group group;
    private Person loggedUser;
    ArrayList<Person> componenti = new ArrayList<>();

    ArrayList<Person> realComponenti = new ArrayList<>();
    ArrayList<Commit> commits = new ArrayList<>();

    Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        back = findViewById(R.id.indietro);
        calendarView = findViewById(R.id.calendarView);

        restoreUser();

        realComponenti = recuperaUtentiRegistrati();
        group = (Group) getIntent().getSerializableExtra(GroupActivity.GROUP_PATH);
        componenti = (ArrayList<Person>) group.getPartecipanti();
        updatePartecipanti(realComponenti);


        for (Person p : componenti) {
            commits.addAll(p.getImpegni());
        }

        Calendario.createCalendar(this, calendarView, commits, true, loggedUser);


        back.setOnClickListener(v -> {
            result = new Intent(GroupCalendarActivity.this, GroupActivity.class);
            result.putExtra(HomeActivity.GROUP_PATH, group);
            result.putExtra(HomeActivity.PERSON_PATH, loggedUser);
            startActivity(result);
            finish();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(GroupCalendarActivity.this, GroupActivity.class);
                result.putExtra(HomeActivity.GROUP_PATH, group);
                result.putExtra(HomeActivity.PERSON_PATH, loggedUser);
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }


    public void restoreUser() {
        // Recupera l'utente dalle SharedPreferences
        SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

        String datiUtente = sharedPreferencesUtente.getString("User", "");

        if (!datiUtente.isEmpty()) {
            byte[] datiUtenteBytes = Base64.decode(datiUtente, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiUtenteBytes))) {
                loggedUser = (Person) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }





    private ArrayList<Person> recuperaUtentiRegistrati() {
        // Lista per memorizzare gli utenti registrati
        ArrayList<Person> utentiRegistrati = null;

        // Ottieni le SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("utentiRegistrati", Context.MODE_PRIVATE);

        // Recupera la rappresentazione di byte come stringa dalle SharedPreferences
        String datiArrayString = sharedPreferences.getString("arrayUtenti", "");

        if (!datiArrayString.isEmpty()) {
            // Decodifica la stringa Base64 e converte l'array di byte in oggetto
            byte[] datiArrayBytes = Base64.decode(datiArrayString, Base64.DEFAULT);

            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiArrayBytes))) {
                utentiRegistrati = (ArrayList<Person>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (utentiRegistrati == null) utentiRegistrati = new ArrayList<>();
        return utentiRegistrati;
    }


    public void updatePartecipanti(ArrayList<Person> existing_user){

        //riaggiorno tutti gli utenti presenti nel gruppo con quelli presenti nella shared preferences aggiornata
        for (Person actual: componenti) {
            for (Person real: existing_user) {

                if(real.getNome().compareTo(actual.getNome())==0 &&
                        real.getCognome().compareTo(actual.getCognome())==0 &&
                        real.getDataNascita().compareTo(actual.getDataNascita())==0 &&
                        real.getPassword().compareTo(actual.getPassword())==0)
                {
                    ArrayList<Commit> realImpegni = real.getImpegni();
                    ArrayList<Commit> actualImpegni = actual.getImpegni();

                    for (Commit c: realImpegni) {
                        if(!actualImpegni.contains(c)){
                            actualImpegni.add(c);
                        }
                    }

                    actualImpegni.removeIf(commit -> !realImpegni.contains(commit));
                }
            }
        }
    }


}