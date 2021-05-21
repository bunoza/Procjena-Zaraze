package com.bunoza.procjenazaraze2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "covid")
public class CovidDB {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "hr_zarazeni")
    public int hr_zarazeni;

    @ColumnInfo(name = "hr_izlijeceni")
    public int hr_izlijeceni;

    @ColumnInfo(name = "hr_umrli")
    public int hr_umrli;

    @ColumnInfo(name = "zup_zarazeni")
    public int zup_zarazeni;

    @ColumnInfo(name = "zup_izlijeceni")
    public int zup_izlijeceni;

    @ColumnInfo(name = "zup_umrli")
    public int zup_umrli;

    public CovidDB(int hr_zarazeni, int hr_izlijeceni, int hr_umrli, int zup_zarazeni, int zup_izlijeceni, int zup_umrli) {
        this.id = 0;
        this.hr_zarazeni = hr_zarazeni;
        this.hr_izlijeceni = hr_izlijeceni;
        this.hr_umrli = hr_umrli;
        this.zup_zarazeni = zup_zarazeni;
        this.zup_izlijeceni = zup_izlijeceni;
        this.zup_umrli = zup_umrli;
    }
}
