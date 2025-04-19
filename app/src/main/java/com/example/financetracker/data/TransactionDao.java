package com.example.financetracker.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financetracker.models.Currency;
import com.example.financetracker.models.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions")
    List<Transaction> getAll();

    @Query("SELECT * FROM transactions WHERE date = :date")
    List<Transaction> getTransactionsByDate(int date);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    Transaction getById(int id);

    @Query("SELECT * FROM transactions WHERE groupId = :groupId")
    List<Transaction> getByGroupId(int groupId);


    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    Transaction getTransactionById(int id);

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
