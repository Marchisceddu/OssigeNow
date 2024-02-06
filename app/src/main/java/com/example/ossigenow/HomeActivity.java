package com.example.ossigenow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    // Utente loggato
    private Person user = null;
    private boolean isLogout = false;

    // Elementi UI
    private ImageView navHome, navInvite, navCalendar, navBooking, navProfile;
    private int home, invite, calendar, booking, profile;
    private LinearLayout containerLayout;
    private LayoutInflater layoutInflater;
    private LinearLayout creaGruppo;

    // Intento per cambiare activity
    private Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ripristina la sessione
        ripristinaSessione();

        // Inizializza gli elementi UI
        inizializzaUI();

        // Gestisce la navbar
        navbar(new int[]{home, invite, calendar, booking, profile}, new ImageView[]{navHome, navInvite, navCalendar, navBooking, navProfile});
    }

    @Override
    protected void onDestroy() {
        if (!isLogout) {
            // Salvo l'ultimo utente
            SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

            SharedPreferences.Editor editorUtente = sharedPreferencesUtente.edit();

            // Converti l'utente attuale in un array di byte utilizzando la serializzazione
            ByteArrayOutputStream byteArrayOutputStreamUtente = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtente)) {
                objectOutputStream.writeObject(user);
                String datiUtente = Base64.encodeToString(byteArrayOutputStreamUtente.toByteArray(), Base64.DEFAULT);

                // Salva la rappresentazione di byte come stringa nelle SharedPreferences
                editorUtente.putString("User", datiUtente);
                editorUtente.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (!isLogout) {
            // Salvo l'ultimo utente
            SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

            SharedPreferences.Editor editorUtente = sharedPreferencesUtente.edit();

            // Converti l'utente attuale in un array di byte utilizzando la serializzazione
            ByteArrayOutputStream byteArrayOutputStreamUtente = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtente)) {
                objectOutputStream.writeObject(user);
                String datiUtente = Base64.encodeToString(byteArrayOutputStreamUtente.toByteArray(), Base64.DEFAULT);

                // Salva la rappresentazione di byte come stringa nelle SharedPreferences
                editorUtente.putString("User", datiUtente);
                editorUtente.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onStop();
    }

    private void ripristinaSessione() {
        // Recupera i dati dell'activity precedente
        user = (Person) getIntent().getSerializableExtra(MainActivity.PERSON_PATH);
    }

    private void inizializzaUI() {
        navHome = findViewById(R.id.navHome);
        navInvite = findViewById(R.id.navInvite);
        navCalendar = findViewById(R.id.navCalendar);
        navBooking = findViewById(R.id.navBooking);
        navProfile = findViewById(R.id.navProfile);
        home = R.layout.home;
        invite = R.layout.invite;
        calendar = R.layout.calendar;
        booking = R.layout.booking;
        profile = R.layout.profile;
    }

    private void confListener(int layout) {
        if (layout == R.layout.home) {
            creaGruppo = findViewById(R.id.creaGruppo);

            creaGruppo.setOnClickListener(v -> {
                System.out.println("Crea gruppo");
                result = new Intent(HomeActivity.this, NewGroupActivity.class);
                result.putExtra(MainActivity.PERSON_PATH, user);
                startActivity(result);
            });
        }
    }

    // Metodi per gestire la navbar
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void setNavItem(ImageView[] itemView, int position, boolean selected) {
//        int padding = selected ?  dpToPx(4) : dpToPx(10);
//        itemView[position].setPadding(padding, padding, padding, padding);

        itemView[position].setBackgroundResource(selected ? R.color.navbar_icon : R.color.navbar_icon_selected);
        if (selected) {
            if(position > 0) itemView[position-1].setBackgroundResource(R.drawable.background_nav_rigth);
            if(position < itemView.length-1) itemView[position+1].setBackgroundResource(R.drawable.background_nav_left);
        }

        itemView[position].setSelected(selected);
    }
    private void navbar(int[] layouts, ImageView[] navItems) {
        // Inizializza il layout
        containerLayout = findViewById(R.id.container);
        layoutInflater = LayoutInflater.from(this);

        // Di default è selezionata la schemata home
        setNavItem(navItems, 0, true);
        containerLayout.addView(layoutInflater.inflate(home, containerLayout, false));
        confListener(home);

        // Imposta il listener per ogni elemento di navigazione
        for (int i = 0; i < layouts.length; i++) {
            int index = i;
            int selectedLayout = layouts[i];
            ImageView navItem = navItems[i];

            navItem.setOnClickListener(v -> {
                // Imposta tutti gli elementi di navigazione come non selezionati
                for (int j = 0; j < navItems.length; j++) {
                    setNavItem(navItems, j,false);
                }
                // Imposta l'elemento di navigazione corrente come selezionato
                setNavItem(navItems, index,true);

                // Imposta il layout corrente
                containerLayout.removeAllViews(); // Rimuove il layout attuale, se presente
                containerLayout.addView(layoutInflater.inflate(selectedLayout, containerLayout, false));
                confListener(selectedLayout);

                // Se il layout è profile imposta gravity center
                if (selectedLayout == R.layout.profile)
                    containerLayout.setGravity(Gravity.CENTER);
                else
                    containerLayout.setGravity(Gravity.START);
            });
        }
    }
}