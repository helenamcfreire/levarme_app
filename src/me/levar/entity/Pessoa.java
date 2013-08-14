package me.levar.entity;

public class Pessoa {

    private String uid;
    private String nome;
    private String username;
    private String pic_square;
    private String qtdAmigosEmComum;
    private boolean selecionado = false;

    public Pessoa() {
    }

    public Pessoa(String uid, String nome) {
        this.uid = uid;
        this.nome = nome;
    }

    public Pessoa(String uid, String nome, String pic_square, String qtdAmigosEmComum) {
        this.uid = uid;
        this.nome = nome;
        this.pic_square = pic_square;
        this.qtdAmigosEmComum = qtdAmigosEmComum;
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

    public String getQtdAmigosEmComum() {
        return qtdAmigosEmComum;
    }

    public void setQtdAmigosEmComum(String qtdAmigosEmComum) {
        this.qtdAmigosEmComum = qtdAmigosEmComum;
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
}