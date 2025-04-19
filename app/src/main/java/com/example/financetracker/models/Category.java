package com.example.financetracker.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
