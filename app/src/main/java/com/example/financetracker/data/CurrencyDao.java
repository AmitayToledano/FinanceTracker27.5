package com.example.financetracker.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financetracker.models.Currency;

import java.util.List;

@Dao
public interface CurrencyDao {

    @Query("SELECT * FROM currency")
    List<Currency> getAll();

    @Query("SELECT * FROM currency WHERE id = :id LIMIT 1")
    Currency getCurrencyById(int id);

    @Insert
    void insert(Currency currency);

    @Update
    void update(Currency currency);

    @Delete
    void delete(Currency currency);

}
