package com.example.financetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Currency;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to add a new currency or edit an existing one.
 */
public class AddEditCurrencyActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENCY_ID = "com.example.financetracker.EXTRA_CURRENCY_ID";
    private static final int DEFAULT_CURRENCY_ID = -1;

    private TextInputLayout textInputLayoutCurrencyName, textInputLayoutCurrencySymbol, textInputLayoutCurrencyRate;
    private TextInputEditText editTextCurrencyName, editTextCurrencySymbol, editTextCurrencyRate;
    private Button buttonSaveCurrency;
    private Toolbar toolbar;

    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int currencyId = DEFAULT_CURRENCY_ID;
    private Currency currentCurrency; // Holds the currency being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_currency);

        db = AppDatabase.getInstance(this);

        // Find UI elements
        toolbar = findViewById(R.id.toolbar_add_edit_currency);
        textInputLayoutCurrencyName = findViewById(R.id.textInputLayoutCurrencyName);
        editTextCurrencyName = findViewById(R.id.editTextCurrencyName);
        textInputLayoutCurrencySymbol = findViewById(R.id.textInputLayoutCurrencySymbol);
        editTextCurrencySymbol = findViewById(R.id.editTextCurrencySymbol);
        textInputLayoutCurrencyRate = findViewById(R.id.textInputLayoutCurrencyRate);
        editTextCurrencyRate = findViewById(R.id.editTextCurrencyRate);
        buttonSaveCurrency = findViewById(R.id.buttonSaveCurrency);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Check if editing
        if (getIntent().hasExtra(EXTRA_CURRENCY_ID)) {
            currencyId = getIntent().getIntExtra(EXTRA_CURRENCY_ID, DEFAULT_CURRENCY_ID);
        }

        // Set title and load data if editing
        if (currencyId != DEFAULT_CURRENCY_ID) {
            setTitle("R.string.edit_currency"); // Define in strings.xml
            loadCurrencyData();
        } else {
            setTitle(R.string.add_currency); // Define in strings.xml
        }

        buttonSaveCurrency.setOnClickListener(v -> saveCurrency());
    }

    /**
     * Loads the data for the currency being edited from the database.
     */
    private void loadCurrencyData() {
        executorService.execute(() -> {
            // IMPORTANT: You need to add a getCurrencyById(int id) method to your CurrencyDao
            currentCurrency = db.currencyDao().getCurrencyById(currencyId);

            if (currentCurrency != null) {
                runOnUiThread(() -> {
                    editTextCurrencyName.setText(currentCurrency.name);
                    editTextCurrencySymbol.setText(currentCurrency.symbol);
                    // Format rate for display, handle potential locale issues if needed
                    editTextCurrencyRate.setText(String.valueOf(currentCurrency.rate));
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "R.string.error_loading_currency", Toast.LENGTH_SHORT).show(); // Define in strings.xml
                    finish();
                });
            }
        });
    }

    /**
     * Validates input and saves the new or updated currency to the database.
     */
    private void saveCurrency() {
        String name = editTextCurrencyName.getText() != null ? editTextCurrencyName.getText().toString().trim() : "";
        String symbol = editTextCurrencySymbol.getText() != null ? editTextCurrencySymbol.getText().toString().trim() : "";
        String rateStr = editTextCurrencyRate.getText() != null ? editTextCurrencyRate.getText().toString().trim() : "";
        double rate = 0.0;

        // Validate Name
        if (TextUtils.isEmpty(name)) {
            textInputLayoutCurrencyName.setError("getString(R.string.error_currency_name_required)"); // Define in strings.xml
            editTextCurrencyName.requestFocus();
            return;
        } else {
            textInputLayoutCurrencyName.setError(null);
        }

        // Validate Symbol
        if (TextUtils.isEmpty(symbol)) {
            textInputLayoutCurrencySymbol.setError("getString(R.string.error_currency_symbol_required)"); // Define in strings.xml
            // Don't necessarily request focus here if name is also empty
            if (!TextUtils.isEmpty(name)) editTextCurrencySymbol.requestFocus();
            return;
        } else {
            textInputLayoutCurrencySymbol.setError(null);
        }

        // Validate Rate
        if (TextUtils.isEmpty(rateStr)) {
            textInputLayoutCurrencyRate.setError("getString(R.string.error_currency_rate_required)"); // Define in strings.xml
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(symbol)) editTextCurrencyRate.requestFocus();
            return;
        } else {
            try {
                rate = Double.parseDouble(rateStr);
                if (rate <= 0) { // Rate should likely be positive
                    textInputLayoutCurrencyRate.setError("getString(R.string.error_currency_rate_positive)"); // Define in strings.xml
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(symbol)) editTextCurrencyRate.requestFocus();
                    return;
                }
                textInputLayoutCurrencyRate.setError(null); // Clear error on success
            } catch (NumberFormatException e) {
                textInputLayoutCurrencyRate.setError("getString(R.string.error_currency_rate_invalid)"); // Define in strings.xml
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(symbol)) editTextCurrencyRate.requestFocus();
                return;
            }
        }


        // Perform database operation on background thread
        double finalRate = rate; // Need effectively final variable for lambda
        executorService.execute(() -> {
            if (currencyId == DEFAULT_CURRENCY_ID) {
                // Add new currency
                Currency newCurrency = new Currency();
                newCurrency.name = name;
                newCurrency.symbol = symbol;
                newCurrency.rate = finalRate;
                db.currencyDao().insert(newCurrency);
                runOnUiThread(() -> Toast.makeText(this, "R.string.currency_saved", Toast.LENGTH_SHORT).show()); // Define in strings.xml
            } else {
                // Update existing currency
                if(currentCurrency != null) {
                    currentCurrency.name = name;
                    currentCurrency.symbol = symbol;
                    currentCurrency.rate = finalRate;
                    db.currencyDao().update(currentCurrency);
                    runOnUiThread(() -> Toast.makeText(this, "R.string.currency_updated", Toast.LENGTH_SHORT).show()); // Define in strings.xml
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "R.string.error_updating_currency", Toast.LENGTH_SHORT).show()); // Define in strings.xml
                }
            }
            runOnUiThread(this::finish);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}

/*
 * ============================================================
 * NOTE: Add this method signature to your CurrencyDao interface
 * ============================================================
 *
 * @Dao
 * public interface CurrencyDao {
 * // ... other methods (getAll, insert, update, delete)
 *
 * @Query("SELECT * FROM currency WHERE id = :id LIMIT 1")
 * Currency getCurrencyById(int id);
 * }
 *
 */
