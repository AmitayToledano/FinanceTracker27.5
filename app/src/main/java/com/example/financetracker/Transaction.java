package com.example.financetracker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    public int id;
    public String title;
    public double amount;
    public long date;

    public Transaction(int id, String title, double amount, long date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date netDate = (new Date(date));
        return sdf.format(netDate);

    }
}