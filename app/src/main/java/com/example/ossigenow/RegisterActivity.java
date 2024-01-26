package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    // Passaggio dati tra activity
    public final static String PERSON_PATH = "com.example.ossigenow.person";
    private Intent result; // Intento per cambiare activity

    // Array per salvare gli utenti registrati
    private ArrayList<Person> utentiRegistrati = new ArrayList();

    // Elementi UI
    private Button registrati, pulisci;
    private EditText nomeUtente, nome, cognome, data, password;
    private ImageButton back;

    // Oggetto per salvare l'utente registrato
    private Person utente;

    // Variabili per il calendario
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializza gli elementi UI
        registrati = findViewById(R.id.conferma);
        pulisci = findViewById(R.id.pulisci);
        nomeUtente = findViewById(R.id.nomeUtente);
        nome = findViewById(R.id.nome);
        cognome = findViewById(R.id.cognome);
        data = findViewById(R.id.data);
        password = findViewById(R.id.password);
        back = findViewById(R.id.indietro);

        // Prendo i dati degli utenti registrati dall'activity precedente
        utentiRegistrati = (ArrayList<Person>) getIntent().getSerializableExtra(MainActivity.USERS_PATH);

        // Impedisci l'apertura della tastiera
        data.setRawInputType(android.text.InputType.TYPE_NULL);
        data.setTextIsSelectable(true);

        // Modifico il calendario
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

        // Mostra il DatePickerFragment in caso di focus attivo
        data.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showDialog(); });
        // Mostra il DatePickerFragment in caso di click
        data.setOnClickListener(v -> showDialog());

        // Torna indietro all'activity precedente
        back.setOnClickListener(v -> {
            result = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(result);
            finish();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // Funzione che mostra il DatePickerFragment tramite il DialogFragment
    private void showDialog() {
        DialogFragment dialogFragment = DatePickerFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    // Questa funzione viene eseguita in caso venga premuto il tasto per confermare
    protected void doPositiveClick(Calendar calendar) {
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
            age--;
        } else
        if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < calendar
                .get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        data.setText(simpleDateFormat.format(calendar.getTime()));
    }

    // Questa funzione viene eseguita in caso venga premuto il tasto per annullare
    protected void doNegativeClick() {}
}