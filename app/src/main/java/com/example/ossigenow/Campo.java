package com.example.ossigenow;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Campo implements Serializable {
    private String nomeCampo, caratteristiche;
    private int img;
    private ArrayList<Calendar> prenotazioni = new ArrayList<>();

    public Campo(String nomeCampo, String caratteristiche, int img) {
        this.nomeCampo = nomeCampo;
        this.caratteristiche = caratteristiche;
        this.img = img;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getCaratteristiche() {
        return caratteristiche;
    }

    public void setCaratteristiche(String caratteristiche) {
        this.caratteristiche = caratteristiche;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public ArrayList<Calendar> getPrenotazioni() {
        return prenotazioni;
    }

    public void addPrenotazione(Calendar prenotazione) {
        this.prenotazioni.add(prenotazione);
    }

    public void removePrenotazione(Calendar prenotazione) {
        this.prenotazioni.remove(prenotazione);
    }
}
