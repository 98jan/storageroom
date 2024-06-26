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

import java.util.ArrayList;
import java.util.List;

public class ShoppingListProductActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter adapter;
    private List<Product> productList;
    private Button buttonSave;
    private Button buttonBack;
    private TextView textViewEmpty;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_product);

        // Initialize RecyclerView and adapter
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Handle item click here if needed (e.g., open detail view)
                Toast.makeText(ShoppingListProductActivity.this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveClicked(Product product) {
                // Implement save logic here if needed
                Toast.makeText(ShoppingListProductActivity.this, "Product saved: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                // Implement cancel logic here if needed
                finish();
            }
        });

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
        productList.add(new Product("12345", "Milk", "Fresh Milk", "1234567890123", "https://example.com/milk.jpg", "DairyX", null, "1 Liter", "Supermarket A", 4, true));
        productList.add(new Product("67890", "Bread", "Whole Wheat Bread", "9876543210987", "https://example.com/bread.jpg", "BakeryY", null, "1 Loaf", "Bakery B", 5, false));

        updateUI();
    }

    private void updateUI() {
        if (productList.isEmpty()) {
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
        for (Product product : productList) {
            int quantity = Integer.parseInt(product.getQuantity());
            if (quantity <= 0) {
                // Handle invalid quantity (for example, show a message)
                Toast.makeText(this, "Invalid quantity for product: " + product.getName(), Toast.LENGTH_SHORT).show();
                return; // Exit method or continue depending on your logic
            }
            // Perform save operation (e.g., update in database)
        }

        // If all validations pass, show success message
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }
}
