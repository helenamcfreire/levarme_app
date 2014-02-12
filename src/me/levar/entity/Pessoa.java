package me.levar.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Pessoa implements Comparable<Pessoa> {

    private String uid;
    private String nome;
    private String username;
    private String pic_square;
    private String cidade;
    private String pais;
    private String estado;
    private String sexo;
    private String aniversario;
    private String relationship_status;
    private boolean app_user = false;
    private List<Pessoa> amigosEmComum;
    private String mutual_friend_count;
    private String registrationId; //Usado pelo Google Cloud Messaging

    public Pessoa() {
    }

    public Pessoa(String uid, String nome, String registrationId) {
        this.uid = uid;
        this.nome = nome;
        this.registrationId = registrationId;
    }

    public Pessoa(String uid, String nome, String cidade, String pais, String estado, String sexo, String aniversario, String relationship_status) {
        this.uid = uid;
        this.nome = nome;
        this.cidade = cidade;
        this.pais = pais;
        this.estado = estado;
        this.sexo = sexo;
        this.aniversario = aniversario;
        this.relationship_status = relationship_status;
    }

    public Pessoa(String uid, String nome, String pic_square, boolean app_user, String mutual_friend_count) {
        this.uid = uid;
        this.nome = nome;
        this.pic_square = pic_square;
        this.app_user = app_user;
        this.mutual_friend_count = mutual_friend_count;
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

    public String getMutual_friend_count() {
        return mutual_friend_count;
    }

    public void setMutual_friend_count(String mutual_friend_count) {
        this.mutual_friend_count = mutual_friend_count;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getAniversario() {
        return aniversario;
    }

    public void setAniversario(String aniversario) {
        this.aniversario = aniversario;
    }

    public String getRelationship_status() {
        return relationship_status;
    }

    public void setRelationship_status(String relationship_status) {
        this.relationship_status = relationship_status;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @Override
    public int compareTo(Pessoa another) {
        return this.getNome().compareTo(another.getNome());
    }
}