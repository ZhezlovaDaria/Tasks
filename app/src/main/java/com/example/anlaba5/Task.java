package com.example.anlaba5;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Calendar;

public class Task implements Serializable {
    public String title;
    public String desc;
    public Calendar dateAndTime = Calendar.getInstance();
    public int prior;
    public int priorColor;
    public boolean notif=false;

    public Task(String title, String desc, Calendar dateAndTime, int prior, boolean notif) {
        this.title = title;
        this.desc = desc;
        this.dateAndTime = dateAndTime;
        this.prior = prior;
        this.notif=notif;
        setColor();
    }
    public String getTitle(){return title;}
    public String getDesc(){return title;}
    public Calendar getDateAndTime() {
        return dateAndTime;
    }
    public int getPrior(){return prior;}
    public int getPriorColor(){return priorColor;}
    public boolean getNotif(){return notif;}
    private void setColor() {
        switch(prior) {
            case 1:
                priorColor=Color.GRAY;
                break;
            case 2:
                priorColor=Color.BLUE;
                break;
            case 3:
                priorColor=Color.GREEN;
                break;
            case 4:
                priorColor=Color.YELLOW;
                break;
            case 5:
                priorColor=Color.RED;
                break;
        }
    }
}