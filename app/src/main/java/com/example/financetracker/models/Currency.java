package com.example.financetracker.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "currency")
public class Currency {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double rate;
    public String symbol;   // e.g., "$"

    @Override
    public String toString() {
        return name + "(" + symbol + ")";
    }

}
