package com.example.financetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    EditText editTitle, editAmount;
    Button buttonSave;
    DatabaseHelper dbHelper;
    CheckBox checkBoxIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        editTitle = findViewById(R.id.edit_title);
        editAmount = findViewById(R.id.edit_amount);
        buttonSave = findViewById(R.id.button_save);
        dbHelper = new DatabaseHelper(this);
        checkBoxIncome = findViewById(R.id.checkbox_income);


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String amountStr = editAmount.getText().toString();

                if (title.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(AddTransactionActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                long timestamp = System.currentTimeMillis();
                if (!checkBoxIncome.isChecked()) {
                    amount = -amount;
                }

                dbHelper.getWritableDatabase().execSQL(
                        "INSERT INTO transactions (title, amount, date) VALUES (?, ?, ?)",
                        new Object[]{title, amount, timestamp}
                );

                Toast.makeText(AddTransactionActivity.this, "Transaction saved", Toast.LENGTH_SHORT).show();
                finish(); // Close the screen and go back
            }
        });
    }
}

