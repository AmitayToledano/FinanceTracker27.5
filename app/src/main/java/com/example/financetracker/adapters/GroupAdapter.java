package com.example.financetracker.adapters; // Assuming you place adapters in an 'adapters' package

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;
import com.example.financetracker.models.Group;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of group items.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<Group> groupList;
    private final OnItemClickListener listener;

    /**
     * Interface for handling click events on adapter items.
     */
    public interface OnItemClickListener {
        void onItemClick(Group group);
        // Add other listeners if needed (e.g., void onItemLongClick(group group);)
    }

    /**
     * Constructor for the groupAdapter.
     * @param groupList The list of categories to display.
     * @param listener The listener for item click events.
     */
    public GroupAdapter(List<Group> groupList, OnItemClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_group.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false); // Use your item layout file
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        // Get the group at the current position
        Group currentgroup = groupList.get(position);
        // Bind the group data to the ViewHolder's views
        holder.bind(currentgroup, listener);
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return groupList != null ? groupList.size() : 0;
    }

    /**
     * ViewHolder class for the group item view.
     * Holds references to the UI elements within the item layout.
     */
    static class GroupViewHolder extends RecyclerView.ViewHolder {
        // UI elements from item_group.xml
        private final TextView textViewgroupName;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find views by ID from the inflated item layout
            textViewgroupName = itemView.findViewById(R.id.textViewItemGroupName); // Use ID from item_group.xml
        }

        /**
         * Binds group data to the views and sets up the click listener.
         * @param group The group data object.
         * @param listener The click listener interface.
         */
        void bind(final Group group, final OnItemClickListener listener) {
            // Set the group name
            textViewgroupName.setText(group.name);

            // Set the click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(group);
                }
            });

            // Add long click listener if needed
            // itemView.setOnLongClickListener(v -> {
            //     if (listener != null) {
            //         // listener.onItemLongClick(group); // Call appropriate method
            //         return true; // Consume the long click
            //     }
            //     return false;
            // });
        }
    }
}
