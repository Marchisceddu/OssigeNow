package com.example.ossigenow;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.util.Base64;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    // Passaggio dati tra activity
    public final static String USERS_PATH = "ARRAY_LIST";
    public final static String PERSON_PATH = "com.example.ossigenow.person";
    private Intent result; // Intento per cambiare activity

    // Array per salvare gli utenti registrati
    private ArrayList<Person> utentiRegistrati = new ArrayList();

    // Elementi UI
    private Button registrati, accedi;
    private EditText nomeUtente, password;

    // Oggetto per salvare l'utente registrato
    private Person newUtente, utenteLoggato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializza gli elementi UI
        accedi = findViewById(R.id.accedi);
        registrati = findViewById(R.id.registrati);
        nomeUtente = findViewById(R.id.nomeUtente);
        password = findViewById(R.id.password);

        // Prendo i dati dagli utenti registrati
        utentiRegistrati = recuperaUtentiRegistrati();

        // Prendo l'utente loggato
        utenteLoggato = recuperaUtenteLoggato();

        // Se l'utente è loggato lo mando direttamente alla home
        if(utenteLoggato != null) {
            result = new Intent(MainActivity.this, HomeActivity.class);
            result.putExtra(PERSON_PATH, utenteLoggato);
            startActivity(result);
            finish();
        }

        // Se vengono passati dati da un'altra activity li recupero e li aggiungo all'arraylist
        newUtente = (Person) getIntent().getSerializableExtra(RegisterActivity.PERSON_PATH); // Recupero l'oggetto
        if(newUtente!=null) utentiRegistrati.add(newUtente);
    }

    @Override
    protected void onDestroy() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // L'activity sta per essere distrutta, puoi salvare qui se non lo hai già fatto in onPause
        SharedPreferences sharedPreferences = getSharedPreferences("utentiRegistrati", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Converti utentiRegistrati in un array di byte utilizzando la serializzazione
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(utentiRegistrati);
            String datiUtentiRegistrati = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editor.putString("arrayUtenti", datiUtentiRegistrati);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
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

    private Person recuperaUtenteLoggato() {
        Person utenteLoggato = null;

        // Recupera l'utente dalle SharedPreferences
        SharedPreferences sharedPreferencesUtente = getSharedPreferences("Utente", Context.MODE_PRIVATE);

        // Recupera la rappresentazione di byte come stringa dalle SharedPreferences
        String datiUtente = sharedPreferencesUtente.getString("Utente", "");

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