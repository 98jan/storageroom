package com.iu.storageroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.ShoppingListProduct;

import java.util.List;

public class ShoppingListProductAdapter extends RecyclerView.Adapter<ShoppingListProductAdapter.ViewHolder> {
    private Context context;
    private List<ShoppingListProduct> shoppingListProductList;

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
        holder.productName.setText(shoppingListProduct.getProduct().getName());
        holder.productQuantity.setText(String.valueOf(shoppingListProduct.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return shoppingListProductList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}
