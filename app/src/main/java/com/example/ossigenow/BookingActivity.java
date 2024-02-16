package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {
    // Passaggio dati tra activity
    private  final static String PRENOTAZIONE_PATH = "PRENOTAZIONE";
    private Intent result;

    // Salvataggio dati
    private Group gruppo;
    private Person user;
    private ArrayList<Commit> impegni = new ArrayList<>();
    private ArrayList<String> disponibilitaData = new ArrayList<>();
    private ArrayList<Integer> disponibilitaCampo = new ArrayList<>();
    private Partita partitaScelta;

    // Elementi UI
    private ImageButton back;
    private Button prenota;

    // Elenco di campi disponibili
    private ArrayList<Campo> campi = new ArrayList<>();

    // Variabile per stampare solo 10 disponibilità o tutte quelle disponibili
    private boolean fullStampa = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Recupera i dati passati da un'altra activity
        gruppo = (Group) getIntent().getSerializableExtra(HomeActivity.GROUP_PATH);
        user = (Person) getIntent().getSerializableExtra(HomeActivity.PERSON_PATH);

        // Inizializza gli elementi UI
        back = findViewById(R.id.indietro);
        prenota = findViewById(R.id.prenota);

        // Recupera i campi
        recuperaCampi();

        // inserisco impegni finti
        for (Person p : gruppo.getPartecipanti()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 17);

            Commit commit = new Commit(calendar, "nome", "14:00", "16:00");
            p.addImpegno(commit);
        }

        // Inizializza l'elenco di impegni dei partecipanti
        for (Person p : gruppo.getPartecipanti()) {
            impegni.addAll(p.getImpegni());
        }

        System.out.println(impegni.get(0).getData());

        trovaDisponibilita();
        printDisponibilita();

        prenota.setOnClickListener(v -> {
            gruppo.setProssima_partita(partitaScelta);
            partitaScelta.getCampo().addPrenotazione(partitaScelta.getData());
            saveGroups(gruppo);

            addCommit(partitaScelta, gruppo);

            confirmMessage("Prenotazione effettuata con successo");

            result = new Intent(BookingActivity.this, HomeActivity.class);
            result.putExtra(HomeActivity.SCREEN_PATH, "home");
            startActivity(result);
            finish();
        });

        // Torna indietro all'activity precedente
        back.setOnClickListener(v -> {
            result = new Intent(BookingActivity.this, HomeActivity.class);
            result.putExtra(HomeActivity.SCREEN_PATH, "booking");
            startActivity(result);
            finish();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(BookingActivity.this, HomeActivity.class);
                result.putExtra(HomeActivity.SCREEN_PATH, "booking");
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onDestroy() {
        // Salvo i campi
        SharedPreferences sharedPreferences = getSharedPreferences("Campi", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Converti l'utente attuale in un array di byte utilizzando la serializzazione
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(campi);
            String dati = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editor.putString("chiave", dati);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onStop() {
        // Salvo i campi
        SharedPreferences sharedPreferences = getSharedPreferences("Campi", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Converti l'utente attuale in un array di byte utilizzando la serializzazione
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(campi);
            String dati = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editor.putString("chiave", dati);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    private void recuperaCampi() {
        SharedPreferences sharedPreferences = getSharedPreferences("Campi", Context.MODE_PRIVATE);

        // Recupera tutti i campi
        String dati = sharedPreferences.getString("chiave", "");

        if (!dati.isEmpty()) {
            byte[] datiBytes = Base64.decode(dati, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiBytes))) {
                campi = (ArrayList<Campo>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (campi.isEmpty()) {
            // Inizializza l'elenco di campi disponibili
            campi.add(new Campo("Campo 1", "sintetico", R.drawable.campo1));
            campi.add(new Campo("Campo 2", "naturale", R.drawable.campo2));
            campi.add(new Campo("Campo 3", "sintetico", R.drawable.campo3));
            campi.add(new Campo("Campo 4", "naturale", R.drawable.campo4));
        }
    }

    private void printFakeCampi(ArrayList<String> fakeDate) {
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout thisView = findViewById(R.id.container_campi);

        if (true) { // se si implementa vera bisogna mettere controllo che non ci giorni in cui ci sono campi liberi e nessuno ha impegni
            TextView no = findViewById(R.id.no_campi);
            no.setVisibility(View.GONE);
        }

        ArrayList<View> campoViews = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            View view = inflater.inflate(R.layout.campo, null);
            ImageView foto;
            TextView nome, data_ora_campo, caratteristiche, scrittaData, scrittaCaratteristiche;
            LinearLayout click;
            int randC = (int) (Math.random() * campi.size());
            int pos = i;

            campoViews.add(view);

            foto = view.findViewById(R.id.foto_campo);
            nome = view.findViewById(R.id.nome_campo);
            data_ora_campo = view.findViewById(R.id.data_ora_campo);
            caratteristiche = view.findViewById(R.id.caratteristiche);
            scrittaData = view.findViewById(R.id.scrittaData);
            scrittaCaratteristiche = view.findViewById(R.id.scrittaCaratteristiche);
            click = view.findViewById(R.id.campo);

            foto.setImageDrawable(ContextCompat.getDrawable(this, campi.get(randC).getImg()));
            nome.setText(campi.get(randC).getNomeCampo());
            data_ora_campo.setText(fakeDate.get(pos));
            caratteristiche.setText(campi.get(randC).getCaratteristiche());

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

            click.setOnClickListener(v -> {
                for (View campoView : campoViews) {
                    TextView nome2, data_ora_campo2, caratteristiche2, scrittaData2, scrittaCaratteristiche2;

                    nome2 = campoView.findViewById(R.id.nome_campo);
                    data_ora_campo2 = campoView.findViewById(R.id.data_ora_campo);
                    caratteristiche2 = campoView.findViewById(R.id.caratteristiche);
                    scrittaData2 = campoView.findViewById(R.id.scrittaData);
                    scrittaCaratteristiche2 = campoView.findViewById(R.id.scrittaCaratteristiche);

                    // Cambia il background di ciascuna vista
                    campoView.setBackground(ContextCompat.getDrawable(this, R.drawable.background_gruppo));
                    nome2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    data_ora_campo2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    caratteristiche2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    scrittaData2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    scrittaCaratteristiche2.setTextColor(ContextCompat.getColor(this, R.color.black));
                }

                // Cambia il background dell'elemento cliccato
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.background_selected));
                nome.setTextColor(ContextCompat.getColor(this, R.color.white));
                data_ora_campo.setTextColor(ContextCompat.getColor(this, R.color.white));
                caratteristiche.setTextColor(ContextCompat.getColor(this, R.color.white));
                scrittaData.setTextColor(ContextCompat.getColor(this, R.color.white));
                scrittaCaratteristiche.setTextColor(ContextCompat.getColor(this, R.color.white));

                // Imposta il pulsante come cliccabile
                prenota.setClickable(true);
                prenota.setBackground(ContextCompat.getDrawable(this, R.drawable.std_button));

                // Imposta il campo selezionato
                Calendar dataSelezionata = Calendar.getInstance();
                dataSelezionata.set(Calendar.YEAR, Integer.parseInt(fakeDate.get(pos).substring(0, 4)));
                dataSelezionata.set(Calendar.MONTH, Integer.parseInt(fakeDate.get(pos).substring(5, 7)));
                dataSelezionata.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fakeDate.get(pos).substring(8, 10)));
                dataSelezionata.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fakeDate.get(pos).substring(11, 13)));
                dataSelezionata.set(Calendar.MINUTE, Integer.parseInt(fakeDate.get(pos).substring(14, 16)));
                partitaScelta = new Partita(dataSelezionata, campi.get(randC));
            });
        }
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

    private void addCommit(Partita partita, Group group) {
        ArrayList<Person> utentiRegistrati = recuperaUtentiRegistrati();

        for (Person p : utentiRegistrati) {
            for (Person g : group.getPartecipanti()) {
                if (p.getNomeUtente().equals(g.getNomeUtente()))
                    p.addImpegno(new Commit(partita.getData(), "Partita con " + group.getNomeGruppo(), partita.getData().get(Calendar.HOUR_OF_DAY) + ":00", (partita.getData().get(Calendar.HOUR_OF_DAY) + 1) + ":00", false));
            }
        }

        // Salva gli utenti registrati
        SharedPreferences sharedPreferences = getSharedPreferences("utentiRegistrati", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Converti utentiRegistrati in un array di byte utilizzando la serializzazione
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(utentiRegistrati);
            String datiUtentiRegistrati = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            // Salva la rappresentazione di byte come stringa nelle SharedPreferences
            editor.putString("arrayUtenti", datiUtentiRegistrati);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Salvo l'impegno anche dell'utente loggato
        user.addImpegno(new Commit(partita.getData(), "Partita con " + group.getNomeGruppo(), partita.getData().get(Calendar.HOUR_OF_DAY) + ":00", (partita.getData().get(Calendar.HOUR_OF_DAY) + 1) + ":00", false));

        // Salva l'utente loggato
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

    private void confirmMessage(String messaggio){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_succes,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView scritta = layout.findViewById(R.id.toast_text);
        scritta.setText(messaggio);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void trovaDisponibilita() {
        // Inizializza un oggetto Calendar per la data corrente
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);

        // Cicla attraverso i prossimin giorni per trovare una data disponibile per la prossima partita
        int giorniDisponibili;
        switch (gruppo.getFrequenzaPartite()) {
            case "Settimanale":
                giorniDisponibili = 7;
                break;
            case "Mensile":
                giorniDisponibili = 30;
                break;
            case "Bimestrale":
                giorniDisponibili = 60;
                break;
            case "Trimestrale":
                giorniDisponibili = 90;
                break;
        }

        for (int i = 0; i < giorniDisponibili; i++) {
            // Incrementa la data corrente di un giorno

            currentCalendar.add(Calendar.HOUR, 7);
            for (int j = 8; j <= 24; j++) { // si può prenotare dalle 08:00 alle 24:00
                // Incrementa l'ora corrente di un'ora
                currentCalendar.add(Calendar.HOUR, 1);

                // Verifica se l'ora corrente è disponibile
                boolean disponibile = true;
                for (Commit c : impegni) {
                    if (c.getData().get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && c.getData().get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) && c.getData().get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
                            && j >= (c.getOraInizio().charAt(0) == '0' ? Integer.parseInt(c.getOraInizio().substring(1, 2)) : Integer.parseInt(c.getOraInizio().substring(0, 2)))
                            && j <= (c.getOraFine().charAt(0) == '0' ? Integer.parseInt(c.getOraFine().substring(1, 2)) : Integer.parseInt(c.getOraFine().substring(0, 2))))
                    {
                        disponibile = false;
                        break;
                    }
                }
                if (disponibile) {
                    boolean disponibileC = true;
                    for (Campo campo : campi) {
                        for (Calendar p : campo.getPrenotazioni()) {
                            if ( p.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && p.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) && p.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)
                                    && j >= p.get(Calendar.HOUR_OF_DAY)
                                    && j <= p.get(Calendar.HOUR_OF_DAY) + 1
                            ) {
                                disponibileC = false;
                                System.out.println("sono qui");
                                break;
                            }
                        }
                        if (disponibileC) {
                            disponibilitaData.add(currentCalendar.get(Calendar.YEAR) + "-" + (currentCalendar.get(Calendar.MONTH) < 10 ? "0" + currentCalendar.get(Calendar.MONTH) : currentCalendar.get(Calendar.MONTH)) + "-" + currentCalendar.get(Calendar.DAY_OF_MONTH) + " " + (j < 10 ? "0" + j : j) + ":00");
                            disponibilitaCampo.add(campi.indexOf(campo));
                        }
                    }
                }
            }
        }
    }

    private void printDisponibilita() {
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout thisView = findViewById(R.id.container_campi);

        if (!disponibilitaData.isEmpty()) {
            TextView no = findViewById(R.id.no_campi);
            no.setVisibility(View.GONE);
        }

        ArrayList<View> campoViews = new ArrayList<>();

        int nVisualizzazioni = disponibilitaData.size() > 10 && !fullStampa ? 10 : disponibilitaData.size();
        for (int i = 0; i < nVisualizzazioni; i++) {
            View view = inflater.inflate(R.layout.campo, null);
            ImageView foto;
            TextView nome, data_ora_campo, caratteristiche, scrittaData, scrittaCaratteristiche;
            LinearLayout click;
            int rand = !fullStampa ? (int) (Math.random() * disponibilitaData.size()) : i;

            campoViews.add(view);

            foto = view.findViewById(R.id.foto_campo);
            nome = view.findViewById(R.id.nome_campo);
            data_ora_campo = view.findViewById(R.id.data_ora_campo);
            caratteristiche = view.findViewById(R.id.caratteristiche);
            scrittaData = view.findViewById(R.id.scrittaData);
            scrittaCaratteristiche = view.findViewById(R.id.scrittaCaratteristiche);
            click = view.findViewById(R.id.campo);

            foto.setImageDrawable(ContextCompat.getDrawable(this, campi.get(disponibilitaCampo.get(rand)).getImg()));
            nome.setText(campi.get(disponibilitaCampo.get(rand)).getNomeCampo());
            caratteristiche.setText(campi.get(disponibilitaCampo.get(rand)).getCaratteristiche());

            // Siccome i mesi vanno da 0 a 11 e non da 1 a 12, bisogna aggiungere 1 al mese
            int meseInt = Integer.parseInt(disponibilitaData.get(rand).substring(5, 7)) + 1;
            String mese = meseInt < 10 ? "0" + meseInt : String.valueOf(meseInt);
            String data = disponibilitaData.get(rand).substring(0, 5) + mese + disponibilitaData.get(rand).substring(7);
            data_ora_campo.setText(data);

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

            click.setOnClickListener(v -> {
                for (View campoView : campoViews) {
                    TextView nome2, data_ora_campo2, caratteristiche2, scrittaData2, scrittaCaratteristiche2;

                    nome2 = campoView.findViewById(R.id.nome_campo);
                    data_ora_campo2 = campoView.findViewById(R.id.data_ora_campo);
                    caratteristiche2 = campoView.findViewById(R.id.caratteristiche);
                    scrittaData2 = campoView.findViewById(R.id.scrittaData);
                    scrittaCaratteristiche2 = campoView.findViewById(R.id.scrittaCaratteristiche);

                    // Cambia il background di ciascuna vista
                    campoView.setBackground(ContextCompat.getDrawable(this, R.drawable.background_gruppo));
                    nome2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    data_ora_campo2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    caratteristiche2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    scrittaData2.setTextColor(ContextCompat.getColor(this, R.color.black));
                    scrittaCaratteristiche2.setTextColor(ContextCompat.getColor(this, R.color.black));
                }

                // Cambia il background dell'elemento cliccato
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.background_selected));
                nome.setTextColor(ContextCompat.getColor(this, R.color.white));
                data_ora_campo.setTextColor(ContextCompat.getColor(this, R.color.white));
                caratteristiche.setTextColor(ContextCompat.getColor(this, R.color.white));
                scrittaData.setTextColor(ContextCompat.getColor(this, R.color.white));
                scrittaCaratteristiche.setTextColor(ContextCompat.getColor(this, R.color.white));

                // Imposta il pulsante come cliccabile
                prenota.setClickable(true);
                prenota.setBackground(ContextCompat.getDrawable(this, R.drawable.std_button));

                // Imposta il campo selezionato
                Calendar dataSelezionata = Calendar.getInstance();
                dataSelezionata.set(Calendar.YEAR, Integer.parseInt(disponibilitaData.get(rand).substring(0, 4)));
                dataSelezionata.set(Calendar.MONTH, Integer.parseInt(disponibilitaData.get(rand).substring(5, 7)));
                dataSelezionata.set(Calendar.DAY_OF_MONTH, Integer.parseInt(disponibilitaData.get(rand).substring(8, 10)));
                dataSelezionata.set(Calendar.HOUR_OF_DAY, Integer.parseInt(disponibilitaData.get(rand).substring(11, 13)));
                dataSelezionata.set(Calendar.MINUTE, Integer.parseInt(disponibilitaData.get(rand).substring(14, 16)));
                partitaScelta = new Partita(dataSelezionata, campi.get(disponibilitaCampo.get(rand)));
                System.out.println(partitaScelta.getData().getTime());
            });
        }
    }
}