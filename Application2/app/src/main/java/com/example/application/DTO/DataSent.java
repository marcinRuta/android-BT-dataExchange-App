package com.example.application.DTO;

public class DataSent {
    public String sila_sygnalu;
    public String model_urzadzenia;
    public String id_nadawcy;
    public String id_odbiorcy;
    public String data;

    public DataSent(String sila_sygnalu, String model_urzadzenia, String id_nadawcy, String id_odbiorcy, String data) {
        this.sila_sygnalu = sila_sygnalu;
        this.model_urzadzenia = model_urzadzenia;
        this.id_nadawcy = id_nadawcy;
        this.id_odbiorcy = id_odbiorcy;
        this.data = data;
    }
}
