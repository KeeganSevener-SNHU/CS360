package com.zybooks.weighttracker;

// LogDocs is an object class for storing information related to the weight logs.
// This allows the app to use the data in more dynamic ways.
public class LogDocs {
    // Variables for the object.
    private long id;
    private float weight;
    private String date;
    private String measure;

    //Constructor for Log.
    LogDocs(long id, float weight, String date, String measure) {
        this.id = id;
        this.weight = weight;
        this.date = date;
        this.measure = measure;
    }

    // Getters for LogDocs objects.
    public long getId() {
        return id;
    }
    public float getWeight() {
        return weight;
    }
    public String getMeasure() {
        return measure;
    }
    public String getDate() {
        return date;
    }
}
