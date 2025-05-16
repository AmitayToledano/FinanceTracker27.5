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

    @Query("SELECT SUM(amount) FROM transactions WHERE groupId = :groupId AND amount < 0")
    double getTotalSpendingForGroup(long groupId);

    @Query("SELECT SUM(amount) FROM transactions WHERE groupId = :groupId AND amount > 0")
    double getTotalIncomeForGroup(long groupId);

    @Query("SELECT SUM(amount) FROM transactions WHERE amount < 0 AND strftime('%Y-%m', date / 1000, 'unixepoch') = strftime('%Y-%m', 'now')")
    double getTotalSpendingForCurrentMonth();

    @Query("SELECT SUM(amount) FROM transactions WHERE amount < 0 AND groupId = :groupId AND strftime('%Y-%m', date / 1000, 'unixepoch') = strftime('%Y-%m', 'now')")
    double getTotalMonthlySpendingForGroup(long groupId);

    @Query("SELECT * FROM transactions LIMIT 10")
    List<Transaction> getSampleTransactions();



}
