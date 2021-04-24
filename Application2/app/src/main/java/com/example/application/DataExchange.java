package com.example.application;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataExchange {
    public String ID;
    public String DeviceModel;
    public String Date;
    public int signalStrength;

    DataExchange(String id, String DeviceModel, int Signal){
        ID=id;
        this.DeviceModel=DeviceModel;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");
        Date=formatter.format(date);
        this.signalStrength=Signal;

    }
}
