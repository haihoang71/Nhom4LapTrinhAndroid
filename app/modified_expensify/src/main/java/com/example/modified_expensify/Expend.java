package com.example.modified_expensify;

import java.io.Serializable;

public class Expend implements Serializable {
    private String date;
    private String name;
    private float amount;
    private String type;
    private String id;
    private int syncStatus;
    private int localId;

    public Expend(){

    }
    public Expend(String date, String name, float amount, String type) {
        this.date = date;
        this.name = name;
        this.amount = amount;
        this.type = type;
    }
    public Expend(String id, String date, String name, float amount, String type) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.amount = amount;
        this.type = type;
    }
    public String getDate() {
        return date;
    }
    public String getName() {
        return name;
    }
    public float getAmount() {
        return amount;
    }
    public String getType() {
        return type;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }
}

