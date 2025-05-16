package com.example.financetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Group;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textViewTotalSpendingValue, textViewIncomeVsExpenseValue;

    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        db = AppDatabase.getInstance(this);

        // === Spinner Filter by Group ===
        Spinner spinnerGroupFilter = findViewById(R.id.spinnerGroupFilter);

        executorService.execute(() -> {
            List<Group> groups = db.groupDao().getAllGroups();
            runOnUiThread(() -> {
                ArrayAdapter<Group> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, groups);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGroupFilter.setAdapter(adapter);

                spinnerGroupFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Group selectedGroup = (Group) parent.getItemAtPosition(position);
                        updateReportForGroup(selectedGroup.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action
                    }
                });
            });
        });
        // === End of Spinner Filter ===

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar_reports);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("R.string.title_reports"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        textViewTotalSpendingValue = findViewById(R.id.textViewTotalSpendingValue);
        textViewIncomeVsExpenseValue = findViewById(R.id.textViewIncomeVsExpenseValue);

        loadReportSummaryData();
    }

    private void loadReportSummaryData() {
        executorService.execute(() -> {
            double totalSpending = 1234.56; // Replace with actual DAO call result
            double totalIncome = 2500.00;   // Replace with actual DAO call result

            String spendingText = String.format(Locale.getDefault(), "%.2f", Math.abs(totalSpending));
            String incomeVsExpenseText = String.format(Locale.getDefault(),
                    "Income: %.2f / Expenses: %.2f", totalIncome, Math.abs(totalSpending));

            runOnUiThread(() -> {
                String defaultSymbol = "$";
                textViewTotalSpendingValue.setText(String.format("%s %s", defaultSymbol, spendingText));
                textViewIncomeVsExpenseValue.setText(incomeVsExpenseText);
            });
        });
    }

    private void updateReportForGroup(long groupId) {
        executorService.execute(() -> {
            double totalSpending = db.transactionDao().getTotalIncomeForGroup(groupId);
            /*double totalIncome = db.transactionDao().getTotalIncomeForGroup(groupId);*/

            String spendingText = String.format(Locale.getDefault(), "%.2f", Math.abs(totalSpending));
            /*String incomeVsExpenseText = String.format(Locale.getDefault(),
                    "Income: %.2f / Expenses: %.2f", totalIncome, Math.abs(totalSpending));*/

            double monthlyTotalExpenses = db.transactionDao().getTotalSpendingForCurrentMonth();

            runOnUiThread(() -> {
                String defaultSymbol = "₪"; // או לשלוף מה־Settings בעתיד
                textViewTotalSpendingValue.setText(String.format("%s %s", defaultSymbol, spendingText));
                if(totalSpending != 0) {
                textViewIncomeVsExpenseValue.setText(/*incomeVsExpenseText + */String.format(" %.2f ₪", (totalSpending-20)));}
                else{textViewIncomeVsExpenseValue.setText(/*incomeVsExpenseText + */String.format(" %.2f ₪", (totalSpending)));}
            });
        });
    }


}
