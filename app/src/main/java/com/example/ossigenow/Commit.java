package com.example.ossigenow;

import java.io.Serializable;
import java.util.Calendar;

public class Commit implements Serializable {
    private Calendar data;
    private String nome;
    private String oraInizio; // formato: "HH:MM"
    private String oraFine;

    public Commit(Calendar data, String nome, String oraInizio, String oraFine) {
        this.data = data;
        this.nome = nome;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
    }

    // Getters
    public Calendar getData() {return data;}
    public String getNome() {return nome;}
    public String getOraInizio() {return oraInizio;}
    public String getOraFine() {return oraFine;}

    // Setters
    public void setData(Calendar data) {this.data = data;}
    public void setNome(String nome) {this.nome = nome;}
    public void setOraInizio(String oraInizio) {this.oraInizio = oraInizio;}
    public void setOraFine(String oraFine) {this.oraFine = oraFine;}
}
