package com.example.financetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.models.Group;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to add a new group or edit an existing one.
 */
public class AddEditGroupActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "com.example.financetracker.EXTRA_GROUP_ID";
    private static final int DEFAULT_GROUP_ID = -1;

    private TextInputLayout textInputLayoutGroupName, textInputLayoutGroupDescription;
    private TextInputEditText editTextGroupName, editTextGroupDescription;
    private Button buttonSaveGroup;
    private Toolbar toolbar;

    private AppDatabase db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int groupId = DEFAULT_GROUP_ID;
    private Group currentGroup; // Holds the group being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_group);

        db = AppDatabase.getInstance(this);

        // Find UI elements
        toolbar = findViewById(R.id.toolbar_add_edit_group);
        textInputLayoutGroupName = findViewById(R.id.textInputLayoutGroupName);
        editTextGroupName = findViewById(R.id.editTextGroupName);
        textInputLayoutGroupDescription = findViewById(R.id.textInputLayoutGroupDescription);
        editTextGroupDescription = findViewById(R.id.editTextGroupDescription);
        buttonSaveGroup = findViewById(R.id.buttonSaveGroup);

        // Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Check if editing
        if (getIntent().hasExtra(EXTRA_GROUP_ID)) {
            groupId = getIntent().getIntExtra(EXTRA_GROUP_ID, DEFAULT_GROUP_ID);
        }

        // Set title and load data if editing
        if (groupId != DEFAULT_GROUP_ID) {
            setTitle("R.string.edit_group"); // Define in strings.xml
            loadGroupData();
        } else {
            setTitle(R.string.add_group); // Define in strings.xml
        }

        buttonSaveGroup.setOnClickListener(v -> saveGroup());
    }

    /**
     * Loads the data for the group being edited from the database.
     */
    private void loadGroupData() {
        executorService.execute(() -> {
            // IMPORTANT: You need to add a getGroupById(int id) method to your GroupDao
            currentGroup = db.groupDao().getGroupById(groupId);

            if (currentGroup != null) {
                runOnUiThread(() -> {
                    editTextGroupName.setText(currentGroup.name);
                    editTextGroupDescription.setText(currentGroup.description);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this," R.string.error_loading_group", Toast.LENGTH_SHORT).show(); // Define in strings.xml
                    finish();
                });
            }
        });
    }

    /**
     * Validates input and saves the new or updated group to the database.
     */
    private void saveGroup() {
        String name = editTextGroupName.getText() != null ? editTextGroupName.getText().toString().trim() : "";
        String description = editTextGroupDescription.getText() != null ? editTextGroupDescription.getText().toString().trim() : ""; // Description is optional

        // Validate required fields
        if (TextUtils.isEmpty(name)) {
            textInputLayoutGroupName.setError("getString(R.string.error_group_name_required)"); // Define in strings.xml
            editTextGroupName.requestFocus();
            return;
        } else {
            textInputLayoutGroupName.setError(null); // Clear error
        }

        // Perform database operation on background thread
        executorService.execute(() -> {
            if (groupId == DEFAULT_GROUP_ID) {
                // Add new group
                Group newGroup = new Group();
                newGroup.name = name;
                newGroup.description = description;
                db.groupDao().insert(newGroup);
                runOnUiThread(() -> Toast.makeText(this, "R.string.group_saved", Toast.LENGTH_SHORT).show()); // Define in strings.xml
            } else {
                // Update existing group
                if(currentGroup != null) {
                    currentGroup.name = name;
                    currentGroup.description = description;
                    db.groupDao().update(currentGroup);
                    runOnUiThread(() -> Toast.makeText(this, "R.string.group_updated", Toast.LENGTH_SHORT).show()); // Define in strings.xml
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "R.string.error_updating_group", Toast.LENGTH_SHORT).show()); // Define in strings.xml
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
 * NOTE: Add this method signature to your GroupDao interface
 * ============================================================
 *
 * @Dao
 * public interface GroupDao {
 * // ... other methods (getAll, insert, update, delete)
 *
 * @Query("SELECT * FROM group_ WHERE id = :id LIMIT 1") // Note table name is group_
 * Group getGroupById(int id);
 * }
 *
 */
