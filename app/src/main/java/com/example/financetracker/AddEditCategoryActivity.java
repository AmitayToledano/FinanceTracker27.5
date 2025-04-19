package com.example.financetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to add a new category or edit an existing one.
 */
public class AddEditCategoryActivity extends AppCompatActivity {

    // Key for passing the category ID via Intent extras
    public static final String EXTRA_CATEGORY_ID = "com.example.financetracker.EXTRA_CATEGORY_ID";
    // Default value indicating a new category is being added
    private static final int DEFAULT_CATEGORY_ID = -1;

    private TextInputLayout textInputLayoutCategoryName;
    private TextInputEditText editTextCategoryName;
    private Button buttonSaveCategory;
    private Toolbar toolbar;

    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int categoryId = DEFAULT_CATEGORY_ID;
    private Category currentCategory; // Holds the category being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        // Initialize database instance
        db = AppDatabase.getInstance(this);

        // Find UI elements
        toolbar = findViewById(R.id.toolbar_add_edit_category);
        textInputLayoutCategoryName = findViewById(R.id.textInputLayoutCategoryName);
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonSaveCategory = findViewById(R.id.buttonSaveCategory);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Handle back button press in toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // Check if an existing category ID was passed
        if (getIntent().hasExtra(EXTRA_CATEGORY_ID)) {
            categoryId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, DEFAULT_CATEGORY_ID);
        }

        // Set title and load data if editing
        if (categoryId != DEFAULT_CATEGORY_ID) {
            setTitle("R.string.edit_category"); // Define in strings.xml
            loadCategoryData();
        } else {
            setTitle(R.string.add_category); // Define in strings.xml
        }

        // Set listener for the save button
        buttonSaveCategory.setOnClickListener(v -> saveCategory());
    }

    /**
     * Loads the data for the category being edited from the database.
     * This runs on a background thread.
     */
    private void loadCategoryData() {
        executorService.execute(() -> {
            // Fetch the category from the database by ID
            // IMPORTANT: You need to add a getCategoryById(int id) method to your CategoryDao
            currentCategory = db.categoryDao().getCategoryById(categoryId);

            // Update the UI on the main thread
            if (currentCategory != null) {
                runOnUiThread(() -> {
                    editTextCategoryName.setText(currentCategory.name);
                });
            } else {
                // Handle case where category is not found (e.g., deleted)
                runOnUiThread(() -> {
                    Toast.makeText(this, "R.string.error_loading_category", Toast.LENGTH_SHORT).show(); // Define in strings.xml
                    finish(); // Close the activity if category not found
                });
            }
        });
    }


    /**
     * Validates input and saves the new or updated category to the database.
     * This runs database operations on a background thread.
     */
    private void saveCategory() {
        String name = editTextCategoryName.getText() != null ? editTextCategoryName.getText().toString().trim() : "";

        // Validate input
        if (TextUtils.isEmpty(name)) {
            textInputLayoutCategoryName.setError("getString(R.string.error_category_name_required)"); // Define in strings.xml
            editTextCategoryName.requestFocus();
            return;
        } else {
            textInputLayoutCategoryName.setError(null); // Clear error
        }

        // Perform database operation on background thread
        executorService.execute(() -> {
            if (categoryId == DEFAULT_CATEGORY_ID) {
                // Add new category
                Category newCategory = new Category();
                newCategory.name = name;
                db.categoryDao().insert(newCategory);
                runOnUiThread(() -> Toast.makeText(this, "R.string.category_saved", Toast.LENGTH_SHORT).show()); // Define in strings.xml
            } else {
                // Update existing category
                if(currentCategory != null) {
                    currentCategory.name = name;
                    db.categoryDao().update(currentCategory);
                    runOnUiThread(() -> Toast.makeText(this, "R.string.category_updated", Toast.LENGTH_SHORT).show()); // Define in strings.xml
                } else {
                    // Should not happen if loadCategoryData worked, but handle defensively
                    runOnUiThread(() -> Toast.makeText(this, "R.string.error_updating_category", Toast.LENGTH_SHORT).show()); // Define in strings.xml
                }
            }
            // Finish activity after successful save/update
            runOnUiThread(this::finish);
        });
    }

    /**
     * Clean up resources when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}

/*
 * ============================================================
 * NOTE: Add this method signature to your CategoryDao interface
 * ============================================================
 *
 * @Dao
 * public interface CategoryDao {
 * // ... other methods (getAll, insert, update, delete)
 *
 * @Query("SELECT * FROM category WHERE id = :id LIMIT 1")
 * Category getCategoryById(int id);
 * }
 *
 */
