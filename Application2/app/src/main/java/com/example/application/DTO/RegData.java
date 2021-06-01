package com.example.application.DTO;

public class RegData {

    public String Nazwa;
    public String Haslo;
    public String Adres_urzadzenia;
    public RegData(String Nazwa, String Haslo, String Adres) {
        this.Nazwa = Nazwa;
        this.Haslo = Haslo;
        this.Adres_urzadzenia=Adres;
    }
}
