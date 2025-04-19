package com.example.financetracker.adapters; // Assuming you place adapters in an 'adapters' package

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financetracker.R; // Make sure R is imported correctly
import com.example.financetracker.models.Currency;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of Currency items.
 */
public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private final List<Currency> currencyList;
    private final OnItemClickListener listener;

    /**
     * Interface for handling click events on adapter items.
     */
    public interface OnItemClickListener {
        void onItemClick(Currency currency);
    }

    /**
     * Constructor for the CurrencyAdapter.
     * @param currencyList The list of currencies to display.
     * @param listener The listener for item click events.
     */
    public CurrencyAdapter(List<Currency> currencyList, OnItemClickListener listener) {
        this.currencyList = currencyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_currency.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false); // Use your item layout file
        return new CurrencyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        // Get the currency at the current position
        Currency currentCurrency = currencyList.get(position);
        // Bind the currency data to the ViewHolder's views
        holder.bind(currentCurrency, listener);
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return currencyList != null ? currencyList.size() : 0;
    }

    /**
     * ViewHolder class for the currency item view.
     * Holds references to the UI elements within the item layout.
     */
    static class CurrencyViewHolder extends RecyclerView.ViewHolder {
        // UI elements from item_currency.xml
        private final TextView textViewCurrencyName;
        private final TextView textViewCurrencySymbol;
        private final TextView textViewCurrencyRate; // Optional: display rate

        CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find views by ID from the inflated item layout
            textViewCurrencyName = itemView.findViewById(R.id.textViewItemCurrencyName); // Use ID from item_currency.xml
            textViewCurrencySymbol = itemView.findViewById(R.id.textViewItemCurrencySymbol); // Use ID from item_currency.xml
            textViewCurrencyRate = itemView.findViewById(R.id.textViewItemCurrencyRate); // Use ID from item_currency.xml
        }

        /**
         * Binds currency data to the views and sets up the click listener.
         * @param currency The currency data object.
         * @param listener The click listener interface.
         */
        void bind(final Currency currency, final OnItemClickListener listener) {
            // Set the currency details
            textViewCurrencyName.setText(currency.name);
            textViewCurrencySymbol.setText(currency.symbol);
            // Format and set the rate (optional)
            textViewCurrencyRate.setText(String.format(Locale.getDefault(), "Rate: %.4f", currency.rate));

            // Set the click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(currency);
                }
            });
        }
    }
}
