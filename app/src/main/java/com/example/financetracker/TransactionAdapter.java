package com.example.financetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import android.util.Log;
public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private Context context;
    private List<Transaction> transactions;
    private DatabaseHelper dbHelper;

    public TransactionAdapter(Context context, List<Transaction> transactions, DatabaseHelper dbHelper) {
        super(context, 0, transactions);
        this.context = context;
        this.transactions = transactions;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = transactions.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        }

        TextView textDescription = convertView.findViewById(R.id.text_description);
        TextView textAmount = convertView.findViewById(R.id.text_amount);
        TextView textDate = convertView.findViewById(R.id.text_date);
        Button buttonEdit = convertView.findViewById(R.id.button_edit);
        Button buttonDelete = convertView.findViewById(R.id.button_delete);

        textDescription.setText(transaction.title);
        if (transaction.amount >= 0) {
            textAmount.setText("Amount: +$" + String.format("%.2f", transaction.amount));
            textAmount.setTextColor(Color.parseColor("#388E3C")); // green
        } else {
            textAmount.setText("Amount: -$" + String.format("%.2f", Math.abs(transaction.amount)));
            textAmount.setTextColor(Color.parseColor("#D32F2F")); // red

        }
        textDate.setText("Date: " + transaction.getFormattedDate());

        buttonDelete.setOnClickListener(v -> {
            dbHelper.deleteTransaction(transaction.id);
            transactions.remove(position);
            notifyDataSetChanged();
        });

        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTransactionActivity.class);
            intent.putExtra("edit_id", transaction.id);
            context.startActivity(intent);
        });

        return convertView;
    }
}
