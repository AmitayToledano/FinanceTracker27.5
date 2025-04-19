package com.example.financetracker.adapters; // Assuming you place adapters in an 'adapters' package

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financetracker.R; // Make sure R is imported correctly
import com.example.financetracker.models.Transaction;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of Transaction items.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;
    private final OnItemClickListener listener;

    /**
     * Interface for handling click events on adapter items.
     */
    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    /**
     * Constructor for the TransactionAdapter.
     * @param transactionList The list of currencies to display.
     * @param listener The listener for item click events.
     */
    public TransactionAdapter(List<Transaction> transactionList, OnItemClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_transaction.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false); // Use your item layout file
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Get the transaction at the current position
        Transaction currentTransaction = transactionList.get(position);
        // Bind the transaction data to the ViewHolder's views
        holder.bind(currentTransaction, listener);
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return transactionList != null ? transactionList.size() : 0;
    }

    /**
     * ViewHolder class for the transaction item view.
     * Holds references to the UI elements within the item layout.
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        // UI elements from item_transaction.xml
        private final TextView textViewTransactionName;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find views by ID from the inflated item layout
            textViewTransactionName = itemView.findViewById(R.id.textViewItemTransactionName); // Use ID from item_transaction.xml
        }

        /**
         * Binds transaction data to the views and sets up the click listener.
         * @param transaction The transaction data object.
         * @param listener The click listener interface.
         */
        void bind(final Transaction transaction, final OnItemClickListener listener) {
            // Set the transaction details
            textViewTransactionName.setText(transaction.title);

            // Set the click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(transaction);
                }
            });
        }
    }
}
