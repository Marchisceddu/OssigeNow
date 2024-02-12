package com.example.ossigenow;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable{

    private String nomeGruppo, frequenzaPartite, prossima_partita;
    private int numPartecipanti;

    private ArrayList<Person> partecipanti = new ArrayList<>();


    public Group(String nomeGruppo, String frequenzaPartite, int numPartecipanti, Person p) {
        this.nomeGruppo = nomeGruppo;
        this.frequenzaPartite = frequenzaPartite;
        this.numPartecipanti = numPartecipanti;
        addPartecipante(p);
        prossima_partita = null;
    }

    public String getNomeGruppo() {
        return nomeGruppo;
    }

    public void setNomeGruppo(String nomeGruppo) {
        this.nomeGruppo = nomeGruppo;
    }

    public String getFrequenzaPartite() {
        return frequenzaPartite;
    }

    public void setFrequenzaPartite(String frequenzaPartite) {
        this.frequenzaPartite = frequenzaPartite;
    }

    public int getNumPartecipanti() {
        return numPartecipanti;
    }

    public void setNumPartecipanti(int numPartecipanti) {
        this.numPartecipanti = numPartecipanti;
    }

    public ArrayList<Person> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(ArrayList<Person> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public String getProssima_partita() {
        return prossima_partita;
    }

    public void setProssima_partita(String prossima_partita) {
        this.prossima_partita = prossima_partita;
    }

    public void addPartecipante(Person p) {
        // Aggiungi solo se la dimensione dell'array è inferiore a numPartecipanti
        if (partecipanti.size() < numPartecipanti) {
            this.partecipanti.add(p);
        } else {
            System.out.println("Il gruppo è già al completo, non è possibile aggiungere ulteriori partecipanti.");
        }
    }

    public int numberParticipant(){
        return partecipanti.size();
    }
}
