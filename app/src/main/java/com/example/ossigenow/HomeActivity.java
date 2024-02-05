package com.example.ossigenow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
    private LinearLayout home, invite, calendar, booking, profile;

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
        navbar(new LinearLayout[]{home, invite, calendar, booking, profile}, new ImageView[]{navHome, navInvite, navCalendar, navBooking, navProfile});
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
        home = findViewById(R.id.home);
        invite = findViewById(R.id.invite);
        calendar = findViewById(R.id.calendar);
        booking = findViewById(R.id.booking);
        profile = findViewById(R.id.profile);
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
    private void navbar(LinearLayout[] layouts, ImageView[] navItems) {
        setNavItem(navItems, 0, true); // di default è selezionata la schemata home

        // Imposta il listener per ogni elemento di navigazione
        for (int i = 0; i < layouts.length; i++) {
            int index = i;
            LinearLayout selectedLayout = layouts[i];
            ImageView navItem = navItems[i];

            navItem.setOnClickListener(v -> {
                // Imposta tutti gli elementi di navigazione come non selezionati
                for (int j = 0; j < navItems.length; j++) {
                    setNavItem(navItems, j,false);
                }
                // Imposta l'elemento di navigazione corrente come selezionato
                setNavItem(navItems, index,true);

                // Imposta tutti i layout associati come invisibili
                for (LinearLayout layout : layouts) {
                    layout.setVisibility(View.GONE);
                }
                // Imposta il layout associato all'elemento di navigazione corrente come visibile
                selectedLayout.setVisibility(View.VISIBLE);
            });
        }
    }
}