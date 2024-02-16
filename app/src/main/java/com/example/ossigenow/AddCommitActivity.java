package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddCommitActivity extends AppCompatActivity {
    protected LayoutInflater inflater;
    protected TextView nomeImpegno, oraInizio, oraFine;
    protected Button inizio, fine, conferma;

    private ImageButton back;
    protected DatePicker date;

    // Intento per cambiare activity
    private Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commit);

        nomeImpegno = findViewById(R.id.nomeImpegno);
        inizio = findViewById(R.id.inizio);//pulsante per selezionare l'ora di inizio, apre un timePickerDialog
        fine = findViewById(R.id.fine); //pulsante per selezionare l'ora di fine, apre un timePickerDialog
        oraInizio = findViewById(R.id.oraInizio); //ora effettiva di inizio che sarà passata all'oggetto impegno
        oraFine = findViewById(R.id.oraFine); //ora effettiva di fine che sarà passata all'oggetto impegno
        conferma = findViewById(R.id.creaImpegno);
        date = findViewById(R.id.datePicker);
        back = findViewById(R.id.indietro);

        // Inizializza il LayoutInflater
        inflater = getLayoutInflater();

        inizioListener(inizio, oraInizio);
        fineListener(fine, oraFine);

        conferma.setOnClickListener(v -> {

            if (checkInput() == 0) {

                //creo un oggetto di tipo calendar da passare al costruttore del nuovo impegno
                Calendar calendar = Calendar.getInstance();
                calendar.set(date.getYear(), date.getMonth(), date.getDayOfMonth());

                //creo un nuovo impegno con i dati inseriti
                Commit commit = new Commit(calendar, nomeImpegno.getText().toString(), oraInizio.getText().toString(), oraFine.getText().toString());
                updateUser(commit);
                result = new Intent(AddCommitActivity.this, HomeActivity.class);
                startActivity(result);
            }

        });

        back.setOnClickListener(v -> {
            result = new Intent(AddCommitActivity.this, HomeActivity.class);
            result.putExtra(HomeActivity.SCREEN_PATH, "calendar");
            startActivity(result);
            finish();
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(AddCommitActivity.this, HomeActivity.class);
                result.putExtra(HomeActivity.SCREEN_PATH, "calendar");
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }



    //funzioni che creano un nuovo timePicker sia per il pulsante inizio che per il pulsante fine
    public void inizioListener(Button inizio, TextView oraInizio) {

        inizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prendo l'istanza del calendario
                final Calendar c = Calendar.getInstance();

                //prendo l'ora del giorno
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                //Inizializzo un nuovo TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddCommitActivity.this, R.style.CustomDatePickerDialogTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String formattedHour= "";
                                String formattedMinute= "";

                                //controllo se le ore e i minuti sono in formato a due cifre
                                if (hourOfDay < 10 && minute < 10){
                                    formattedHour = String.format(Locale.ITALY,"%02d", hourOfDay);
                                    formattedMinute = String.format(Locale.ITALY,"%02d", minute);}
                                else if (hourOfDay < 10){
                                    formattedHour = String.format(Locale.ITALY,"%02d", hourOfDay);
                                    formattedMinute = String.valueOf(minute);}
                                else if (minute < 10) {
                                    formattedMinute = String.format(Locale.ITALY, "%02d", minute);
                                    formattedHour = String.valueOf(hourOfDay);}
                                else {
                                    formattedHour = String.valueOf(hourOfDay);
                                    formattedMinute = String.valueOf(minute);
                                }

                                //creo la stringa con l'ora selezionata
                                oraInizio.setText(formattedHour + ":" + formattedMinute);

                                String temp_inizio = oraInizio.getText().toString();
                                String temp_fine = oraFine.getText().toString();

                                //controllo che l'ora di fine sia maggiore di quella di inizio solo se l'ora di inizio è stata selezionata
                                if((temp_fine.equals("00:00") == false && temp_inizio.compareTo(temp_fine) > 0) || temp_fine.equals(temp_inizio) == true){

                                    View layout = inflater.inflate(R.layout.toast_error,
                                            (ViewGroup) findViewById(R.id.toast_layout_root));

                                    Toast toast = new Toast(getApplicationContext());
                                    String text = "L'ora di inizio non può essere maggiore o uguale a quella di fine";
                                    TextView scritta = layout.findViewById(R.id.toast_text);
                                    scritta.setText(text);
                                    toast.setGravity(Gravity.BOTTOM, 0, 50);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();

                                    //oraFine prenderà il valore di oraInizio e lo aumenterà di 1 ora
                                    int ora = Integer.parseInt(temp_inizio.substring(0,2));
                                    ora++;
                                    oraFine.setText(String.format(Locale.ITALY,"%02d", ora)+ temp_inizio.substring(2,5));
                                }
                                oraInizio.setError(null);
                            }
                        }, hour, minute, false);
                //mostro il timePickerDialog
                timePickerDialog.setCustomTitle(inflater.inflate(R.layout.timepicker_title, null));
                timePickerDialog.show();

            }
        });
    }


    public void fineListener(Button fine, TextView oraFine) {

        fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prendo l'istanza del calendario
                final Calendar c = Calendar.getInstance();

                //prendo l'ora del giorno
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                //Inizializzo un nuovo TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddCommitActivity.this, R.style.CustomDatePickerDialogTheme,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String formattedHour= "";
                                String formattedMinute= "";

                                //controllo se le ore e i minuti sono in formato a due cifre
                                if (hourOfDay < 10 && minute < 10){
                                    formattedHour = String.format(Locale.ITALY,"%02d", hourOfDay);
                                    formattedMinute = String.format(Locale.ITALY,"%02d", minute);}
                                else if (hourOfDay < 10){
                                    formattedHour = String.format(Locale.ITALY,"%02d", hourOfDay);
                                    formattedMinute = String.valueOf(minute);}
                                else if (minute < 10) {
                                    formattedMinute = String.format(Locale.ITALY, "%02d", minute);
                                    formattedHour = String.valueOf(hourOfDay);}
                                else {
                                    formattedHour = String.valueOf(hourOfDay);
                                    formattedMinute = String.valueOf(minute);
                                }
                                //creo la stringa con l'ora selezionata
                                oraFine.setText(formattedHour + ":" + formattedMinute);

                                String temp_inizio = oraInizio.getText().toString();
                                String temp_fine = oraFine.getText().toString();

                                //controllo che l'ora di fine sia maggiore di quella di inizio solo se l'ora di inizio è stata selezionata
                                if((temp_inizio.equals("00:00") == false && temp_inizio.compareTo(temp_fine) > 0) || temp_fine.equals(temp_inizio) == true){

                                    View layout = inflater.inflate(R.layout.toast_error,
                                            (ViewGroup) findViewById(R.id.toast_layout_root));

                                    Toast toast = new Toast(getApplicationContext());
                                    String text = "L'ora di inizio non può essere maggiore o uguale a quella di fine";
                                    TextView scritta = layout.findViewById(R.id.toast_text);
                                    scritta.setText(text);
                                    toast.setGravity(Gravity.BOTTOM, 0, 50);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();

                                    //oraFine prenderà il valore di oraInizio e lo aumenterà di 1 ora
                                    int ora = Integer.parseInt(temp_inizio.substring(0,2));
                                    ora++;
                                    oraFine.setText(String.format(Locale.ITALY,"%02d", ora)+ temp_inizio.substring(2,5));
                                }

                                oraFine.setError(null);

                            }
                        }, hour, minute, false);

                //mostro il timePickerDialog
                timePickerDialog.setCustomTitle(inflater.inflate(R.layout.timepicker_title, null));
                timePickerDialog.show();
            }
        });

    }


    public int checkInput() {
        int errors = 0;

        if (nomeImpegno.getText() == null || nomeImpegno.getText().length() == 0) {
            errors++;
            nomeImpegno.setError("Inserire un nome");
        } else
            nomeImpegno.setError(null);

        if (oraInizio.getText() == null || oraInizio.getText().toString().equals("00:00") || oraInizio.getText().length() == 0) {
            errors++;
            oraInizio.setError("Inserire un'ora di inizio");
        } else
            oraInizio.setError(null);

        if (oraFine.getText() == null || oraFine.getText().toString().equals("00:00") || oraFine.getText().length() == 0) {
            errors++;
            oraFine.setError("Inserire un'ora di fine");
        } else
            oraFine.setError(null);
        return errors;
    }


    public void updateUser(Commit commit){

        Person user = null;
        // Recupera l'utente dalle SharedPreferences
        SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

        String datiUtente = sharedPreferencesUtente.getString("User", "");

        if (!datiUtente.isEmpty()) {
            byte[] datiUtenteBytes = Base64.decode(datiUtente, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiUtenteBytes))) {
                user = (Person) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Aggiorna l'utente
        user.addImpegno(commit);

        SharedPreferences.Editor editorUtente = sharedPreferencesUtente.edit();

        // Converti l'utente attuale in un array di byte utilizzando la serializzazione
        ByteArrayOutputStream byteArrayOutputStreamUtente = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtente)) {
            objectOutputStream.writeObject(user);
            String newDatiUtente = Base64.encodeToString(byteArrayOutputStreamUtente.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editorUtente.putString("User", newDatiUtente);
            editorUtente.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveUser(user);
    }



    //funzione che salva l'utente con l'impegno rimosso e lo sostituisce con quello attuale
    private void saveUser(Person user) {


        ArrayList<Person> existing_user = new ArrayList<>();
        SharedPreferences sharedPreferencesUtenti = getSharedPreferences("utentiRegistrati", Context.MODE_PRIVATE);

        // Recupera tutti gli utenti
        String datiUtenti = sharedPreferencesUtenti.getString("arrayUtenti", "");

        if (!datiUtenti.isEmpty()) {
            byte[] datiGruppiBytes = Base64.decode(datiUtenti, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiGruppiBytes))) {
                existing_user = (ArrayList<Person>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Cerca l'utente e lo sostituisce
        for (Person p : existing_user) {
            if (p.getNome().equals(user.getNome()) && p.getCognome().equals(user.getCognome()) && p.getPassword().equals(user.getPassword())) {
                existing_user.remove(p);
                existing_user.add(user);
                break;
            }
        }

        // Salva i nuovi utenti
        SharedPreferences.Editor editorUtenti = sharedPreferencesUtenti.edit();
        ByteArrayOutputStream byteArrayOutputStreamUtenti = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtenti)) {
            objectOutputStream.writeObject(existing_user);
            String datiUtentiString = Base64.encodeToString(byteArrayOutputStreamUtenti.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editorUtenti.putString("arrayUtenti", datiUtentiString);
            editorUtenti.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}