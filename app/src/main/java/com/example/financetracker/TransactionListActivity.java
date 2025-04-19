package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.adapters.TransactionAdapter;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Group;
import com.example.financetracker.models.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private List<Group> groupList = new ArrayList<>();

    private Spinner spinnerGroupFilter;
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Group selectedGroup = null; // null means "All"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        db = AppDatabase.getInstance(this);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar_transaction);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Transactions");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewTransactions = findViewById(R.id.recyclerViewTransaction);
        spinnerGroupFilter = findViewById(R.id.spinnerGroupFilter); // Add this Spinner to your XML
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);

        setupRecyclerView();
        loadGroups(); // Load groups and trigger loading transactions

        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionListActivity.this, AddEditTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(transactionList, transaction -> {
            Intent intent = new Intent(TransactionListActivity.this, AddEditTransactionActivity.class);
            intent.putExtra(AddEditTransactionActivity.EXTRA_TRANSACTION_ID, transaction.id);
            startActivity(intent);
        });
        recyclerViewTransactions.setAdapter(transactionAdapter);
    }

    private void loadGroups() {
        executorService.execute(() -> {
            List<Group> groups = db.groupDao().getAll();
            runOnUiThread(() -> {
                groupList.clear();
                groupList.add(null); // null = "All"
                groupList.addAll(groups);

                List<String> groupNames = new ArrayList<>();
                groupNames.add("All");
                for (Group group : groups) {
                    groupNames.add(group.name);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGroupFilter.setAdapter(adapter);

                spinnerGroupFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedGroup = groupList.get(position);
                        loadTransactions();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedGroup = null;
                        loadTransactions();
                    }
                });
            });
        });
    }

    private void loadTransactions() {
        executorService.execute(() -> {
            List<Transaction> loadedTransactions;
            if (selectedGroup == null) {
                loadedTransactions = db.transactionDao().getAll();
            } else {
                loadedTransactions = db.transactionDao().getByGroupId(selectedGroup.id);
            }

            runOnUiThread(() -> {
                transactionList.clear();
                transactionList.addAll(loadedTransactions);
                transactionAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions(); // Reload in case of changes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
