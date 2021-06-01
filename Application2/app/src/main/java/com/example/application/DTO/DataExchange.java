package com.example.application.DTO;

import com.example.application.Encryption;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataExchange {
    public String ID;
    public String DeviceModel;
    public String Date;
    public String signalStrength;

    public DataExchange(String id, String DeviceModel, String Signal){
        ID=id;
        this.DeviceModel=DeviceModel;
        Date date = new Date();
        Encryption encryptor=new Encryption();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");
        Date=encryptor.encrypt(formatter.format(date));
        this.signalStrength=Signal;

    }

    public DataExchange(String ID, String deviceModel, String date, String signalStrength) {
        this.ID = ID;
        DeviceModel = deviceModel;
        Date = date;
        this.signalStrength = signalStrength;
    }
}
