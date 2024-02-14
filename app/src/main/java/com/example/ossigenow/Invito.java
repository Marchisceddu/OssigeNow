package com.example.ossigenow;

import java.io.Serializable;

public class Invito implements Serializable {
    private Group group;
    private String utenteInvitato;
    private String utenteInvitante;

    public Invito(Group group, String utenteInvitato, String utenteInvitante) {
        this.group = group;
        this.utenteInvitato = utenteInvitato;
        this.utenteInvitante = utenteInvitante;
    }

    public Group getGroup() {
        return group;
    }

    public String getUtenteInvitato() {
        return utenteInvitato;
    }

    public String getUtenteInvitante() {
        return utenteInvitante;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setUtenteInvitato(String utenteInvitato) {
        this.utenteInvitato = utenteInvitato;
    }
}
