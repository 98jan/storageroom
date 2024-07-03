package com.iu.storageroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.Product;

import java.util.List;

/**
 * Adapter class for displaying a list of products in a RecyclerView.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private OnFavoriteClickListener onFavoriteClickListener;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    /**
     * Interface definition for handling edit button clicks.
     */
    public interface OnEditClickListener {
        /**
         * Called when the edit button of a product is clicked.
         *
         * @param product The product associated with the clicked edit button.
         */
        void onEditClick(Product product);
    }

    /**
     * Interface definition for handling delete button clicks.
     */
    public interface OnDeleteClickListener {
        /**
         * Called when the delete button of a product is clicked.
         *
         * @param product The product associated with the clicked delete button.
         */
        void onDeleteClick(Product product);
    }

    /**
     * Constructs a new ProductAdapter.
     *
     * @param context               The context of the activity or fragment.
     * @param productList           The list of products to display.
     * @param onFavoriteClickListener The listener for favorite button clicks.
     * @param editClickListener     The listener for edit button clicks.
     * @param deleteClickListener   The listener for delete button clicks.
     */
    public ProductAdapter(Context context, List<Product> productList, OnFavoriteClickListener onFavoriteClickListener, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.productList = productList;
        this.onFavoriteClickListener = onFavoriteClickListener;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productQuantity.setText(String.valueOf(product.getQuantity()));

        // Edit button click listener
        holder.editButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(product);
            }
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(product);
            }
        });

        // Set favorite button icon based on product status
        if (product.isFavourite()) {
            holder.favoriteButton.setImageResource(R.drawable.favorite_icon);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.favorite_icon_outline);
        }

        // Favorite button click listener
        holder.favoriteButton.setOnClickListener(v -> {
            // Inform the activity that the favorite button was clicked
            onFavoriteClickListener.onFavoriteClick(product, position);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Updates the list of products displayed by the adapter.
     *
     * @param newProductList The new list of products to display.
     */
    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product, int position);
    }

    /**
     * ViewHolder class for caching views of individual product items in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productQuantity;
        public ImageButton favoriteButton;
        public ImageView editButton;
        public ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

}