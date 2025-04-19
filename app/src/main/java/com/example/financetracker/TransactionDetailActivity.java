package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Category;
import com.example.financetracker.models.Currency;
import com.example.financetracker.models.Group;
import com.example.financetracker.models.Transaction;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date; // Ensure date is stored appropriately (e.g., Long timestamp)
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Activity to display the details of a single transaction.
 * Allows editing and deleting the transaction.
 */
public class TransactionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION_ID = "com.example.financetracker.EXTRA_TRANSACTION_ID";
    private static final int DEFAULT_TRANSACTION_ID = -1;

    private TextView textViewDetailTitle, textViewDetailAmount, textViewDetailDate;
    private TextView textViewDetailCategory, textViewDetailGroup, textViewDetailDescription;
    private Toolbar toolbar;

    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int transactionId = DEFAULT_TRANSACTION_ID;
    private Transaction currentTransaction; // Holds the displayed transaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        db = AppDatabase.getInstance(this);

        // Find UI elements
        toolbar = findViewById(R.id.toolbar_transaction_detail);
        textViewDetailTitle = findViewById(R.id.textViewDetailTitle);
        textViewDetailAmount = findViewById(R.id.textViewDetailAmount);
        textViewDetailDate = findViewById(R.id.textViewDetailDate);
        textViewDetailCategory = findViewById(R.id.textViewDetailCategory);
        textViewDetailGroup = findViewById(R.id.textViewDetailGroup);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("R.string.transaction_details"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get transaction ID from intent
        if (getIntent().hasExtra(EXTRA_TRANSACTION_ID)) {
            transactionId = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);
        }

        // Load details if ID is valid
        if (transactionId != DEFAULT_TRANSACTION_ID) {
            loadTransactionDetails();
        } else {
            // Handle invalid ID case
            Toast.makeText(this, "R.string.error_invalid_transaction_id", Toast.LENGTH_SHORT).show(); // Define in strings.xml
            finish();
        }
    }

    /**
     * Loads transaction details and related data (Category, Group, Currency)
     * from the database in a background thread.
     */
    private void loadTransactionDetails() {
        executorService.execute(() -> {
            // Fetch transaction
            // IMPORTANT: Requires getTransactionById(int id) in TransactionDao
            currentTransaction = db.transactionDao().getTransactionById(transactionId);

            if (currentTransaction != null) {
                // Fetch related data (requires getXById methods in respective DAOs)
                Category category = db.categoryDao().getCategoryById(currentTransaction.categoryId);
                Group group = db.groupDao().getGroupById(currentTransaction.groupId);
                Currency currency = db.currencyDao().getCurrencyById(currentTransaction.currencyId);

                // Update UI on the main thread
                runOnUiThread(() -> populateUI(currentTransaction, category, group, currency));
            } else {
                // Handle case where transaction is not found
                runOnUiThread(() -> {
                    Toast.makeText(TransactionDetailActivity.this, "R.string.error_transaction_not_found", Toast.LENGTH_SHORT).show(); // Define in strings.xml
                    finish();
                });
            }
        });
    }

    /**
     * Populates the UI elements with the loaded transaction data.
     * @param transaction The transaction details.
     * @param category The related category (can be null).
     * @param group The related group (can be null).
     * @param currency The related currency (can be null).
     */
    private void populateUI(Transaction transaction, Category category, Group group, Currency currency) {
        textViewDetailTitle.setText(transaction.title);

        // --- Amount Formatting ---
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(); // Uses default locale
        String formattedAmount;
        if (currency != null && !TextUtils.isEmpty(currency.symbol)) {
            // Basic approach: Prepend symbol, format number.
            // For robust formatting, consider using java.util.Currency and locale settings.
            formattedAmount = String.format("%s %s",
                    currency.symbol,
                    String.format(Locale.getDefault(), "%.2f", transaction.amount));
        } else {
            // Fallback if currency or symbol is missing
            formattedAmount = String.format(Locale.getDefault(), "%.2f", transaction.amount);
        }
        textViewDetailAmount.setText(formattedAmount);
        // Optionally set text color based on amount (e.g., green for income, red for expense)
        // if (transaction.amount >= 0) {
        //    textViewDetailAmount.setTextColor(getResources().getColor(R.color.positive_amount, getTheme()));
        // } else {
        //    textViewDetailAmount.setTextColor(getResources().getColor(R.color.negative_amount, getTheme()));
        // }


        // --- Date Formatting ---
        // IMPORTANT: Assumes 'transaction.date' is a meaningful value representing the date.
        // Storing as Long (milliseconds since epoch) is recommended.
        // This is a basic example. Adapt based on how you store the date.
        try {
            // If 'date' is stored as timestamp (Long)
            long timestamp = transaction.date; // Assuming 'date' can be cast or is already long
            if (timestamp > 0) {
                textViewDetailDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(timestamp)));
            } else {
                textViewDetailDate.setText(R.string.not_set); // Define in strings.xml
            }
            // If 'date' is stored differently (e.g., YYYYMMDD int), parse and format accordingly.
        } catch (Exception e) {
            textViewDetailDate.setText(R.string.not_set); // Fallback on error
        }


        // --- Category, Group, Description ---
        textViewDetailCategory.setText(category != null ? category.name : getString(R.string.not_set));
        textViewDetailGroup.setText(group != null ? group.name : getString(R.string.not_set));
        textViewDetailDescription.setText(!TextUtils.isEmpty(transaction.description) ? transaction.description : getString(R.string.no_description)); // Define in strings.xml
    }

    /** Inflate options menu (Edit, Delete) */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaction_detail, menu); // Create res/menu/menu_transaction_detail.xml
        return true;
    }

    /** Handle options menu item selection */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // Using if-else as switch on resource IDs is discouraged before API level R
        if (id == R.id.action_edit_transaction) {
            editTransaction();
            return true;
        } else if (id == R.id.action_delete_transaction) {
            confirmDeleteTransaction();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts the AddTransactionActivity to edit the current transaction.
     */
    private void editTransaction() {
//        if (transactionId != DEFAULT_TRANSACTION_ID) {
//            // Use your existing AddTransactionActivity
//            Intent intent = new Intent(this, AddTransactionActivity.class);
//            // Pass the transaction ID so AddTransactionActivity knows it's editing
//            // Ensure AddTransactionActivity checks for this extra
//            intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION_ID, transactionId);
//            startActivity(intent);
//            // Optional: finish this detail activity after starting edit
//            // finish();
//        }
    }

    /**
     * Shows a confirmation dialog before deleting the transaction.
     */
    private void confirmDeleteTransaction() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_transaction_title) // Define in strings.xml
                .setMessage(R.string.delete_transaction_confirmation) // Define in strings.xml
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteTransaction()) // Define in strings.xml
                .setNegativeButton(android.R.string.cancel, null)
                .setIconAttribute(android.R.attr.alertDialogIcon) // Use theme's alert icon
                .show();
    }

    /**
     * Deletes the current transaction from the database in a background thread.
     */
    private void deleteTransaction() {
        if (currentTransaction != null) {
            executorService.execute(() -> {
                db.transactionDao().delete(currentTransaction);
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.transaction_deleted, Toast.LENGTH_SHORT).show(); // Define in strings.xml
                    finish(); // Close detail view after deletion
                });
            });
        }
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
 * =================================================================================
 * NOTE: Add these method signatures to your respective DAO interfaces if not present
 * =================================================================================
 *
 * @Dao public interface TransactionDao {
 * // ... other methods
 * @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
 * Transaction getTransactionById(int id);
 * }
 *
 * @Dao public interface CategoryDao {
 * // ... other methods
 * @Query("SELECT * FROM category WHERE id = :id LIMIT 1")
 * Category getCategoryById(int id);
 * }
 *
 * @Dao public interface GroupDao {
 * // ... other methods
 * @Query("SELECT * FROM group_ WHERE id = :id LIMIT 1")
 * Group getGroupById(int id);
 * }
 *
 * @Dao public interface CurrencyDao {
 * // ... other methods
 * @Query("SELECT * FROM currency WHERE id = :id LIMIT 1")
 * Currency getCurrencyById(int id);
 * }
 *
 * =================================================================================
 * NOTE: Ensure your AddTransactionActivity handles the EXTRA_TRANSACTION_ID extra
 * to load data when editing an existing transaction.
 * =================================================================================
 */
