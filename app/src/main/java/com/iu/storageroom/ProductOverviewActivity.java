package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductOverviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private FloatingActionButton createProductButton;
    private FloatingActionButton redirectToShoppingList;
    private FloatingActionButton backButton;
    private TextView emptyView;
    private TextView headerTextView;
    private String storageroomKey;
    private String userId;
    private String storageroomName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);

        recyclerView = findViewById(R.id.productRecyclerView);
        emptyView = findViewById(R.id.productEmptyView);
        headerTextView = findViewById(R.id.headerTextView);

        createProductButton = findViewById(R.id.createProductButton);
        redirectToShoppingList = findViewById(R.id.shoppingListActionButton);
        backButton = findViewById(R.id.backButton);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        storageroomKey = getIntent().getStringExtra("storageroomKey");
        userId = getIntent().getStringExtra("userId");
        storageroomName = getIntent().getStringExtra("storageroomName");


        if (storageroomKey == null || userId == null) {
            Toast.makeText(this, getString(R.string.invalid_storageroom), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (storageroomName != null) {
            headerTextView.setText(storageroomName);
        }

        redirectToShoppingList.setOnClickListener(v -> openShoppingListActivity());

        createProductButton.setOnClickListener(v -> openAddProductActivity());

        backButton.setOnClickListener(v -> finish());

        // Fetch products from Firebase for the selected storageroom
        FirebaseUtil.readData("products/" + userId + "/" + storageroomKey, Product.class, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                // Not used
            }

            @Override
            public void onCallback(List<Object> list) {
                if (list != null) {
                    productList.clear();
                    for (Object object : list) {
                        if (object instanceof Product product) {
                            productList.add(product);
                        }
                    }
                    updateUI();
                } else {
                    updateUI();
                }
            }
        });
    }

    private void updateUI() {
        if (productList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void openShoppingListActivity() {
        Intent intent = new Intent(this, ShoppingListOverviewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        startActivity(intent);
    }

    private void openAddProductActivity() {
        Intent intent = new Intent(this,ProductActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        startActivity(intent);
    }
}
