package com.example.application.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegData {

    @SerializedName("nazwa")
    public String nazwa;
    @SerializedName("haslo")
    public String haslo;
    @SerializedName("adres_urzadzenia")
    public String adresUrzadzenia;

    public RegData(String nazwa, String haslo, String adresUrzadzenia) {
        this.nazwa = nazwa;
        this.haslo = haslo;
        this.adresUrzadzenia = adresUrzadzenia;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getAdresUrzadzenia() {
        return adresUrzadzenia;
    }

    public void setAdresUrzadzenia(String adresUrzadzenia) {
        this.adresUrzadzenia = adresUrzadzenia;
    }
}
