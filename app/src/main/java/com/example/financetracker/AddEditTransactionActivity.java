package com.example.financetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Category;
import com.example.financetracker.models.Currency;
import com.example.financetracker.models.Group;
import com.example.financetracker.models.Transaction;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_TRANSACTION_ID = "com.example.financetracker.EXTRA_TRANSACTION_ID";

    private TextInputEditText editTextTransactionTitle;
    private TextInputEditText editTextTransactionAmount;
    private Spinner spinnerTransactionCategory;
    private Spinner spinnerTransactionGroup;
    private Spinner spinnerTransactionCurrency;
    private Spinner spinnerTransactionDate;
    private Button buttonSaveTransaction;

    private AppDatabase db;
    private ExecutorService executorService;

    private Date selectedDate;
    private Transaction editingTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        editTextTransactionTitle = findViewById(R.id.editTextTransactionTitle);
        editTextTransactionAmount = findViewById(R.id.editTextTransactionAmount);
        spinnerTransactionCategory = findViewById(R.id.spinnerTransactionCategory);
        spinnerTransactionGroup = findViewById(R.id.spinnerTransactionGroup);
        spinnerTransactionCurrency = findViewById(R.id.spinnerTransactionCurrency);
        spinnerTransactionDate = findViewById(R.id.spinnerTransactionDate);
        buttonSaveTransaction = findViewById(R.id.buttonSaveTransaction);

        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        loadCategories();
        loadGroups();
        loadCurrencies();
        setUpDateSpinner();

        buttonSaveTransaction.setOnClickListener(v -> saveTransaction());

        int transactionId = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        if (transactionId != -1) {
            loadTransaction(transactionId);
        }
    }

    private void loadCategories() {
        executorService.execute(() -> {
            List<Category> categories = db.categoryDao().getAll();
            runOnUiThread(() -> {
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTransactionCategory.setAdapter(adapter);
            });
        });
    }

    private void loadGroups() {
        executorService.execute(() -> {
            List<Group> groups = db.groupDao().getAll();
            runOnUiThread(() -> {
                ArrayAdapter<Group> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, groups);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTransactionGroup.setAdapter(adapter);
            });
        });
    }

    private void loadCurrencies() {
        executorService.execute(() -> {
            List<Currency> currencies = db.currencyDao().getAll();
            runOnUiThread(() -> {
                ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, currencies);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTransactionCurrency.setAdapter(adapter);
            });
        });
    }

    private void setUpDateSpinner() {
        final List<Date> dates = getNext30Days();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, convertDatesToStrings(dates));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransactionDate.setAdapter(adapter);
        spinnerTransactionDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = dates.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveTransaction() {
        final String title = editTextTransactionTitle.getText().toString();
        final String amountText = editTextTransactionAmount.getText().toString();

        if (title.isEmpty() || amountText.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        final double amount = Double.parseDouble(amountText);

        Group selectedGroup = (Group) spinnerTransactionGroup.getSelectedItem();
        Currency selectedCurrency = (Currency) spinnerTransactionCurrency.getSelectedItem();
        Category selectedCategory = (Category) spinnerTransactionCategory.getSelectedItem();

        if (selectedGroup == null || selectedCurrency == null || selectedCategory == null) {
            Toast.makeText(this, "Please select all options", Toast.LENGTH_SHORT).show();
            return;
        }

        Transaction transaction = editingTransaction != null ? editingTransaction : new Transaction();
        transaction.title = title;
        transaction.amount = amount;
        transaction.categoryId = selectedCategory.id;
        transaction.groupId = selectedGroup.id;
        transaction.currencyId = selectedCurrency.id;
        transaction.date = selectedDate.getDate();

        executorService.execute(() -> {
            if (editingTransaction != null) {
                db.transactionDao().update(transaction);
            } else {
                db.transactionDao().insert(transaction);
            }
        });

        finish();
    }

    private void loadTransaction(int transactionId) {
        executorService.execute(() -> {
            editingTransaction = db.transactionDao().getById(transactionId);
            runOnUiThread(() -> {
                if (editingTransaction != null) {
                    editTextTransactionTitle.setText(editingTransaction.title);
                    editTextTransactionAmount.setText(String.valueOf(editingTransaction.amount));
                    preselectSpinnerItem(spinnerTransactionCategory, editingTransaction.categoryId);
                    preselectSpinnerItem(spinnerTransactionGroup, editingTransaction.groupId);
                    preselectSpinnerItem(spinnerTransactionCurrency, editingTransaction.currencyId);
                    preselectDateSpinnerItem(editingTransaction.date);
                }
            });
        });
    }

    private <T> void preselectSpinnerItem(Spinner spinner, int id) {
        ArrayAdapter<T> adapter = (ArrayAdapter<T>) spinner.getAdapter();
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            try {
                int itemId = (int) item.getClass().getField("id").get(item);
                if (itemId == id) {
                    spinner.setSelection(i);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void preselectDateSpinnerItem(int dayOfMonth) {
        List<Date> dates = getNext30Days();
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).getDate() == dayOfMonth) {
                spinnerTransactionDate.setSelection(i);
                selectedDate = dates.get(i);
                break;
            }
        }
    }

    private List<Date> getNext30Days() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    private List<String> convertDatesToStrings(List<Date> dates) {
        List<String> dateStrings = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        for (Date date : dates) {
            dateStrings.add(dateFormat.format(date));
        }
        return dateStrings;
    }
}
