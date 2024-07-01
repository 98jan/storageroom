package com.iu.storageroom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.Product;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private OnFavoriteClickListener onFavoriteClickListener;

    public ProductAdapter(Context context, List<Product> productList, OnFavoriteClickListener onFavoriteClickListener) {
        this.context = context;
        this.productList = productList;
        this.onFavoriteClickListener = onFavoriteClickListener;
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
        // Setzen des Favoriten-Icons je nach Produktstatus
        if (product.isFavourite()) {
            holder.favoriteButton.setImageResource(R.drawable.favorite_icon);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.favorite_icon_outline);
        }

        // OnClickListener für Favoriten-Button
        holder.favoriteButton.setOnClickListener(v -> {
            // Informiere den Activity, dass der Favoriten-Button geklickt wurde
            onFavoriteClickListener.onFavoriteClick(product, position);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productQuantity;
        public ImageButton favoriteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }

}
