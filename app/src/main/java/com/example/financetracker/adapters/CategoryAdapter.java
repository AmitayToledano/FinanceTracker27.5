package com.example.financetracker.adapters; // Assuming you place adapters in an 'adapters' package

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financetracker.R; // Make sure R is imported correctly
import com.example.financetracker.models.Category;
import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of Category items.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final OnItemClickListener listener;

    /**
     * Interface for handling click events on adapter items.
     */
    public interface OnItemClickListener {
        void onItemClick(Category category);
        // Add other listeners if needed (e.g., void onItemLongClick(Category category);)
    }

    /**
     * Constructor for the CategoryAdapter.
     * @param categoryList The list of categories to display.
     * @param listener The listener for item click events.
     */
    public CategoryAdapter(List<Category> categoryList, OnItemClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_category.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false); // Use your item layout file
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Get the category at the current position
        Category currentCategory = categoryList.get(position);
        // Bind the category data to the ViewHolder's views
        holder.bind(currentCategory, listener);
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return categoryList != null ? categoryList.size() : 0;
    }

    /**
     * ViewHolder class for the category item view.
     * Holds references to the UI elements within the item layout.
     */
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        // UI elements from item_category.xml
        private final TextView textViewCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find views by ID from the inflated item layout
            textViewCategoryName = itemView.findViewById(R.id.textViewItemCategoryName); // Use ID from item_category.xml
        }

        /**
         * Binds category data to the views and sets up the click listener.
         * @param category The category data object.
         * @param listener The click listener interface.
         */
        void bind(final Category category, final OnItemClickListener listener) {
            // Set the category name
            textViewCategoryName.setText(category.name);

            // Set the click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(category);
                }
            });

            // Add long click listener if needed
            // itemView.setOnLongClickListener(v -> {
            //     if (listener != null) {
            //         // listener.onItemLongClick(category); // Call appropriate method
            //         return true; // Consume the long click
            //     }
            //     return false;
            // });
        }
    }
}
