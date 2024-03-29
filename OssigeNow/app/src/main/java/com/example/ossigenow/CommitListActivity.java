package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommitListActivity extends AppCompatActivity {


    private TextView day, assenza;
    private LinearLayout thisView;
    protected ImageButton back;
    protected LayoutInflater inflater;
    protected Person actualUser;

    private boolean isGroup;
    private ArrayList<Commit> commits = new ArrayList<>();
    private ArrayList<Commit> all_commits = new ArrayList<>();
    private Calendar actualDate;
    private Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_list);

        inflater = getLayoutInflater();

        thisView = findViewById(R.id.commit_container);
        back = findViewById(R.id.indietro);
        day = findViewById(R.id.day);
        assenza = findViewById(R.id.assenza_commit);

        //recupero la data selezionata e la setto per il dialog
        actualDate = (Calendar) getIntent().getSerializableExtra("data");
        setDay();

        //recupero l'utente loggato
        actualUser = recuperaUtenteLoggato();
        //recupero il gruppo
        isGroup = getIntent().getBooleanExtra("isGroup", false);

        //se non è un gruppo recupero gli impegni dell'utente
        if(!isGroup) {
            all_commits = actualUser.getImpegni();

            //prendo solo gli impegni del giorno
            for (Commit commit : all_commits) {
                if (commit.getData().get(Calendar.DAY_OF_MONTH) == actualDate.get(Calendar.DAY_OF_MONTH) &&
                        commit.getData().get(Calendar.MONTH) == actualDate.get(Calendar.MONTH) &&
                        commit.getData().get(Calendar.YEAR) == actualDate.get(Calendar.YEAR)) {
                    commits.add(commit);
                }
            }
            //se non ci sono impegni mostro un messaggio
            if (commits.isEmpty()) {
                assenza.setVisibility(View.VISIBLE);
            }else{
                //prendo ogni impegno e lo mostro
                for (Commit impegno : commits) {

                    View view = inflater.inflate(R.layout.impegno, null);
                    TextView nome, orario;
                    ImageButton delete;

                    nome = view.findViewById(R.id.nome_impegno);
                    orario = view.findViewById(R.id.orario);
                    delete = view.findViewById(R.id.delete);

                    //se l'impegno non è cancellabile non mostro il tasto
                    if (!impegno.getIsDeleteble()) {
                        delete.setVisibility(View.GONE);
                    }

                    //setto il nome e l'orario dell'impegno
                    nome.setText(impegno.getNome());
                    String inizio = impegno.getOraInizio();
                    String fine = impegno.getOraFine();

                    orario.setText("Dalle: " + inizio + " alle: " + fine);

                    int marginInDp = 20; // Puoi specificare il valore direttamente
                    int marginInPixels = (int) (marginInDp * getResources().getDisplayMetrics().density);

                    // Impostare i margini per creare uno spazio tra le viste
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, marginInPixels);
                    view.setLayoutParams(layoutParams);

                    thisView.addView(view);

                    delete.setOnClickListener(v -> {
                        chiediConfermaCancellazione(impegno.getNome(), thisView, view, inflater, impegno);
                    });
                }
            }

            //il pulsante indietro mi riporta alla schermata del calendario dell'utente
            back.setOnClickListener(v -> {
                result = new Intent(CommitListActivity.this, HomeActivity.class);
                result.putExtra(HomeActivity.SCREEN_PATH, "calendar");
                startActivity(result);
                finish();
            });

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    result = new Intent(CommitListActivity.this, HomeActivity.class);
                    result.putExtra(HomeActivity.SCREEN_PATH, "calendar");
                    startActivity(result);
                    finish();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);

            //Se è un gruppo recupero gli impegni del gruppo
        }else {
            all_commits = (ArrayList<Commit>) getIntent().getSerializableExtra("impegni");

            //prendo gli impegni dell'utente
            ArrayList<Commit> loggedUserCommits = actualUser.getImpegni();

            //prendo solo gli impegni del giorno
            for (Commit commit : all_commits) {
                if (commit.getData().get(Calendar.DAY_OF_MONTH) == actualDate.get(Calendar.DAY_OF_MONTH) &&
                        commit.getData().get(Calendar.MONTH) == actualDate.get(Calendar.MONTH) &&
                        commit.getData().get(Calendar.YEAR) == actualDate.get(Calendar.YEAR)) {
                    commits.add(commit);
                }
            }
            //se non ci sono impegni mostro un messaggio
            if (commits.isEmpty()) {
                assenza.setVisibility(View.VISIBLE);
            }else {
                //prendo ogni impegno e lo mostro
                for (Commit impegno : commits) {

                    //creo la vista per ogni impegno
                    View view = inflater.inflate(R.layout.impegno, null);
                    TextView nome, orario;
                    ImageView delete;
                    CardView bottomLine;
                    boolean isLoggedUser = false;

                    nome = view.findViewById(R.id.nome_impegno);
                    orario = view.findViewById(R.id.orario);
                    delete = view.findViewById(R.id.delete);
                    bottomLine = view.findViewById(R.id.bottom_line);

                    delete.setVisibility(View.GONE);

                    //controllo se l'impegno è dell'utente loggato
                    for (Commit commit : loggedUserCommits) {
                        if (commit.getData().equals(impegno.getData()) && commit.getNome().equals(impegno.getNome()) && commit.getOraInizio().equals(impegno.getOraInizio()) && commit.getOraFine().equals(impegno.getOraFine())) {
                            nome.setText(impegno.getNome());
                            isLoggedUser = true;
                        }
                    }

                    //se l'impegno non è dell'utente loggato non mostro il tasto per cancellare
                    if (!isLoggedUser) {
                        //cambio il colore della linea mettendo il colore del gruppo
                        bottomLine.setCardBackgroundColor(getColor(R.color.group_commit));
                        //cambio il nome dell'impegno generalizzandolo per privacy
                        nome.setText("Impegno di un altro utente");
                    }

                    //se l'impegno non è cancellabile non mostro il tasto
                    if (!impegno.getIsDeleteble()) {
                        delete.setVisibility(View.GONE);
                    }

                    //setto l'orario dell'impegno
                    String inizio = impegno.getOraInizio();
                    String fine = impegno.getOraFine();

                    orario.setText("Dalle: " + inizio + " alle: " + fine);

                    int marginInDp = 20; // Puoi specificare il valore direttamente
                    int marginInPixels = (int) (marginInDp * getResources().getDisplayMetrics().density);

                    // Impostare i margini per creare uno spazio tra le viste
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, marginInPixels);
                    view.setLayoutParams(layoutParams);

                    thisView.addView(view);

                }

            }
            // il pulsante indietro mi riporta alla schermata del calendario di prima
            back.setOnClickListener(v -> {
                finish();
            });
        }
    }


    private void setDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        day.setText("Impegni del: " + simpleDateFormat.format(actualDate.getTime()));
    }

    private Person recuperaUtenteLoggato() {
        Person utenteLoggato = null;

        // Recupera l'utente dalle SharedPreferences
        SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

        // Recupera la rappresentazione di byte come stringa dalle SharedPreferences
        String datiUtente = sharedPreferencesUtente.getString("User", "");

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


    private void chiediConfermaCancellazione(String name,LinearLayout thisView, View view, LayoutInflater inflater,Commit impegno) {
        View alert_view = inflater.inflate(R.layout.dialogo_conferma, null);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(alert_view);

        String messaggio = "Stai per cancellare l'impegno:\n\n"+"'"+name+"'"+"\n\nSei sicuro?\n";
        TextView alert_dialog = alert_view.findViewById(R.id.completa_avviso);
        alert_dialog.setText(messaggio);

        Button conferma = alert_view.findViewById(R.id.conferma);
        Button annulla = alert_view.findViewById(R.id.annulla);

        dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

        conferma.setOnClickListener(v -> {
            thisView.removeView(view);
            dialog.dismiss();
            updateUser(impegno);
        });

        annulla.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }





    public void updateUser(Commit commit){

        SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

        SharedPreferences.Editor editorUtente = sharedPreferencesUtente.edit();

        actualUser.removeImpegno(commit);

        // Converti l'utente attuale in un array di byte utilizzando la serializzazione
        ByteArrayOutputStream byteArrayOutputStreamUtente = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamUtente)) {
            objectOutputStream.writeObject(actualUser);
            String newDatiUtente = Base64.encodeToString(byteArrayOutputStreamUtente.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editorUtente.putString("User", newDatiUtente);
            editorUtente.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveUser(actualUser);
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