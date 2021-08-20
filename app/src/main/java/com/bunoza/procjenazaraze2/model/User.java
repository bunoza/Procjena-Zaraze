package com.bunoza.procjenazaraze2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Random;

@Entity
public class User {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "Ime")
    public String ime;

    @ColumnInfo(name = "Prezime")
    public String prezime;

    @ColumnInfo(name="Spol")
    public char spol;

    @ColumnInfo(name="Datum")
    public String datum;

    @ColumnInfo(name="Pusenje")
    public boolean pusenje;

    @ColumnInfo(name="Lijekovi")
    public boolean lijekovi;

    @ColumnInfo(name="Posao")
    public String posao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public char getSpol() {
        return spol;
    }

    public void setSpol(char spol) {
        this.spol = spol;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public Boolean getPusenje() {
        return pusenje;
    }

    public void setPusenje(Boolean pusenje) {
        this.pusenje = pusenje;
    }

    public Boolean getLijekovi() {
        return lijekovi;
    }

    public void setLijekovi(Boolean lijekovi) {
        this.lijekovi = lijekovi;
    }

    public String getPosao() {
        return posao;
    }

    public void setPosao(String posao) {
        this.posao = posao;
    }

    public User(String ime, String prezime, char spol, String datum, Boolean pusenje, Boolean lijekovi, String posao) {
        this.id = 8;
        this.ime = ime;
        this.prezime = prezime;
        this.spol = spol;
        this.datum = datum;
        this.pusenje = pusenje;
        this.lijekovi = lijekovi;
        this.posao = posao;
    }

    @Override
    public String toString() {
        return "User{" +
                "ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", spol=" + spol +
                ", datum='" + datum + '\'' +
                ", pusenje=" + pusenje +
                ", lijekovi=" + lijekovi +
                ", posao='" + posao + '\'' +
                '}';
    }
}
