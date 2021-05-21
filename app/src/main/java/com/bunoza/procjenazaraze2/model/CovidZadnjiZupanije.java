package com.bunoza.procjenazaraze2.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CovidZadnjiZupanije {

    @SerializedName("Datum")
    @Expose
    private String datum;
    @SerializedName("PodaciDetaljno")
    @Expose
    private List<PodaciDetaljno> podaciDetaljno = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public CovidZadnjiZupanije() {
    }

    /**
     *
     * @param datum
     * @param podaciDetaljno
     */
    public CovidZadnjiZupanije(String datum, List<PodaciDetaljno> podaciDetaljno) {
        super();
        this.datum = datum;
        this.podaciDetaljno = podaciDetaljno;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public List<PodaciDetaljno> getPodaciDetaljno() {
        return podaciDetaljno;
    }

    public void setPodaciDetaljno(List<PodaciDetaljno> podaciDetaljno) {
        this.podaciDetaljno = podaciDetaljno;
    }

    public CovidZadnjiZupanije(List<CovidZadnjiZupanije> covid) {
        this.datum = covid.get(0).getDatum();
        this.podaciDetaljno = new ArrayList<>();
        List<PodaciDetaljno> temp0 = new ArrayList<>();
        List<PodaciDetaljno> temp1 = new ArrayList<>();
        temp0.addAll(covid.get(0).getPodaciDetaljno());
        temp1.addAll(covid.get(1).getPodaciDetaljno());
//        this.podaciDetaljno.addAll(covid.get(0).getPodaciDetaljno());
        int i;
        for(i = 0; i < covid.get(0).getPodaciDetaljno().size(); i++){
            PodaciDetaljno tempPodaci = new PodaciDetaljno(temp0.get(i).getBrojZarazenih() - temp1.get(i).getBrojZarazenih(),
                    temp0.get(i).getBrojUmrlih() - temp1.get(i).getBrojUmrlih(),
                    ((temp0.get(i).getBrojZarazenih() - temp1.get(i).getBrojZarazenih()) - (temp0.get(i).getBrojUmrlih() - temp1.get(i).getBrojUmrlih()) -
                            (temp0.get(i).getBrojAktivni()- temp1.get(i).getBrojAktivni())),
                    temp0.get(i).getZupanija()
                    );
            this.podaciDetaljno.add(tempPodaci);
        }
    }

    @Override
    public String toString() {
        return "CovidZadnjiZupanije{" +
                "datum='" + datum + '\'' +
                ", podaciDetaljno=" + podaciDetaljno +
                '}';
    }
}