package com.simonebakker.simone.addemup.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Game implements Serializable {

    private int mID;
    private String mName;
    private int mPoints;
    private int mProgress;
    private String mDate;

    public Game() {}

    public Game(int mID, int mPoints, int mProgress) {
        this.mID = mID;
        this.mPoints = mPoints;
        this.mProgress = mProgress;
        this.mName = "";
        setCurrentDate();
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmPoints() {
        return mPoints;
    }

    public void setmPoints(int mPoints) {
        this.mPoints = mPoints;
    }

    public int getmProgress() {
        return mProgress;
    }

    public void setmProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    // gets the current date and formats it to day/month/year hh:mm:ss
    public void setCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = Calendar.getInstance().getTime();
        mDate = dateFormat.format(date);
    }
}
