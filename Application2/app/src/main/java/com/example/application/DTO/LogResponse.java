package com.example.application.DTO;

public class LogResponse {
    private String message;
    private int ID;

    public LogResponse(String messege) {
        this.message = messege;
    }

    public LogResponse(int ID) {
        this.ID = ID;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String messge) {
        this.message = message;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
