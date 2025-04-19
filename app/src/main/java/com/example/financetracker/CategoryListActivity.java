package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// Assuming you will create a CategoryAdapter similar to your GroupAdapter/TransactionAdapter
// import com.example.financetracker.adapters.CategoryAdapter;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.financetracker.adapters.CategoryAdapter;

/**
 * Activity to display a list of categories.
 * Allows users to view categories and initiate adding new ones.
 */
public class CategoryListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;
     private CategoryAdapter categoryAdapter; // Uncomment when adapter is created
    private List<Category> categoryList = new ArrayList<>();
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Initialize database instance
        db = AppDatabase.getInstance(this);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar_categories);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("R.string.title_categories"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Handle back button press in toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // Find UI elements
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        FloatingActionButton fabAddCategory = findViewById(R.id.fabAddCategory);

        // Setup RecyclerView
        setupRecyclerView();

        // Load categories from database
        loadCategories();

        // Set listener for FAB to open AddEditCategoryActivity
        fabAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryListActivity.this, AddEditCategoryActivity.class);
            // Consider using startActivityForResult if you need immediate list refresh after adding/editing
            startActivity(intent);
        });

        // Note: Click listeners for editing/deleting items should be implemented
        // within your CategoryAdapter.
    }

    /**
     * Configures the RecyclerView with a LayoutManager and Adapter.
     */
    private void setupRecyclerView() {
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        // Instantiate and set the adapter when it's created
         categoryAdapter = new CategoryAdapter(categoryList, category -> {
             // Handle item click: Launch AddEditCategoryActivity for editing
             Intent intent = new Intent(CategoryListActivity.this, AddEditCategoryActivity.class);
             intent.putExtra(AddEditCategoryActivity.EXTRA_CATEGORY_ID, category.id);
             startActivity(intent);
         });
         recyclerViewCategories.setAdapter(categoryAdapter);
    }

    /**
     * Loads categories from the database in a background thread
     * and updates the UI on the main thread.
     */
    private void loadCategories() {
        executorService.execute(() -> {
            // Perform database query on background thread
            List<Category> loadedCategories = db.categoryDao().getAll();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                categoryList.clear();
                categoryList.addAll(loadedCategories);
                // Notify the adapter that the data set has changed
                 if (categoryAdapter != null) { // Uncomment when adapter is created
                     categoryAdapter.notifyDataSetChanged();
                 }
                // Placeholder log until adapter is ready
                System.out.println("Loaded " + categoryList.size() + " categories.");
                if (categoryList.isEmpty()) {
                    // Optionally show an empty state message
                }
            });
        });
    }

    /**
     * Refresh the list when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Reload data in case changes were made in AddEditCategoryActivity
        loadCategories();
    }

    /**
     * Clean up resources when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor service to prevent memory leaks
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
