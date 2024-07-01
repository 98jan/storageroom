package com.iu.storageroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.ShoppingList;

import java.util.List;

/**
 * Adapter for displaying Storage Room items in a RecyclerView.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private final List<ShoppingList> shoppingListCollection;
    private final Context context;
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;
    private final OnOpenProductListener openProductListener;

    /**
     * Interface for handling edit button clicks.
     */
    public interface OnEditClickListener {
        void onEditClick(ShoppingList shoppingList);
    }

    /**
     * Interface for handling delete button clicks.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(ShoppingList shoppingList);
    }

    /**
     * Interface for handling item clicks to open product details.
     */
    public interface OnOpenProductListener {
        void onOpenProduct(ShoppingList shoppingList);
    }

    /**
     * Constructor for the ShoppingListAdapter.
     *
     * @param context             The context in which the adapter is being used.
     * @param shoppingListCollection The list of shopping lists to display.
     * @param editClickListener   Listener for edit button clicks.
     * @param deleteClickListener Listener for delete button clicks.
     * @param openProductListener Listener for item clicks to open product details.
     */
    public ShoppingListAdapter(Context context, List<ShoppingList> shoppingListCollection,
                               OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener,
                               OnOpenProductListener openProductListener) {
        this.context = context;
        this.shoppingListCollection = shoppingListCollection;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
        this.openProductListener = openProductListener;
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_shoppinglist_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingList shoppingList = shoppingListCollection.get(position);
        holder.shoppingListName.setText(shoppingList.getName());

        int iconResId = shoppingList.getSelectedIconInt();
        holder.shoppingListImage.setImageResource(iconResId);

        // Edit button click listener
        holder.editButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(shoppingList);
            }
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(shoppingList);
            }
        });

        // Item click listener to open ShoppingListOverviewActivity
        holder.itemView.setOnClickListener(v -> {
            if (openProductListener != null) {
                openProductListener.onOpenProduct(shoppingList);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return shoppingListCollection.size();
    }

    /**
     * ViewHolder class for holding the view elements of each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView shoppingListName;
        public ImageView shoppingListImage;
        public ImageView editButton;
        public ImageView deleteButton;
        public ImageView addProductFab;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The View object that represents the RecyclerView item.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            shoppingListName = itemView.findViewById(R.id.shoppingListName);
            shoppingListImage = itemView.findViewById(R.id.shoppingListImage);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addProductFab = itemView.findViewById(R.id.createProductButton);
        }
    }
}
