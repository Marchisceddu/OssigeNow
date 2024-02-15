package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private Partita partitaScelta;

    // Elementi UI
    private ImageButton back;
    private Button prenota;

    // Elenco di campi disponibili
    private ArrayList<Campo> campi = new ArrayList<>();

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

        // Inizializza l'elenco di campi disponibili
        campi.add(new Campo("Campo 1", "erba sintetica", R.drawable.campo1));
        campi.add(new Campo("Campo 2", "erba naturale", R.drawable.campo2));
            campi.add(new Campo("Campo 3", "erba sintetica", R.drawable.campo3));
        campi.add(new Campo("Campo 4", "erba naturale", R.drawable.campo4));

        ArrayList<String> fakeDate = new ArrayList<>();
        fakeDate.add("2021-06-01 14:00");
        fakeDate.add("2021-06-01 15:00");
        fakeDate.add("2021-06-01 16:00");
        fakeDate.add("2021-06-01 17:00");
        fakeDate.add("2021-06-01 18:00");
        fakeDate.add("2021-06-01 19:00");
        fakeDate.add("2021-06-01 20:00");
        fakeDate.add("2021-06-01 21:00");
        fakeDate.add("2021-06-01 22:00");
        fakeDate.add("2021-06-01 23:00");

        printFakeCampi(fakeDate);

        prenota.setOnClickListener(v -> {
            gruppo.setProssima_partita(partitaScelta);
            saveGroups(gruppo);

            // confirm message

            result = new Intent(BookingActivity.this, HomeActivity.class);
            startActivity(result);
            finish();
        });

        // Torna indietro all'activity precedente
        back.setOnClickListener(v -> {
            result = new Intent(BookingActivity.this, HomeActivity.class);
            startActivity(result);
            finish();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(BookingActivity.this, HomeActivity.class);
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
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

    private void parteFunzionanteNONHOVOGLIADIFINIRLO(){
        // Inizializza l'elenco di impegni dei partecipanti
        for (Person p : gruppo.getPartecipanti()) {
            impegni.addAll(p.getCommits());
        }

        // Trova tutti le ore disponibili
        // Inizializza un oggetto Calendar per la data corrente
        Calendar currentCalendar = Calendar.getInstance();

        // Trova il giorno della settimana corrente
        int dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);

        // Cicla attraverso i prossimi n giorni per trovare una data disponibile per la prossima partita
        int giorniDisponibili = 7;
        for (int i = 0; i < giorniDisponibili; i++) {
            // Incrementa la data corrente di un giorno
            currentCalendar.add(Calendar.DAY_OF_MONTH, 1);

            currentCalendar.add(Calendar.HOUR, 7);
            for (int j = 8; j < 24; j++) { // si può prenotare dalle 08:00 alle 24:00
                // Incrementa l'ora corrente di un'ora
                currentCalendar.add(Calendar.HOUR, 1);

                // Verifica se l'ora corrente è disponibile
                boolean disponibile = true;
                for (Commit c : impegni) {
                    if (c.getData().equals(currentCalendar)
                            && j > (c.getOraInizio().charAt(0) == '0' ? Integer.parseInt(c.getOraInizio().substring(1, 2)) : Integer.parseInt(c.getOraInizio().substring(0, 2)))
                            && j < (c.getOraFine().charAt(0) == '0' ? Integer.parseInt(c.getOraFine().substring(1, 2)) : Integer.parseInt(c.getOraFine().substring(0, 2)))
                    ) {
                        disponibile = false;
                        break;
                    }
                }
                if (disponibile) {
                    // Aggiungi l'ora corrente come disponibile
                }
            }
        }

        int maxDatePrenotabili = 10;
        for (int i = 0; i < 10; i++) {

        }
    }
}