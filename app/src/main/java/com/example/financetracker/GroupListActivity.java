package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// Assuming you will use your existing GroupAdapter
import com.example.financetracker.adapters.GroupAdapter; // Make sure package is correct
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to display a list of groups.
 * Allows users to view groups and initiate adding new ones.
 */
public class GroupListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGroups;
    private GroupAdapter groupAdapter; // Use your existing adapter
    private List<Group> groupList = new ArrayList<>();
    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        // Initialize database instance
        db = AppDatabase.getInstance(this);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar_groups);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("R.string.title_groups"); // Define in strings.xml
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Find UI elements
        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);
        FloatingActionButton fabAddGroup = findViewById(R.id.fabAddGroup);

        // Setup RecyclerView
        setupRecyclerView();

        // Load groups from database
        loadGroups();

        // Set listener for FAB to open AddEditGroupActivity
        fabAddGroup.setOnClickListener(v -> {
            Intent intent = new Intent(GroupListActivity.this, AddEditGroupActivity.class);
            startActivity(intent);
        });

        // Note: Click listeners for editing/deleting items should be implemented
        // within your GroupAdapter.
    }

    /**
     * Configures the RecyclerView with a LayoutManager and Adapter.
     */
    private void setupRecyclerView() {
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));
        // Instantiate your existing GroupAdapter
        // Make sure your GroupAdapter constructor and click listener interface match your needs
        groupAdapter = new GroupAdapter(groupList, group -> {
            // Handle item click: Launch AddEditGroupActivity for editing
            Intent intent = new Intent(GroupListActivity.this, AddEditGroupActivity.class);
            intent.putExtra(AddEditGroupActivity.EXTRA_GROUP_ID, group.id);
            startActivity(intent);
        });
        recyclerViewGroups.setAdapter(groupAdapter);
    }

    /**
     * Loads groups from the database in a background thread
     * and updates the UI on the main thread.
     */
    private void loadGroups() {
        executorService.execute(() -> {
            // Perform database query on background thread
            List<Group> loadedGroups = db.groupDao().getAll();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                groupList.clear();
                groupList.addAll(loadedGroups);
                // Notify the adapter that the data set has changed
                if (groupAdapter != null) {
                    groupAdapter.notifyDataSetChanged();
                }
                System.out.println("Loaded " + groupList.size() + " groups.");
                if (groupList.isEmpty()) {
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
        loadGroups();
    }

    /**
     * Clean up resources when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
