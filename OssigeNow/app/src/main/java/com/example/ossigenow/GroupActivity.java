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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    // Passaggio dati tra activity
    public final static String GROUP_PATH = "GROUP";
    private Intent result; // Intento per cambiare activity

    // Oggetti per salvare
    private Group group;
    private ArrayList<Person> utentiRegistrati = new ArrayList<>();
    private Person utenteLoggato = new Person();
    //public ArrayList<Group> existing_group = new ArrayList<>();
    private ArrayList<Invito> inviti = new ArrayList<>();

    // Elementi UI
    private TextView prova;
    private TextView group_name, number_user, cadenza, prossima_partita, campo;
    private ImageButton back;
    private Button exit_group, delete_group;
    private LinearLayout invito, impegni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group_name = findViewById(R.id.group_name);
        number_user = findViewById(R.id.number_user);
        cadenza = findViewById(R.id.cadenza);
        campo = findViewById(R.id.campo);
        prossima_partita = findViewById(R.id.prossima_partita);
        invito = findViewById(R.id. invite);
        impegni = findViewById(R.id.controlloImpegni);
        back = findViewById(R.id.indietro);
        exit_group = findViewById(R.id.exit_group);
        delete_group = findViewById(R.id.delete_group);

        group = (Group) getIntent().getSerializableExtra(NewGroupActivity.NEW_GROUP_PATH);
        if (group != null){
            utenteLoggato = group.getAdmin();
        }

        if (group == null) {
            group = (Group) getIntent().getSerializableExtra(HomeActivity.GROUP_PATH);
            utenteLoggato = (Person) getIntent().getSerializableExtra(HomeActivity.PERSON_PATH);
        }

        if (!utenteLoggato.getNomeUtente().equals(group.getAdmin().getNomeUtente())) {
            exit_group.setVisibility(View.VISIBLE);  // Rendi il bottone visibile
            exit_group.setClickable(true);  // Rendi il bottone cliccabile
        } else {
            exit_group.setVisibility(View.INVISIBLE);  // Rendi il bottone invisibile
            exit_group.setClickable(false);  // Rendi il bottone non cliccabile
        }

        if (utenteLoggato.getNomeUtente().equals(group.getAdmin().getNomeUtente())) {
            delete_group.setVisibility(View.VISIBLE);  // Rendi il bottone visibile
            delete_group.setClickable(true);  // Rendi il bottone cliccabile
        } else {
            delete_group.setVisibility(View.INVISIBLE);  // Rendi il bottone invisibile
            delete_group.setClickable(false);  // Rendi il bottone non cliccabile
        }

        utentiRegistrati = recuperaUtentiRegistrati();

        group_name.setText(group.getNomeGruppo());
        number_user.setText(group.getNumberParticipanti()+" / "+group.getPartecipantiRichiesti());
        cadenza.setText(group.getFrequenzaPartite());
        if (group.getProssima_partita() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            prossima_partita.setText(sdf.format(group.getProssima_partita().getData().getTime()));
            campo.setText(group.getProssima_partita().getCampo().getNomeCampo());
        } else{
            prossima_partita.setText("Data da definire");
            campo.setText("Campo da definire");
        }



        LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout thisView = (LinearLayout) findViewById(R.id.block_user);
        for (Person p : group.getPartecipanti()) {
            if (!utenteLoggato.getNomeUtente().equals(group.getAdmin().getNomeUtente())) {
                View view = inflater.inflate(R.layout.see_user, null);

                TextView is_admin = view.findViewById(R.id.admin);
                TextView user_name = view.findViewById(R.id.name_user);
                TextView letter_user = view.findViewById(R.id.letter_user);

                if (p.getNomeUtente().equals(group.getAdmin().getNomeUtente())) {
                    is_admin.setVisibility(View.VISIBLE);
                }

                if(p.getNomeUtente().equals(utenteLoggato.getNomeUtente())){
                    letter_user.setBackgroundResource(R.drawable.letter_actual_user);
                }

                user_name.setText(p.getNomeUtente());
                letter_user.setText(p.getNomeUtente().substring(0, 1).toUpperCase());

                thisView.addView(view);
            }
            else {

                if (p.getNomeUtente().equals(group.getAdmin().getNomeUtente())) {
                    View view = inflater.inflate(R.layout.see_user, null);

                    TextView is_admin = view.findViewById(R.id.admin);
                    TextView user_name = view.findViewById(R.id.name_user);
                    TextView letter_user = view.findViewById(R.id.letter_user);

                    is_admin.setVisibility(View.VISIBLE);

                    if(p.getNomeUtente().equals(utenteLoggato.getNomeUtente())){
                        letter_user.setBackgroundResource(R.drawable.letter_actual_user);
                    }
                    user_name.setText(p.getNomeUtente());
                    letter_user.setText(p.getNomeUtente().substring(0, 1).toUpperCase());

                    thisView.addView(view);
                }

                else{
                    View view = inflater.inflate(R.layout.see_user_admin, null);

                    TextView user_name = view.findViewById(R.id.name_user);
                    TextView letter_user = view.findViewById(R.id.letter_user);

                    if(p.getNomeUtente().equals(utenteLoggato.getNomeUtente())){
                        letter_user.setBackgroundResource(R.drawable.letter_actual_user);
                    }

                    ImageButton delete_user = view.findViewById(R.id.delete_user);

                    user_name.setText(p.getNomeUtente());
                    letter_user.setText(p.getNomeUtente().substring(0, 1).toUpperCase());

                    thisView.addView(view);

                    delete_user.setOnClickListener(v -> {
                        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View alert_view = inflater2.inflate(R.layout.dialogo_conferma, null);

                        final Dialog dialog = new Dialog(this);
                        dialog.setContentView(alert_view);

                        String messaggio = "Sei sicuro di voler togliere l'utente " + p.getNomeUtente() + " dal gruppo?\n\n";
                        TextView alert_dialog = alert_view.findViewById(R.id.completa_avviso);
                        alert_dialog.setText(messaggio);

                        Button conferma = alert_view.findViewById(R.id.conferma);
                        Button annulla = alert_view.findViewById(R.id.annulla);

                        dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

                        conferma.setOnClickListener(v2 -> {
                            group.removePartecipante(p);
                            saveGroups(group);
                            dialog.dismiss();
                            ((ViewGroup) view.getParent()).removeView(view);
                            number_user.setText(group.getNumberParticipanti()+" / "+group.getPartecipantiRichiesti());
                        });

                        annulla.setOnClickListener(v2 -> {
                            dialog.dismiss();
                        });

                        dialog.show();
                    });
                }
            }
        }

        exit_group.setOnClickListener(v -> {

            LayoutInflater inflater3 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alert_view = inflater3.inflate(R.layout.dialogo_conferma, null);

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(alert_view);

            String messaggio = "Sei sicuro di voler uscire dal gruppo " + group.getNomeGruppo() + " ?\n\n";
            TextView alert_dialog = alert_view.findViewById(R.id.completa_avviso);
            alert_dialog.setText(messaggio);

            Button conferma = alert_view.findViewById(R.id.conferma);
            Button annulla = alert_view.findViewById(R.id.annulla);

            dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

            conferma.setOnClickListener(v2 -> {
                group.removePartecipante_by_UsernName(utenteLoggato.getNomeUtente());
                saveGroups(group);
                dialog.dismiss();
                number_user.setText(group.getNumberParticipanti()+" / "+group.getPartecipantiRichiesti());
                result = new Intent(GroupActivity.this, HomeActivity.class);
                result.putExtra(HomeActivity.SCREEN_PATH, "home");
                startActivity(result);
            });

            annulla.setOnClickListener(v2 -> {
                dialog.dismiss();
            });

            dialog.show();
        });

        delete_group.setOnClickListener(v -> {

            LayoutInflater inflater3 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View alert_view = inflater3.inflate(R.layout.dialogo_conferma, null);

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(alert_view);

            String messaggio = "Sei sicuro di voler eliminare il gruppo " + group.getNomeGruppo() + " ?\n\n";
            TextView alert_dialog = alert_view.findViewById(R.id.completa_avviso);
            alert_dialog.setText(messaggio);

            Button conferma = alert_view.findViewById(R.id.conferma);
            Button annulla = alert_view.findViewById(R.id.annulla);

            dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

            conferma.setOnClickListener(v2 -> {
                deleteGroup(group);
                deleteInvite();
                dialog.dismiss();
                result = new Intent(GroupActivity.this, HomeActivity.class);
                result.putExtra(HomeActivity.SCREEN_PATH, "home");
                startActivity(result);
                finish();
            });

            annulla.setOnClickListener(v2 -> {
                dialog.dismiss();
            });

            dialog.show();
        });

        invito.setOnClickListener(v -> {
            if (group.getNumberParticipanti() > group.getPartecipantiRichiesti()) {
                errorMessage("Il gruppo è pieno");
            } else{
                LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View invite_view = inflater2.inflate(R.layout.send_invite, null);

                final Dialog dialog = new Dialog(this);
                dialog.setContentView(invite_view);

                EditText nome_utente = invite_view.findViewById(R.id.nomeUtente);

                Button invia = invite_view.findViewById(R.id.invia);
                Button annulla = invite_view.findViewById(R.id.annulla);

                dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

                invia.setOnClickListener(v2 -> {
                    if (checkInput(nome_utente)) {
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
            }
        });

        impegni.setOnClickListener(v -> {
            result = new Intent(GroupActivity.this, GroupCalendarActivity.class);
            result.putExtra(GROUP_PATH, group);
            startActivity(result);
            finish();
        });

        back.setOnClickListener(v -> {
            result = new Intent(GroupActivity.this, HomeActivity.class);
            result.putExtra(HomeActivity.SCREEN_PATH, "home");
            startActivity(result);
            finish();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(GroupActivity.this, HomeActivity.class);
                result.putExtra(HomeActivity.SCREEN_PATH, "home");
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
                    ArrayList<Invito> invitiUtente = new ArrayList<>();
                    // Recupera gli inviti dell'utente da invitare
                    String path = "inviti" + nomeUtente.getText();
                    SharedPreferences sharedPreferencesInviti = getSharedPreferences(path, Context.MODE_PRIVATE);

                    String datiInviti = sharedPreferencesInviti.getString("chiave", "");

                    if (!datiInviti.isEmpty()) {
                        byte[] datiInvitiBytes = Base64.decode(datiInviti, Base64.DEFAULT);
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiInvitiBytes))) {
                            invitiUtente = (ArrayList<Invito>) objectInputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    for (Invito i : invitiUtente) {
                        if (i.getGroup().getNomeGruppo().equals(group.getNomeGruppo())) {
                            errors++;
                            errorMessage("E' già stato inviato un invito a questo utente");
                            break;
                        }
                    }
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

    private void saveGroups(Group group) {
        ArrayList<Group> existing_group = new ArrayList<>();
        SharedPreferences sharedPreferencesGruppi = getSharedPreferences("gruppi", Context.MODE_PRIVATE);

        // Recupera tutti i gruppi
        String datiGruppi = sharedPreferencesGruppi.getString("chiave", "");

        if (!datiGruppi.isEmpty()) {
            byte[] datiGruppiBytes = Base64.decode(datiGruppi, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiGruppiBytes))) {
                existing_group = (ArrayList<Group>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Cerca il gruppo e lo sostituisce
        for (Group g : existing_group) {
            if (g.getNomeGruppo().equals(group.getNomeGruppo())) {
                existing_group.remove(g);
                existing_group.add(group);
                break;
            }
        }

        // Salva i gruppi
        SharedPreferences.Editor editorGruppi = sharedPreferencesGruppi.edit();
        ByteArrayOutputStream byteArrayOutputStreamGruppi = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamGruppi)) {
            objectOutputStream.writeObject(existing_group);
            String datiGruppiString = Base64.encodeToString(byteArrayOutputStreamGruppi.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editorGruppi.putString("chiave", datiGruppiString);
            editorGruppi.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteGroup(Group group) {
        ArrayList<Group> existing_group = new ArrayList<>();
        SharedPreferences sharedPreferencesGruppi = getSharedPreferences("gruppi", Context.MODE_PRIVATE);

        // Recupera tutti i gruppi
        String datiGruppi = sharedPreferencesGruppi.getString("chiave", "");

        if (!datiGruppi.isEmpty()) {
            byte[] datiGruppiBytes = Base64.decode(datiGruppi, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiGruppiBytes))) {
                existing_group = (ArrayList<Group>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Cerca il gruppo e lo sostituisce
        for (Group g : existing_group) {
            if (g.getNomeGruppo().equals(group.getNomeGruppo())) {
                existing_group.remove(g);
                break;
            }
        }

        // Salva i gruppi
        SharedPreferences.Editor editorGruppi = sharedPreferencesGruppi.edit();
        ByteArrayOutputStream byteArrayOutputStreamGruppi = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamGruppi)) {
            objectOutputStream.writeObject(existing_group);
            String datiGruppiString = Base64.encodeToString(byteArrayOutputStreamGruppi.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editorGruppi.putString("chiave", datiGruppiString);
            editorGruppi.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteInvite() {
        for (Invito i : inviti) {
            if (i.getGroup().getNomeGruppo().equals(group.getNomeGruppo())) {
                inviti.remove(i);
            }
        }

        for (Person p : utentiRegistrati){
            String path = "inviti" + p.getNomeUtente();
            SharedPreferences sharedPreferences2 = getSharedPreferences(path, Context.MODE_PRIVATE);
            ByteArrayOutputStream byteArrayOutputStreamUtente = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtente)) {
                objectOutputStream.writeObject(inviti);
                String datiInvito = Base64.encodeToString(byteArrayOutputStreamUtente.toByteArray(), Base64.DEFAULT);


                // Ottieni un Editor per modificare le SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences2.edit();

                // Salva la rappresentazione di byte come stringa nelle SharedPreferences
                editor.putString("chiave", datiInvito);
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}