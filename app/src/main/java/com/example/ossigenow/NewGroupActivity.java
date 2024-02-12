package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class NewGroupActivity extends AppCompatActivity {

    public final static String NEW_GROUP_PATH = "NEW_GROUP";
    private Intent result; // Intento per cambiare activity

    private Button creaGruppo;

    private ImageButton back;
    private EditText nomeGruppo;

    private RadioButton radioButton10, radioButton12, radioButton14;

    private RadioGroup radioGroup;

    private int numPartecipanti = -1;

    private Spinner frequenzaPartite;

    private Person utenteLoggato;

    private Group group;

    public ArrayList<Group> existing_group = new ArrayList<>();

    private EditText error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        nomeGruppo = findViewById(R.id.nomeGruppo);
        frequenzaPartite = findViewById(R.id.frequenzaPartite);
        creaGruppo = findViewById(R.id.creaGruppo);
        back = findViewById(R.id.indietro);
        error = findViewById(R.id.error);
        error.setInputType(InputType.TYPE_NULL);


        radioGroup = findViewById(R.id.radioGroup);
        radioButton10 = findViewById(R.id.radioButton10);
        radioButton12 = findViewById(R.id.radioButton12);
        radioButton14 = findViewById(R.id.radioButton14);

        utenteLoggato = (Person) getIntent().getSerializableExtra(HomeActivity.PERSON_PATH);

        SharedPreferences sharedPreferences = getSharedPreferences("gruppi", Context.MODE_PRIVATE);

        String datiArrayString = sharedPreferences.getString("chiave", "");

        if (!datiArrayString.isEmpty()) {
            byte[] datiArrayBytes = Base64.decode(datiArrayString, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiArrayBytes))) {
                existing_group = (ArrayList<Group>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Imposta il valore selezionato quando cambia la selezione
                if (checkedId == R.id.radioButton10) {
                    numPartecipanti = 10;
                } else if (checkedId == R.id.radioButton12) {
                    numPartecipanti = 12;
                } else if (checkedId == R.id.radioButton14) {
                    numPartecipanti = 14;
                }
            }
        });


        creaGruppo.setOnClickListener(v -> {
            if (!checkInput()) {
                result = new Intent(NewGroupActivity.this, GroupActivity.class);
                group = new Group(nomeGruppo.getText().toString(), frequenzaPartite.getSelectedItem().toString(), numPartecipanti, utenteLoggato);
                existing_group.add(group);
                saveGroupsToSharedPreferences(existing_group);
                result.putExtra(NEW_GROUP_PATH, group);
                startActivity(result);
                finish();
            }
        });

        back.setOnClickListener(v -> {
            result = new Intent(NewGroupActivity.this, HomeActivity.class);
            startActivity(result);
            finish();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(NewGroupActivity.this, HomeActivity.class);
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private boolean checkInput() {
        boolean errors = false;


        if (nomeGruppo.getText() == null || nomeGruppo.getText().length() == 0) {
            errors = true;
            nomeGruppo.setError("Inserire il nome del gruppo");
        } else {
            if (existing_group != null){
                boolean b = false;
                int n = 0;
                int n2;
                String nuovoNomeGruppo = nomeGruppo.getText().toString();
                while (!b) {
                    b = true;
                    n2 = n;
                    for (Group g : existing_group) {
                        if (g.getNomeGruppo().equals(nuovoNomeGruppo)) {
                            n++;
                        }
                    }
                    if (n != n2) {
                        b = false;
                        nuovoNomeGruppo = nomeGruppo.getText().toString() + "" + n;

                    }
                }
                nomeGruppo.setText(nuovoNomeGruppo);
            }
            nomeGruppo.setError(null);
        }

        if (numPartecipanti == -1) {
            errors = true;
            error.setError("Selezionare il numero di partecipanti");

        }
        else {
            error.setError(null);

        }
        return errors;
    }

    private void saveGroupsToSharedPreferences(ArrayList<Group> groups) {
        SharedPreferences sharedPreferences = getSharedPreferences("gruppi", Context.MODE_PRIVATE);

        // Convertire l'ArrayList in una stringa Base64
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(groups);
            String datiArrayString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            // Salvare la stringa Base64 nelle SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("chiave", datiArrayString);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}