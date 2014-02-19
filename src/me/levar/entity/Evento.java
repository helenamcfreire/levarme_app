package me.levar.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 05/02/14
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class Evento implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eid);
        dest.writeString(nome);
        dest.writeString(date);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Evento> CREATOR = new Parcelable.Creator<Evento>() {
        public Evento createFromParcel(Parcel in) {
            return new Evento(in);
        }

        public Evento[] newArray(int size) {
            return new Evento[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Evento(Parcel in) {
        eid = in.readString();
        nome = in.readString();
        date = in.readString();
    }
}
