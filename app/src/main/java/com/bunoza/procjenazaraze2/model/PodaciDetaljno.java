package com.bunoza.procjenazaraze2.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PodaciDetaljno {

    @SerializedName("broj_zarazenih")
    @Expose
    private Integer brojZarazenih;
    @SerializedName("broj_umrlih")
    @Expose
    private Integer brojUmrlih;
    @SerializedName("broj_aktivni")
    @Expose
    private Integer brojAktivni;
    @SerializedName("Zupanija")
    @Expose
    private String zupanija;

    /**
     * No args constructor for use in serialization
     *
     */
    public PodaciDetaljno() {
    }

    /**
     *
     * @param brojUmrlih
     * @param brojZarazenih
     * @param brojAktivni
     * @param zupanija
     */
    public PodaciDetaljno(Integer brojZarazenih, Integer brojUmrlih, Integer brojAktivni, String zupanija) {
        super();
        this.brojZarazenih = brojZarazenih;
        this.brojUmrlih = brojUmrlih;
        this.brojAktivni = brojAktivni;
        this.zupanija = zupanija;
    }

    public Integer getBrojZarazenih() {
        return brojZarazenih;
    }

    public void setBrojZarazenih(Integer brojZarazenih) {
        this.brojZarazenih = brojZarazenih;
    }

    public Integer getBrojUmrlih() {
        return brojUmrlih;
    }

    public void setBrojUmrlih(Integer brojUmrlih) {
        this.brojUmrlih = brojUmrlih;
    }

    public Integer getBrojAktivni() {
        return brojAktivni;
    }

    public void setBrojAktivni(Integer brojAktivni) {
        this.brojAktivni = brojAktivni;
    }

    public String getZupanija() {
        return zupanija;
    }

    public void setZupanija(String zupanija) {
        this.zupanija = zupanija;
    }

    public PodaciDetaljno(List<PodaciDetaljno> covid1, List<PodaciDetaljno> covid2) {

    }

    @Override
    public String toString() {
        return "PodaciDetaljno{" +
                "brojZarazenih=" + brojZarazenih +
                ", brojUmrlih=" + brojUmrlih +
                ", brojAktivni=" + brojAktivni +
                ", zupanija='" + zupanija + '\'' +
                '}';
    }
}