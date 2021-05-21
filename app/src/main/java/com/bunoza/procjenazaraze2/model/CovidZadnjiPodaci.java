package com.bunoza.procjenazaraze2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CovidZadnjiPodaci {

    @SerializedName("SlucajeviSvijet")
    @Expose
    private Integer slucajeviSvijet;
    @SerializedName("SlucajeviHrvatska")
    @Expose
    private Integer slucajeviHrvatska;
    @SerializedName("UmrliSvijet")
    @Expose
    private Integer umrliSvijet;
    @SerializedName("UmrliHrvatska")
    @Expose
    private Integer umrliHrvatska;
    @SerializedName("IzlijeceniSvijet")
    @Expose
    private Integer izlijeceniSvijet;
    @SerializedName("IzlijeceniHrvatska")
    @Expose
    private Integer izlijeceniHrvatska;
    @SerializedName("Datum")
    @Expose
    private String datum;

    public Integer getSlucajeviSvijet() {
        return slucajeviSvijet;
    }

    public void setSlucajeviSvijet(Integer slucajeviSvijet) {
        this.slucajeviSvijet = slucajeviSvijet;
    }

    public Integer getSlucajeviHrvatska() {
        return slucajeviHrvatska;
    }

    public void setSlucajeviHrvatska(Integer slucajeviHrvatska) {
        this.slucajeviHrvatska = slucajeviHrvatska;
    }

    public Integer getUmrliSvijet() {
        return umrliSvijet;
    }

    public void setUmrliSvijet(Integer umrliSvijet) {
        this.umrliSvijet = umrliSvijet;
    }

    public Integer getUmrliHrvatska() {
        return umrliHrvatska;
    }

    public void setUmrliHrvatska(Integer umrliHrvatska) {
        this.umrliHrvatska = umrliHrvatska;
    }

    public Integer getIzlijeceniSvijet() {
        return izlijeceniSvijet;
    }

    public void setIzlijeceniSvijet(Integer izlijeceniSvijet) {
        this.izlijeceniSvijet = izlijeceniSvijet;
    }

    public Integer getIzlijeceniHrvatska() {
        return izlijeceniHrvatska;
    }

    public void setIzlijeceniHrvatska(Integer izlijeceniHrvatska) {
        this.izlijeceniHrvatska = izlijeceniHrvatska;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    @Override
    public String toString() {
        return "CovidZadnjiPodaci{" +
                "SlucajeviHrvatska=" + slucajeviHrvatska +
                ", UmrliHrvatska=" + umrliHrvatska +
                ", IzlijeceniHrvatska=" + izlijeceniHrvatska +
                '}';
    }

    public CovidZadnjiPodaci(CovidZadnjiPodaci covid) {
        this.datum = covid.datum;
        this.izlijeceniHrvatska = covid.izlijeceniHrvatska;
        this.izlijeceniSvijet = covid.izlijeceniSvijet;
        this.slucajeviHrvatska = covid.slucajeviHrvatska;
        this.slucajeviSvijet = covid.slucajeviSvijet;
        this.umrliHrvatska = covid.umrliHrvatska;
        this.umrliSvijet = covid.umrliSvijet;
    }
    public  CovidZadnjiPodaci(List<CovidZadnjiPodaci> covid) {
        this.datum = covid.get(0).getDatum();
        this.izlijeceniHrvatska = covid.get(0).getIzlijeceniHrvatska() - covid.get(1).getIzlijeceniHrvatska();
        this.izlijeceniSvijet = covid.get(0).getIzlijeceniSvijet() - covid.get(1).getIzlijeceniSvijet();
        this.slucajeviHrvatska = covid.get(0).getSlucajeviHrvatska() - covid.get(1).getSlucajeviHrvatska();
        this.slucajeviSvijet = covid.get(0).getSlucajeviSvijet() - covid.get(1).getSlucajeviSvijet();
        this.umrliHrvatska = covid.get(0).getUmrliHrvatska() - covid.get(1).getUmrliHrvatska();
        this.umrliSvijet = covid.get(0).getUmrliSvijet() - covid.get(1).getUmrliSvijet();
    }
}
