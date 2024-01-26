package com.example.ossigenow;

import java.io.Serializable;
public class Person implements Serializable {
    private String nomeUtente, nome, cognome, dataNascita, password;

    public Person() {
        super();
    }
    public Person(String nomeUtente, String nome, String cognome, String dataNascita, String password) {
        this.nomeUtente = nomeUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.password = password;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getPassword() {
        return password;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
