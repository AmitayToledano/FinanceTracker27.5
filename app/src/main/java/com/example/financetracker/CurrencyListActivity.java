package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// Assuming you will create a CurrencyAdapter
// import com.example.financetracker.adapters.CurrencyAdapter;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Currency;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.financetracker.adapters.CurrencyAdapter;
/**
 * Activity to display a list of currencies.
 */
public class CurrencyListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCurrencies;
     private CurrencyAdapter currencyAdapter; // Uncomment when adapter is created
    private List<Currency> currencyList = new ArrayList<>();
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        db = AppDatabase.getInstance(this);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar_currencies);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Currencies"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Find UI elements
        recyclerViewCurrencies = findViewById(R.id.recyclerViewCurrencies);
        FloatingActionButton fabAddCurrency = findViewById(R.id.fabAddCurrency);

        setupRecyclerView();
        loadCurrencies();

        fabAddCurrency.setOnClickListener(v -> {
            Intent intent = new Intent(CurrencyListActivity.this, AddEditCurrencyActivity.class);
            startActivity(intent);
        });

        // Item click listeners for editing/deleting go in the adapter
    }

    private void setupRecyclerView() {
        recyclerViewCurrencies.setLayoutManager(new LinearLayoutManager(this));
         currencyAdapter = new CurrencyAdapter(currencyList, currency -> {
             // Handle item click: Launch AddEditCurrencyActivity for editing
             Intent intent = new Intent(CurrencyListActivity.this, AddEditCurrencyActivity.class);
             intent.putExtra(AddEditCurrencyActivity.EXTRA_CURRENCY_ID, currency.id);
             startActivity(intent);
         });
         recyclerViewCurrencies.setAdapter(currencyAdapter);
    }

    private void loadCurrencies() {
        executorService.execute(() -> {
            List<Currency> loadedCurrencies = db.currencyDao().getAll();
            runOnUiThread(() -> {
                currencyList.clear();
                currencyList.addAll(loadedCurrencies);
                 if (currencyAdapter != null) { // Uncomment when adapter is created
                     currencyAdapter.notifyDataSetChanged();
                 }
                System.out.println("Loaded " + currencyList.size() + " currencies.");
                if (currencyList.isEmpty()) { /* Show empty state */ }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrencies(); // Refresh list
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
