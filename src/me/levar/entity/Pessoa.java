package me.levar.entity;

import java.util.List;

public class Pessoa implements Comparable<Pessoa> {

    private String uid;
    private String nome;
    private String username;
    private String pic_square;
    private boolean selecionado = false;
    private List<Pessoa> amigosEmComum;

    public Pessoa() {
    }

    public Pessoa(String uid, String nome) {
        this.uid = uid;
        this.nome = nome;
    }

    public Pessoa(String uid, String nome, String pic_square) {
        this.uid = uid;
        this.nome = nome;
        this.pic_square = pic_square;
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

    public String getPic_square() {
        return pic_square;
    }

    public void setPic_square(String pic_square) {
        this.pic_square = pic_square;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
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