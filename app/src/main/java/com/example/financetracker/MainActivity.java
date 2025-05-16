package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import View
import android.widget.Button; // Or MaterialButton if using Material Components theme extensively

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton; // Example using MaterialButton
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener { // Implement OnClickListener

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialButton buttonViewTransactions, buttonAddTransaction;
    private MaterialButton buttonManageCategories, buttonManageGroups, buttonManageCurrencies;
    private MaterialButton buttonViewReports, buttonOpenSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name); // Use app name or a dashboard title
        }
        // No back button needed on main screen usually
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Find Navigation Buttons
        buttonViewTransactions = findViewById(R.id.buttonViewTransactions);
        buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
        buttonManageCategories = findViewById(R.id.buttonManageCategories);
        buttonManageGroups = findViewById(R.id.buttonManageGroups);
        buttonManageCurrencies = findViewById(R.id.buttonManageCurrencies);
        buttonViewReports = findViewById(R.id.buttonViewReports);
        //buttonOpenSettings = findViewById(R.id.buttonOpenSettings);

        // Set Click Listeners
        buttonViewTransactions.setOnClickListener(this);
        buttonAddTransaction.setOnClickListener(this);
        buttonManageCategories.setOnClickListener(this);
        buttonManageGroups.setOnClickListener(this);
        buttonManageCurrencies.setOnClickListener(this);
        buttonViewReports.setOnClickListener(this);
        //buttonOpenSettings.setOnClickListener(this);


        // Create an ActionBarDrawerToggle to handle
        // the drawer's open/close state
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        // Add the toggle as a listener to the DrawerLayout
        drawerLayout.addDrawerListener(toggle);

        // Synchronize the toggle's state with the linked DrawerLayout
        toggle.syncState();
        // You could also add summary information here (e.g., current balance)
        // fetching it from the database (requires background task).
    }

    /**
     * Handles click events for the navigation buttons.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId(); // Get the ID of the clicked view

        // Use if-else as switch on resource IDs is discouraged before API level R
        if (id == R.id.buttonViewTransactions) {
            intent = new Intent(this, TransactionListActivity.class); // Existing Activity
        } else
            if (id == R.id.buttonAddTransaction) {
            intent = new Intent(this, AddEditTransactionActivity.class); // Existing Activity
        } else
        if (id == R.id.buttonManageCategories) {
            intent = new Intent(this, CategoryListActivity.class); // New Activity
        } else if (id == R.id.buttonManageGroups) {
            intent = new Intent(this, GroupListActivity.class); // New Activity
        } else if (id == R.id.buttonManageCurrencies) {
            intent = new Intent(this, CurrencyListActivity.class); // New Activity
        } else if (id == R.id.buttonViewReports) {
            intent = new Intent(this, ReportsActivity.class); // New Activity
        } /*else if (id == R.id.buttonOpenSettings) {
            intent = new Intent(this, SettingsActivity.class); // New Activity
        }*/

        // Start the activity if an intent was created
        if (intent != null) {
            startActivity(intent);
        }
    }

    // Optional: Add onCreateOptionsMenu if you want a menu on the main screen (e.g., for settings)
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Create res/menu/menu_main.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
