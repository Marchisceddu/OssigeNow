package com.example.ossigenow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
    private EditText nomeGruppo, numPartecipanti;

    private Spinner frequenzaPartite;

    private Person utenteLoggato;

    private Group group;

    public ArrayList<Group> existing_group = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        nomeGruppo = findViewById(R.id.nomeGruppo);
        numPartecipanti = findViewById(R.id.numPartecipanti);
        frequenzaPartite = findViewById(R.id.frequenzaPartite);
        creaGruppo = findViewById(R.id.creaGruppo);

        utenteLoggato = (Person) getIntent().getSerializableExtra(MainActivity.PERSON_PATH);

        SharedPreferences sharedPreferences = getSharedPreferences("gruppi", Context.MODE_PRIVATE);

        String datiArrayString = sharedPreferences.getString(utenteLoggato.getNomeUtente(), "");

        if (!datiArrayString.isEmpty()) {
            byte[] datiArrayBytes = Base64.decode(datiArrayString, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiArrayBytes))) {
                existing_group = (ArrayList<Group>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        creaGruppo.setOnClickListener(v -> {
            if (!checkInput()) {
                int nPartecipanti = Integer.parseInt(numPartecipanti.getText().toString());
                result = new Intent(NewGroupActivity.this, GroupActivity.class);
                group = new Group(nomeGruppo.getText().toString(), frequenzaPartite.getSelectedItem().toString(), nPartecipanti, utenteLoggato);
                existing_group.add(group);
                saveGroupsToSharedPreferences(existing_group);
                result.putExtra(NEW_GROUP_PATH, group);
                startActivity(result);
                finish();
            }
        });
    }

    private boolean checkInput() {
        boolean errors = false;


        if (nomeGruppo.getText() == null || nomeGruppo.getText().length() == 0) {
            errors = true;
            nomeGruppo.setError("Inserire il nome del gruppo");
        } else {
            if (existing_group != null){
                int n = 0;
                for(Group g : existing_group){
                    if(g.getNomeGruppo().equals(nomeGruppo.getText().toString())){
                        n++;
                    }
                }
                if (n > 0){
                    String nuovoNomeGruppo = nomeGruppo.getText().toString() + "" + n;
                    nomeGruppo.setText(nuovoNomeGruppo);
                }
            }
            nomeGruppo.setError(null);
        }

        if (numPartecipanti.getText() == null || numPartecipanti.getText().length() == 0) {
            errors = true;
            numPartecipanti.setError("Inserire il numero di partecipanti");
        }
        else {

            int nPartecipanti = Integer.parseInt(numPartecipanti.getText().toString());

            if (nPartecipanti != 10 && nPartecipanti != 12) {
                errors = true;
                numPartecipanti.setError("Il numero di partecipanti deve essere 10 o 12");
            }
            else {
                numPartecipanti.setError(null);
            }
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
            editor.putString(utenteLoggato.getNomeUtente(), datiArrayString);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}