package com.example.ossigenow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    private Group group;

    private TextView prova;
    public final static String GROUP_PATH = "GROUP";

    public ArrayList<Group> existing_group = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group = (Group) getIntent().getSerializableExtra(NewGroupActivity.NEW_GROUP_PATH);

        prova = findViewById(R.id.prova);

        String s = "";
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

        Person p2 = new Person();
        p2.setNome("esempio");
        p2.setCognome("esempio");
        p2.setNomeUtente("esempio");
        p2.setPassword("esempio");
        p2.setDataNascita("esempio");


        Group g1 = existing_group.get(0);
        g1.addPartecipante(p2);
        for( Group g : existing_group){

            s = s + " " + g.getNomeGruppo() + " " + g.getFrequenzaPartite() + " " +g.getNumPartecipanti()+ " " +g.getProssima_partita()+"\n";
            for (Person p : g.getPartecipanti() ){
                s = s +" "+ p.getNome() +" "+ p.getCognome()+" "+ p.getNomeUtente()+ " "+ p.getDataNascita() +" "+ p.getPassword()+"\n\n";
            }
        }


        prova.setText(s);
    }
}