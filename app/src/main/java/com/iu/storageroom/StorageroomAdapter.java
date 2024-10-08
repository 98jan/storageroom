package com.iu.storageroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.Storageroom;

import java.util.List;

/**
 * Adapter for displaying Storage Room items in a RecyclerView.
 */
public class StorageroomAdapter extends RecyclerView.Adapter<StorageroomAdapter.ViewHolder> {
    private List<Storageroom> storageroomList;
    private Context context;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnOpenProductListener openProductListener;

    /**
     * Interface for handling edit button clicks.
     */
    public interface OnEditClickListener {
        void onEditClick(Storageroom storageroom);
    }

    /**
     * Interface for handling delete button clicks.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(Storageroom storageroom);
    }

    /**
     * Interface for handling opening a product from storageroom.
     */
    public interface OnOpenProductListener {
        void onOpenProduct(Storageroom storageroom);
    }

    /**
     * Constructor for the StorageroomAdapter.
     *
     * @param context               The context in which the adapter is being used.
     * @param storageroomList       The list of storagerooms to display.
     * @param editClickListener     Listener for edit button clicks.
     * @param deleteClickListener   Listener for delete button clicks.
     * @param openProductListener   Listener for opening a product.
     */
    public StorageroomAdapter(Context context, List<Storageroom> storageroomList,
                              OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener,
                              OnOpenProductListener openProductListener) {
        this.context = context;
        this.storageroomList = storageroomList;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
        this.openProductListener = openProductListener;
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType  The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_storageroom_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder.
     *
     * @param holder    The ViewHolder to bind data to.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Storageroom storageroom = storageroomList.get(position);
        holder.storageroomName.setText(storageroom.getName());

        int iconResId = storageroom.getSelectedIconInt();
        holder.storageroomImage.setImageResource(iconResId);

        // Edit button click listener
        holder.editButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(storageroom);
            }
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(storageroom);
            }
        });

        // Item click listener to open ProductOverviewActivity
        holder.itemView.setOnClickListener(v -> {
            if (openProductListener != null) {
                openProductListener.onOpenProduct(storageroom);
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
        return storageroomList.size();
    }

    /**
     * ViewHolder class for holding the view elements of each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView storageroomName;
        public ImageView storageroomImage;
        public ImageView editButton;
        public ImageView deleteButton;
        public ImageView addProductFab;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView  The View object that represents the RecyclerView item.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            storageroomName = itemView.findViewById(R.id.storageroomName);
            storageroomImage = itemView.findViewById(R.id.storageroomImage);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addProductFab = itemView.findViewById(R.id.createProductButton);
        }
    }
}