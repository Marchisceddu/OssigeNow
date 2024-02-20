package com.example.ossigenow;

import java.io.Serializable;
import java.util.Calendar;

public class Partita implements Serializable{
    private Calendar data;
    private Campo campo;

    public Partita(Calendar data, Campo campo) {
        this.data = data;
        this.campo = campo;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public Campo getCampo() {
        return campo;
    }

    public void setCampo(Campo campo) {
        this.campo = campo;
    }
}
