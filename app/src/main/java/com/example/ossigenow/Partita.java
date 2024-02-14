package com.example.ossigenow;

import java.io.Serializable;
import java.util.Calendar;

public class Partita implements Serializable{
    private Calendar data;
    private String ora;
    private String campo;

    public Partita(Calendar data, String ora, String campo) {
        this.data = data;
        this.ora = ora;
        this.campo = campo;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }
}
