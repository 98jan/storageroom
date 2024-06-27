package com.iu.storageroom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iu.storageroom.model.Product;
import com.iu.storageroom.model.ShoppingListProduct;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListProductActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ShoppingListProductAdapter adapter;
    private List<ShoppingListProduct> shoppingListProductList;
    private Button buttonSave;
    private Button buttonBack;
    private TextView textViewEmpty;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist_product);

        // Initialize RecyclerView and adapter
        recyclerViewProducts = findViewById(R.id.recyclerViewShoppingListProducts);
        shoppingListProductList = new ArrayList<>();
        adapter = new ShoppingListProductAdapter(this, shoppingListProductList);

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);

        // Initialize buttons and empty view
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);
        textViewEmpty = findViewById(R.id.textViewEmpty);

        // Set click listeners
        buttonSave.setOnClickListener(v -> saveData());
        buttonBack.setOnClickListener(v -> finish());

        // Retrieve userId from intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mock data (replace with actual data retrieval)
        loadMockData();
    }

    private void loadMockData() {
        // Simulated data, replace with actual data retrieval logic
        shoppingListProductList.add(new ShoppingListProduct(null, new Product("12345", "Milk", "Fresh Milk", "1234567890123", "https://example.com/milk.jpg", "DairyX", null, "1 Liter", "Supermarket A", 4, true), 2));
        shoppingListProductList.add(new ShoppingListProduct(null, new Product("67890", "Bread", "Whole Wheat Bread", "9876543210987", "https://example.com/bread.jpg", "BakeryY", null, "1 Loaf", "Bakery B", 5, false), 1));

        updateUI();
    }

    private void updateUI() {
        if (shoppingListProductList.isEmpty()) {
            recyclerViewProducts.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewProducts.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void saveData() {
        // Example: Saving product quantity
        for (ShoppingListProduct shoppingListProduct : shoppingListProductList) {
            int quantity = shoppingListProduct.getQuantity();
            if (quantity <= 0) {
                // Handle invalid quantity (for example, show a message)
                Toast.makeText(this, "Invalid quantity for product: " + shoppingListProduct.getProduct().getName(), Toast.LENGTH_SHORT).show();
                return; // Exit method or continue depending on your logic
            }
            // Perform save operation (e.g., update in database)
        }

        // If all validations pass, show success message
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }
}
