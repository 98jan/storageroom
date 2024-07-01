package com.iu.storageroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.ShoppingListProduct;

import java.util.List;

public class ShoppingListProductAdapter extends RecyclerView.Adapter<ShoppingListProductAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingListProduct> shoppingListProductList;
    private OnItemClickListener onItemClickListener;

    public ShoppingListProductAdapter(Context context, List<ShoppingListProduct> shoppingListProductList) {
        this.context = context;
        this.shoppingListProductList = shoppingListProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_shoppinglist_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingListProduct shoppingListProduct = shoppingListProductList.get(position);
        holder.productName.setText(shoppingListProduct.getProductName());
        holder.productQuantity.setText(String.valueOf(shoppingListProduct.getQuantity()));

        // Set click listeners for edit and delete buttons
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingListProductList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productQuantity;
        public ImageView editButton;
        public ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
