package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    // Passaggio dati tra activity
    public final static String GROUP_PATH = "GROUP";
    private Intent result; // Intento per cambiare activity

    // Oggetti per salvare
    private Group group;
    private ArrayList<Person> utentiRegistrati = new ArrayList<>();
    private Person utenteLoggato = new Person();
    public ArrayList<Group> existing_group = new ArrayList<>();
    private ArrayList<Invito> inviti = new ArrayList<>();

    // Elementi UI
    private TextView prova;
    private TextView group_name, number_user;
    private ImageButton back;
    private LinearLayout invito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group_name = findViewById(R.id.group_name);
        number_user = findViewById(R.id.number_user);
        invito = findViewById(R.id. invite);
        back = findViewById(R.id.indietro);

        group = (Group) getIntent().getSerializableExtra(NewGroupActivity.NEW_GROUP_PATH);
        if (group != null){
            utenteLoggato = group.getPartecipanti().get(0);
        }

        if (group == null) {
            group = (Group) getIntent().getSerializableExtra(HomeActivity.GROUP_PATH);
            utenteLoggato = (Person) getIntent().getSerializableExtra(HomeActivity.PERSON_PATH);
        }

        utentiRegistrati = recuperaUtentiRegistrati();

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

        group_name.setText(group.getNomeGruppo());
        number_user.setText(group.getNumberParticipanti()+"/"+group.getPartecipantiRichiesti());


        LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout thisView = (LinearLayout) findViewById(R.id.block_user);
        for (Person p : group.getPartecipanti()) {
            View view = inflater.inflate(R.layout.see_user, null);

            TextView user_name = view.findViewById(R.id.name_user);
            TextView letter_user = view.findViewById(R.id.letter_user);

            user_name.setText(p.getNomeUtente());
            letter_user.setText(p.getNomeUtente().substring(0,1).toUpperCase());

            thisView.addView(view);
        }

        invito.setOnClickListener(v -> {
            LayoutInflater inflater2 =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View invite_view = inflater2.inflate(R.layout.send_invite, null);

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(invite_view);

            EditText nome_utente = invite_view.findViewById(R.id.nomeUtente);

            Button invia = invite_view.findViewById(R.id.invia);
            Button annulla = invite_view.findViewById(R.id.annulla);

            dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

            invia.setOnClickListener(v2 -> {
                if(checkInput(nome_utente)) {
                    Invito invito = new Invito(group, nome_utente.getText().toString(), utenteLoggato.getNomeUtente());

                    String path = "inviti" + nome_utente.getText().toString();
                    SharedPreferences sharedPreferences2 = getSharedPreferences(path, Context.MODE_PRIVATE);

                    // Ottieni un Editor per modificare le SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences2.edit();

                    // Recupero gli inviti dell'utente per appendere il nuovo
                    String datiInviti = sharedPreferences2.getString("chiave", "");

                    if (!datiInviti.isEmpty()) {
                        byte[] datiInvitiBytes = Base64.decode(datiInviti, Base64.DEFAULT);
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiInvitiBytes))) {
                            inviti = (ArrayList<Invito>) objectInputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    inviti.add(invito);

                    // Converti l'utente attuale in un array di byte utilizzando la serializzazione
                    ByteArrayOutputStream byteArrayOutputStreamUtente = new ByteArrayOutputStream();
                    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtente)) {
                        objectOutputStream.writeObject(inviti);
                        String datiInvito = Base64.encodeToString(byteArrayOutputStreamUtente.toByteArray(), Base64.DEFAULT);

                        // Salva la rappresentazione di byte come stringa nelle SharedPreferences
                        editor.putString("chiave", datiInvito);
                        editor.apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    confirmMessage();
                    dialog.dismiss();
                }
            });

            annulla.setOnClickListener(v2 -> {
                dialog.dismiss();
            });
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    dialog.dismiss();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);

            dialog.show();
        });

        back.setOnClickListener(v -> {
            result = new Intent(GroupActivity.this, HomeActivity.class);
            startActivity(result);
            finish();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(GroupActivity.this, HomeActivity.class);
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private boolean checkInput(EditText nomeUtente) {
        int errors = 0;

        if (nomeUtente.getText() == null || nomeUtente.getText().length() == 0) {
            errors++;
            nomeUtente.setError("Inserire il Nome Utente");
        } else if (nomeUtente.getText().toString().compareTo(utenteLoggato.getNomeUtente())==0) {
            errors++;
            errorMessage("Non puoi inviare un invito a te stesso");
        }
        else {
            Boolean n = false;

            for (Person p : group.getPartecipanti()) {
                if ((nomeUtente.getText().toString().compareTo(p.getNomeUtente())) == 0) {
                    n = true;
                }
            }
            if (n) {
                errors++;
                errorMessage("Utente già presente nel gruppo");
            }
            else {
                for (Person u : utentiRegistrati) {
                    if ((nomeUtente.getText().toString().compareTo(u.getNomeUtente())) == 0) {
                        n = true;
                    }
                }
                if (!n) {
                    errors++;
                    errorMessage("Nome Utente non esistente");
                } else {
                    // da cambiare con la nuova shared
//                    SharedPreferences sharedPreferences = getSharedPreferences(nomeUtente.getText().toString(), Context.MODE_PRIVATE);
//
//                    // Recupera il valore associato alla chiave "group_name"
//                    String existing_invite = sharedPreferences.getString(group_name.getText().toString(), "");
//
//                    if (!existing_invite.equals("")) {
//                        errors++;
//                        errorMessage("E già stato inviato un invito a questo utente");
//                    } else {
//                        nomeUtente.setError(null);
//                    }
                }
            }
        }

        return errors == 0;
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

    private void confirmMessage(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_succes,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView scritta = layout.findViewById(R.id.toast_text);
        scritta.setText("Invito inviato con successo");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void errorMessage(String s) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_error,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView scritta = layout.findViewById(R.id.toast_text);
        scritta.setText(s);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}