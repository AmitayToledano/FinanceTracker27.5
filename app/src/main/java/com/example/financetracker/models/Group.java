package com.example.financetracker.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "group_")
public class Group {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }


}
