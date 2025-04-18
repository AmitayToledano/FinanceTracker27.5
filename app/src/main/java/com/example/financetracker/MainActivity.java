package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import com.example.financetracker.DatabaseHelper;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView textBalance;
    Button buttonAddTransaction;
    Button buttonShowAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        textBalance = findViewById(R.id.text_balance);
        buttonAddTransaction = findViewById(R.id.button_add_transaction);
        buttonShowAll = findViewById(R.id.button_show_all);
        double balance = dbHelper.getCurrentBalance();
        textBalance.setText("Balance: $" + String.format("%.2f", balance));

        buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intent);
            }
        });

        buttonShowAll.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllTransactionsActivity.class);
            startActivity(intent);
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        double balance = dbHelper.getCurrentBalance();
        textBalance.setText("Balance: $" + String.format("%.2f", balance));
    }

}
