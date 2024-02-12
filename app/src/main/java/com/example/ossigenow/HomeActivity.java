package com.example.ossigenow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.applandeo.materialcalendarview.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    // Utente loggato
    public final static String PERSON_PATH = "com.example.ossigenow.person";
    public final static String GROUP_PATH = "com.example.ossigenow.groups";
    private Person user = null;
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Invito> inviti = new ArrayList<>();
    private boolean isLogout = false;

    // Elementi UI
    private ImageView navHome, navInvite, navCalendar, navBooking, navProfile;
    private int home, invite, calendar, booking, profile;
    private LinearLayout containerLayout;
    private LayoutInflater layoutInflater;
    private LinearLayout creaGruppo, logout;
    private ImageView view;
    private TextView nomeUtente, nome, cognome, dataNascita;
    private CalendarView calendarView;
    private CustomCalendarView customCalendarView;

    // Intento per cambiare activity
    private Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ripristina la sessione
        ripristinaSessione();

        // Inizializza gli elementi UI
        inizializzaUI();

        // Gestisce la navbar
        navbar(new int[]{home, invite, calendar, booking, profile}, new ImageView[]{navHome, navInvite, navCalendar, navBooking, navProfile});
    }

    @Override
    protected void onDestroy() {
        if (!isLogout) {
            // Salvo l'ultimo utente
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

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (!isLogout) {
            // Salvo l'ultimo utente
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

        super.onStop();
    }

    private void chiudiSessione() {
        // Elimina l'utente dalle SharedPreferences
        SharedPreferences sharedPreferencesUtente = getSharedPreferences("User", Context.MODE_PRIVATE);

        SharedPreferences.Editor editorUtente = sharedPreferencesUtente.edit();

        editorUtente.clear();
        editorUtente.apply();
    }

    private void chiediConfermaLogout(){
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alert_view = inflater.inflate(R.layout.dialogo_conferma, null);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(alert_view);

        String messaggio = "Sei sicuro di voler terminare la sessione?\n\n";
        TextView alert_dialog = alert_view.findViewById(R.id.completa_avviso);
        alert_dialog.setText(messaggio);

        Button conferma = alert_view.findViewById(R.id.conferma);
        Button annulla = alert_view.findViewById(R.id.annulla);

        dialog.getWindow().setBackgroundDrawableResource(R.color.trasparent);

        conferma.setOnClickListener(v -> {
            dialog.dismiss();
            isLogout = true;
            chiudiSessione();
            result = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(result);
            finish();
        });

        annulla.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void ripristinaSessione() {
        // Recupera i dati dell'utente loggato
        user = (Person) getIntent().getSerializableExtra(MainActivity.PERSON_PATH);

        if (user == null) {
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
        }

        // Recupera i gruppi dell'utente loggato
        SharedPreferences sharedPreferencesGruppi = getSharedPreferences("gruppi", Context.MODE_PRIVATE);

        String datiGruppi = sharedPreferencesGruppi.getString("chiave", "");

        if (!datiGruppi.isEmpty()) {
            byte[] datiGruppiBytes = Base64.decode(datiGruppi, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiGruppiBytes))) {
                groups = (ArrayList<Group>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Controlla se l'utente è presente nel gruppo altrimenti lo elimina
        if (groups != null) {
            groups.removeIf(g -> {
                for (Person p : g.getPartecipanti()) {
                    if (p.getNomeUtente().equals(user.getNomeUtente())) {
                        return false; // Non rimuovere il gruppo se l'utente è presente
                    }
                }
                return true; // Rimuovi il gruppo se l'utente non è presente
            });
        }

        // Recupera gli inviti dell'utente loggato
        String path = "inviti" + user.getNomeUtente();
        SharedPreferences sharedPreferencesInviti = getSharedPreferences(path, Context.MODE_PRIVATE);

        String datiInviti = sharedPreferencesInviti.getString("chiave", "");

        if (!datiInviti.isEmpty()) {
            byte[] datiInvitiBytes = Base64.decode(datiInviti, Base64.DEFAULT);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(datiInvitiBytes))) {
                inviti = (ArrayList<Invito>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void inizializzaUI() {
        navHome = findViewById(R.id.navHome);
        navInvite = findViewById(R.id.navInvite);
        navCalendar = findViewById(R.id.navCalendar);
        navBooking = findViewById(R.id.navBooking);
        navProfile = findViewById(R.id.navProfile);
        home = R.layout.home;
        invite = R.layout.invite;
        calendar = R.layout.calendar;
        booking = R.layout.booking;
        profile = R.layout.profile;
    }

    private void confListener(int layout) {
        if (layout == R.layout.home) {
            printGruppi();

            creaGruppo = findViewById(R.id.creaGruppo);

            creaGruppo.setOnClickListener(v -> {
                result = new Intent(HomeActivity.this, NewGroupActivity.class);
                result.putExtra(HomeActivity.PERSON_PATH, user);
                startActivity(result);
            });
        } else if (layout == R.layout.invite) {
            printInviti();
        } else if (layout == R.layout.calendar) {
            calendarView = findViewById(R.id.calendarView);

            Calendario.createCalendar(this, calendarView);
        } else if (layout == R.layout.booking) {
            // codice per la pagina delle prenotazioni
        } else if (layout == R.layout.profile) {
            logout = findViewById(R.id.logout);
            nomeUtente = findViewById(R.id.nome_utente);
            nome = findViewById(R.id.nome);
            cognome = findViewById(R.id.cognome);
            dataNascita = findViewById(R.id.data_nascita);

            nomeUtente.setText(user.getNomeUtente());
            nome.setText(user.getNome());
            cognome.setText(user.getCognome());
            dataNascita.setText(user.getDataNascita());

            logout.setOnClickListener(v -> {
                chiediConfermaLogout();
            });
        }
    }

    // Metodi per gestire la navbar
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void setNavItem(ImageView[] itemView, int position, boolean selected) {
//        int padding = selected ?  dpToPx(4) : dpToPx(10);
//        itemView[position].setPadding(padding, padding, padding, padding);

        itemView[position].setBackgroundResource(selected ? R.color.navbar_icon : R.color.navbar_icon_selected);
        if (selected) {
            if(position > 0) itemView[position-1].setBackgroundResource(R.drawable.background_nav_rigth);
            if(position < itemView.length-1) itemView[position+1].setBackgroundResource(R.drawable.background_nav_left);
        }

        itemView[position].setSelected(selected);
    }
    private void navbar(int[] layouts, ImageView[] navItems) {
        // Inizializza il layout
        containerLayout = findViewById(R.id.container);
        layoutInflater = LayoutInflater.from(this);

        // Di default è selezionata la schemata home
        setNavItem(navItems, 0, true);
        containerLayout.addView(layoutInflater.inflate(home, containerLayout, false));
        confListener(home);

        // Imposta il listener per ogni elemento di navigazione
        for (int i = 0; i < layouts.length; i++) {
            int index = i;
            int selectedLayout = layouts[i];
            ImageView navItem = navItems[i];

            navItem.setOnClickListener(v -> {
                // Imposta tutti gli elementi di navigazione come non selezionati
                for (int j = 0; j < navItems.length; j++) {
                    setNavItem(navItems, j,false);
                }
                // Imposta l'elemento di navigazione corrente come selezionato
                setNavItem(navItems, index,true);

                // Imposta il layout corrente
                containerLayout.removeAllViews(); // Rimuove il layout attuale, se presente
                containerLayout.addView(layoutInflater.inflate(selectedLayout, containerLayout, false));
                confListener(selectedLayout);

                // Se il layout è profile imposta gravity center
                if (selectedLayout == R.layout.profile || selectedLayout == R.layout.calendar)
                    containerLayout.setGravity(Gravity.CENTER);
                else
                    containerLayout.setGravity(Gravity.START);
            });
        }
    }

    private void printGruppi() {
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout thisView = findViewById(R.id.conteiner_gruppi);

        if (!groups.isEmpty()) {
            TextView no = findViewById(R.id.no_gruppi);
            no.setVisibility(View.GONE);
        }

        for (Group g : groups) {
            View view = inflater.inflate(R.layout.gruppo, null);
            TextView nome, partecipanti;
            ImageButton mostra;

            nome = view.findViewById(R.id.nome_gruppo);
            partecipanti = view.findViewById(R.id.partecipanti);
            mostra = view.findViewById(R.id.view);

            nome.setText(g.getNomeGruppo());
            String partecipantiText = "partecipanti :" + g.numberParticipant() + " / " + g.getNumPartecipanti();
            partecipanti.setText(partecipantiText);

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

            mostra.setOnClickListener(v -> {
                result = new Intent(HomeActivity.this, GroupActivity.class);
                result.putExtra(GROUP_PATH, g);
                result.putExtra(PERSON_PATH, user);
                startActivity(result);
            });
        }
    }

    private void printInviti() {
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout thisView = findViewById(R.id.conteiner_inviti);

        if (!inviti.isEmpty()) {
            TextView no = findViewById(R.id.no_inviti);
            no.setVisibility(View.GONE);
        }

        for (Invito i : inviti) {
            View view = inflater.inflate(R.layout.invito, null);
            TextView nome_gruppo, utente_invitante;
            ImageButton accetta, rifiuta;

            nome_gruppo = view.findViewById(R.id.nome_gruppo);
            utente_invitante = view.findViewById(R.id.nome_inviante);
            accetta = view.findViewById(R.id.accept);
            rifiuta = view.findViewById(R.id.decline);

            nome_gruppo.setText(i.getGroup().getNomeGruppo());
            String messaggio_invitante = i.getUtenteInvitante() + " ti ha invitato";
            utente_invitante.setText(messaggio_invitante);

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

            accetta.setOnClickListener(v -> {

            });

            rifiuta.setOnClickListener(v -> {

            });
        }
    }
}