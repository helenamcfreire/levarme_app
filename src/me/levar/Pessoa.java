package me.levar;

public class Pessoa {

    private String uid;
    private String nome;
    private String pic_square;

    public Pessoa() {
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
}