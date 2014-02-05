package me.levar.activity;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 05/02/14
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class Evento {

    private String eid;
    private String nome;
    private String date;

    public Evento(String eid, String nome, String date) {
        this.eid = eid;
        this.nome = nome;
        this.date = date;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
