package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

    // Variabili per il calendario
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializza gli elementi UI
        registrati = findViewById(R.id.conferma);
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

        // Conferma i dati inseriti e passa alla schermata home
        registrati.setOnClickListener(v -> {
            if (checkInput()) { // Se gli input sono validi
                Person user = updateUsers();

                // Creo l'intento di passare da MainActivity a ResultActivity
                result = new Intent(RegisterActivity.this, MainActivity.class);
                result.putExtra(PERSON_PATH, user); // Passo dati all'intento
                confirmMessage();
                startActivity(result);
                finish();
            }
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

    private boolean checkInput() {
        int errors = 0;

        // Controllo che i campi non siano vuoti
        if (nomeUtente.getText() == null || nomeUtente.getText().length() == 0) {
            errors++;
            nomeUtente.setError("Inserire il nome utente");
        } else
            nomeUtente.setError(null);
        if (nome.getText() == null || nome.getText().length() == 0) {
            errors++;
            nome.setError("Inserire il nome");
        } else
            nome.setError(null);

        if (cognome.getText() == null || cognome.getText().length() == 0) {
            errors++;
            cognome.setError("Inserire il cognome");
        } else
            cognome.setError(null);

        if (data.getText() == null || data.getText().length() == 0) {
            errors++;
            data.setError("Inserire la data");
        } else
            data.setError(null);

        if (password.getText() == null || password.getText().length() == 0) {
            errors++;
            password.setError("Inserire la password");
        } else
            password.setError(null);

        // Controllo che non ci siano utenti uguali
        if (!utentiRegistrati.isEmpty()) {
            for (Person user : utentiRegistrati) {
                if (((nomeUtente.getText().toString().compareTo(user.getCognome())) == 0)) {
                    errors++;
                    errorMessage();
                }
            }
        }

        return errors == 0; // True se non vi sono errori, altrimenti false
    }

    private Person updateUsers(){
        Person user = new Person();

        user.setNomeUtente(nomeUtente.getText().toString());
        user.setNome(nome.getText().toString());
        user.setCognome(cognome.getText().toString());
        user.setDataNascita(data.getText().toString());
        user.setPassword(password.getText().toString());

        return user;
    }

    private void confirmMessage(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_register_confirmed,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void errorMessage() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_error_account_already_exists,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}