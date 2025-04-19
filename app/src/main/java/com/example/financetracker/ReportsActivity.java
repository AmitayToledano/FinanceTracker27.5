package com.example.financetracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financetracker.data.AppDatabase;
// Import a chart library if you add charts, e.g.:
// import com.github.mikephil.charting.charts.PieChart;
// import com.github.mikephil.charting.charts.BarChart;
// import com.github.mikephil.charting.data.*; // PieData, BarData etc.
// import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Locale; // For formatting

/**
 * Activity to display financial reports and summaries.
 * Placeholder implementation - requires complex queries and potentially chart libraries.
 */
public class ReportsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textViewTotalSpendingValue, textViewIncomeVsExpenseValue;
    // Example Chart Views (add library dependency first)
    // private PieChart pieChartExpensesByCategory;
    // private BarChart barChartMonthlySummary;

    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        db = AppDatabase.getInstance(this);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar_reports);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("R.string.title_reports"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Find UI elements for summary text
        textViewTotalSpendingValue = findViewById(R.id.textViewTotalSpendingValue);
        textViewIncomeVsExpenseValue = findViewById(R.id.textViewIncomeVsExpenseValue);

        // Find chart views if using a library
        // pieChartExpensesByCategory = findViewById(R.id.pieChartExpensesByCategory);
        // barChartMonthlySummary = findViewById(R.id.barChartMonthlySummary);

        // Load summary data and potentially setup charts
        loadReportSummaryData();
        // setupCharts(); // Call this after adding chart library and implementation
    }

    /**
     * Loads summary data (e.g., total spending, income vs expense).
     * Requires specific aggregation queries in DAOs.
     */
    private void loadReportSummaryData() {
        executorService.execute(() -> {
            // --- Placeholder Queries - Implement these in your TransactionDao ---
            // Example: Get total spending (sum of negative amounts)
            // double totalSpending = db.transactionDao().getTotalSpendingForPeriod(startDate, endDate); // Needs DAO method
            // Example: Get total income (sum of positive amounts)
            // double totalIncome = db.transactionDao().getTotalIncomeForPeriod(startDate, endDate); // Needs DAO method

            // Placeholder values for now
            double totalSpending = 1234.56; // Replace with actual DAO call result
            double totalIncome = 2500.00;   // Replace with actual DAO call result
            // --- End Placeholder Queries ---


            // Format values for display
            String spendingText = String.format(Locale.getDefault(), "%.2f", Math.abs(totalSpending)); // Show positive value
            String incomeVsExpenseText = String.format(Locale.getDefault(),
                    "Income: %.2f / Expenses: %.2f", totalIncome, Math.abs(totalSpending));

            // Update UI on the main thread
            runOnUiThread(() -> {
                // TODO: Get default currency symbol from settings/DB for better formatting
                String defaultSymbol = "$"; // Placeholder symbol
                textViewTotalSpendingValue.setText(String.format("%s %s", defaultSymbol, spendingText));
                textViewIncomeVsExpenseValue.setText(incomeVsExpenseText);
            });
        });
    }

    /**
     * Configures and populates charts (if using a chart library).
     * Requires fetching aggregated data (e.g., spending per category, monthly totals).
     */
    private void setupCharts() {
        // TODO: Implement chart setup using a library like MPAndroidChart
        // 1. Add chart library dependency to build.gradle.
        // 2. Add Chart views (PieChart, BarChart) to activity_reports.xml.
        // 3. Find chart views by ID here.
        // 4. Create DAO methods to fetch aggregated data:
        //    - e.g., Map<String, Double> getSpendingByCategory(long start, long end)
        //    - e.g., List<MonthlyTotal> getMonthlySummary(int year)
        // 5. Fetch data using ExecutorService.
        // 6. On the main thread:
        //    - Create chart data objects (PieDataSet, BarDataSet, etc.).
        //    - Customize chart appearance (colors, labels, legends).
        //    - Set data on the chart view.
        //    - Call chartView.invalidate() to refresh.

        // --- Example Snippet (Conceptual - using MPAndroidChart) ---
        /*
        executorService.execute(() -> {
            // Map<String, Double> spendingData = db.transactionDao().getSpendingByCategory(...); // Needs DAO method
            runOnUiThread(() -> {
                if (spendingData != null && !spendingData.isEmpty()) {
                    ArrayList<PieEntry> entries = new ArrayList<>();
                    for (Map.Entry<String, Double> entry : spendingData.entrySet()) {
                        // Use category name as label, amount as value
                        entries.add(new PieEntry(Math.abs(entry.getValue().floatValue()), entry.getKey()));
                    }
                    PieDataSet dataSet = new PieDataSet(entries, "Spending by Category");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Example colors
                    dataSet.setValueTextSize(12f);

                    PieData pieData = new PieData(dataSet);
                    pieChartExpensesByCategory.setData(pieData);
                    pieChartExpensesByCategory.getDescription().setEnabled(false); // Hide description label
                    pieChartExpensesByCategory.setEntryLabelTextSize(10f);
                    pieChartExpensesByCategory.invalidate(); // Refresh chart
                } else {
                    // Handle empty data case for chart
                    pieChartExpensesByCategory.clear();
                    pieChartExpensesByCategory.setNoDataText("No spending data available.");
                    pieChartExpensesByCategory.invalidate();
                }
            });
        });
        */
        // --- End Example Snippet ---
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
 * ========================================================================
 * NOTE: Reporting requires more complex queries in your TransactionDao.
 * Examples of methods you might need:
 * ========================================================================
 *
 * @Dao
 * public interface TransactionDao {
 * // ... other methods ...
 *
 * // Get sum of amounts (filter by positive/negative for income/expense)
 * @Query("SELECT SUM(amount) FROM transactions WHERE amount < 0") // Example for spending
 * double getTotalSpending(); // Add date range parameters if needed
 *
 * @Query("SELECT SUM(amount) FROM transactions WHERE amount > 0") // Example for income
 * double getTotalIncome(); // Add date range parameters if needed
 *
 * // Get spending grouped by category (requires joining with Category table)
 * // This might return a custom data class or require multiple queries/processing
 * @Query("SELECT c.name, SUM(t.amount) as total FROM transactions t " +
 * "INNER JOIN category c ON t.categoryId = c.id " +
 * "WHERE t.amount < 0 GROUP BY t.categoryId") // Example structure
 * List<CategorySpending> getSpendingByCategory(); // Define CategorySpending data class
 *
 * // Similar queries needed for monthly summaries, etc.
 * }
 *
 * // Example Data Class for grouped results
 * public class CategorySpending {
 * public String name;
 * public double total;
 * }
 *
 */
