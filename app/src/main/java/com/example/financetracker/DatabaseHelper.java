package com.example.financetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "finance.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_TRANSACTIONS = "transactions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "amount REAL,"
                + "date INTEGER"
                + ")";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade logic
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public double getCurrentBalance() {
        double balance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT SUM(amount) FROM " + TABLE_TRANSACTIONS;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }
        cursor.close();
        return balance;
    }

    public void addTransaction(String title, double amount) {
        String sql = "INSERT INTO " + TABLE_TRANSACTIONS + " (title, amount) VALUES (?, ?)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql, new String[]{title, String.valueOf(amount)});
        db.close();
    }



    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                double amount = cursor.getDouble(2);
                long date = cursor.getInt(3);
                Transaction transaction = new Transaction(
                        id, title, amount, date
                );
                list.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, "id = ?", new String[]{String.valueOf(id)});
        db.close();

    }

}