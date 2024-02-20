package com.example.ossigenow;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable{

    private String nomeGruppo, frequenzaPartite;
    private Partita prossima_partita;
    private int numPartecipanti;
    private Person admin;
    private ArrayList<Person> partecipanti = new ArrayList<>();

    public Group(String nomeGruppo, Person admin, String frequenzaPartite, int numPartecipanti, Person p) {
        this.nomeGruppo = nomeGruppo;
        this.frequenzaPartite = frequenzaPartite;
        this.numPartecipanti = numPartecipanti;
        this.admin = admin;
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

    public int getPartecipantiRichiesti() {
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

    public Partita getProssima_partita() {
        return prossima_partita;
    }

    public void setProssima_partita(Partita prossima_partita) {
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

    public int getNumberParticipanti(){
        return partecipanti.size();
    }

    public void removePartecipante(Person p){
        this.partecipanti.remove(p);
    }

    public Person getAdmin() {
        return admin;
    }

    public void removePartecipante_by_UsernName(String userName){
        for (Person p : partecipanti){
            if(p.getNomeUtente().equals(userName)){
                this.removePartecipante(p);
            }
        }
    }
}
