package com.example.financetracker;

import android.os.Bundle;
import android.widget.Spinner; // Example for settings
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial; // Example for settings

/**
 * Activity for application settings.
 * This is a basic example; consider using PreferenceFragmentCompat for standard settings.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    // Example settings UI elements
    private Spinner spinnerDefaultCurrency;
    private SwitchMaterial switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using a simple layout here. For complex settings, use PreferenceFragmentCompat.
//        setContentView(R.layout.activity_settings_simple);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("R.string.title_settings"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Find settings UI elements (examples)
        spinnerDefaultCurrency = findViewById(R.id.spinnerDefaultCurrency);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        // TODO: Load current settings values (e.g., from SharedPreferences)
        loadSettings();

        // TODO: Setup listeners to save settings when changed
        setupSettingsListeners();

        // TODO: Populate Spinner with currencies from DB (requires background task)
        populateCurrencySpinner();
    }

    /**
     * Loads setting values from storage (e.g., SharedPreferences).
     */
    private void loadSettings() {
        // Example: Load dark mode preference
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", false);
        // switchDarkMode.setChecked(isDarkMode);

        // Example: Load default currency preference (store currency ID or symbol)
        // int defaultCurrencyId = prefs.getInt("default_currency_id", -1);
        // Set spinner selection based on loaded ID (requires spinner population first)
    }

    /**
     * Sets up listeners for UI elements to save settings when they change.
     */
    private void setupSettingsListeners() {
        // Example: Save dark mode preference
        // switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //     prefs.edit().putBoolean("dark_mode_enabled", isChecked).apply();
        //     // Apply theme change (might require activity restart or AppCompatDelegate)
        //     AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        // });

        // Example: Save default currency preference
        // spinnerDefaultCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //     @Override
        //     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //         Currency selectedCurrency = (Currency) parent.getItemAtPosition(position);
        //         if (selectedCurrency != null) {
        //             SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        //             prefs.edit().putInt("default_currency_id", selectedCurrency.id).apply();
        //         }
        //     }
        //     @Override
        //     public void onNothingSelected(AdapterView<?> parent) { }
        // });
    }

    /**
     * Populates the currency spinner. Should fetch currencies from DB.
     * Needs background execution and an Adapter for the Spinner.
     */
    private void populateCurrencySpinner() {
        // TODO:
        // 1. Fetch List<Currency> from db.currencyDao().getAll() (use ExecutorService).
        // 2. Create an ArrayAdapter<Currency> (override toString() in Currency model for display).
        // 3. Set adapter on spinnerDefaultCurrency on the main thread.
        // 4. Set spinner selection based on loaded default currency ID (in loadSettings).
    }

    // --- Using PreferenceFragmentCompat (Alternative/Recommended Approach) ---
    /*
    // 1. Create res/layout/activity_settings_container.xml with a FrameLayout:
    // <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    //    android:id="@+id/settings_container"
    //    android:layout_width="match_parent"
    //    android:layout_height="match_parent" />

    // 2. Create res/xml/root_preferences.xml with PreferenceScreen, PreferenceCategory, ListPreference, SwitchPreferenceCompat etc.

    // 3. Modify SettingsActivity:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_container); // Use container layout

        // Setup Toolbar (if not part of the fragment layout)
        toolbar = findViewById(R.id.toolbar_settings); // Assuming toolbar is in activity_settings_container
        setSupportActionBar(toolbar);
        // ... setup toolbar navigation ...

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    // 4. Create inner SettingsFragment class:
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // Example: Populate ListPreference dynamically
            // ListPreference currencyPref = findPreference("default_currency_preference_key");
            // if (currencyPref != null) {
            //     // TODO: Load currencies (background task), set entries and entryValues
            // }
        }
    }
    */
    // -----------------------------------------------------------------------
}
