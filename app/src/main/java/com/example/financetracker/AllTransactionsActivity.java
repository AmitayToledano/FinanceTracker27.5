package com.example.financetracker;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AllTransactionsActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper dbHelper;
    List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transactions);

        listView = findViewById(R.id.transaction_list);
        dbHelper = new DatabaseHelper(this);

        transactions = dbHelper.getAllTransactions(); // implement this in your DB helper
        TransactionAdapter adapter = new TransactionAdapter(this, transactions, dbHelper);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        transactions.clear();
        transactions.addAll(dbHelper.getAllTransactions());
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }
}
