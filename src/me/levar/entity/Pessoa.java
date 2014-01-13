package me.levar.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Pessoa implements Comparable<Pessoa> {

    private String uid;
    private String nome;
    private String username;
    private String pic_square;
    private boolean app_user = false;
    private List<Pessoa> amigosEmComum;

    public Pessoa() {
    }

    public Pessoa(String uid, String nome) {
        this.uid = uid;
        this.nome = nome;
    }

    public Pessoa(String uid, String nome, String pic_square, boolean app_user) {
        this.uid = uid;
        this.nome = nome;
        this.pic_square = pic_square;
        this.app_user = app_user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPrimeiroNome() {
        return StringUtils.split(nome, " ")[0];
    }

    public String getPic_square() {
        return pic_square;
    }

    public void setPic_square(String pic_square) {
        this.pic_square = pic_square;
    }

    public boolean isApp_user() {
        return app_user;
    }

    public void setApp_user(boolean app_user) {
        this.app_user = app_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Pessoa> getAmigosEmComum() {
        return amigosEmComum;
    }

    public void setAmigosEmComum(List<Pessoa> amigosEmComum) {
        this.amigosEmComum = amigosEmComum;
    }

    @Override
    public int compareTo(Pessoa another) {
        return this.getNome().compareTo(another.getNome());
    }
}