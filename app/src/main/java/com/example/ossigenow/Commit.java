package com.example.ossigenow;

import java.util.Calendar;

public class Commit {
    private Calendar data;
    private String nome;
    private String descrizione;
    private String oraInizio; // formato: "HH:MM"
    private String oraFine;

    public Commit(Calendar data, String nome, String descrizione, String oraInizio, String oraFine) {
        this.data = data;
        this.nome = nome;
        this.descrizione = descrizione;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
    }

    // Getters
    public Calendar getData() {return data;}
    public String getNome() {return nome;}
    public String getDescrizione() {return descrizione;}
    public String getOraInizio() {return oraInizio;}
    public String getOraFine() {return oraFine;}

    // Setters
    public void setData(Calendar data) {this.data = data;}
    public void setNome(String nome) {this.nome = nome;}
    public void setDescrizione(String descrizione) {this.descrizione = descrizione;}
    public void setOraInizio(String oraInizio) {this.oraInizio = oraInizio;}
    public void setOraFine(String oraFine) {this.oraFine = oraFine;}
}
