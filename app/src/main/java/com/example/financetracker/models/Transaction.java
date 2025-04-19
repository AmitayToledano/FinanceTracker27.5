package com.example.financetracker.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
@Entity(
        tableName = "transactions",
        foreignKeys = {
            @ForeignKey(
                entity = Currency.class,
                parentColumns = "id",
                childColumns = "currencyId",
                onDelete = ForeignKey.CASCADE
            ),
                @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                entity = Group.class,
                parentColumns = "id",
                childColumns = "groupId",
                onDelete = ForeignKey.CASCADE)
        }
)public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;

    public double amount;
    public int date;
    public int currencyId;  // This references Currency.id
    public int categoryId;  // This references Category.id
    public int groupId;  // This references Group.id

}
