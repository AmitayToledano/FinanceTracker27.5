package com.example.financetracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.financetracker.models.Transaction;
import com.example.financetracker.models.Category;
import com.example.financetracker.models.Group;
import com.example.financetracker.models.Currency;

@Database(entities = {Transaction.class, Category.class, Group.class, Currency.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract TransactionDao transactionDao();
    public abstract CurrencyDao currencyDao();
    public abstract CategoryDao categoryDao();
    public abstract GroupDao groupDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "finance_db"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
