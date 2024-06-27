package com.iu.storageroom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.model.ShoppingListProduct;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListProductActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ShoppingListProductAdapter adapter;
    private List<ShoppingListProduct> shoppingListProductList;
    private Button buttonSave;
    private Button buttonBack;
    private TextView textViewEmpty;
    private EditText editTextProductName;
    private EditText editTextProductQuantity;
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
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductQuantity = findViewById(R.id.editTextProductQuantity);

        // Set click listeners
        buttonSave.setOnClickListener(v -> {
            saveData();
            clean();
        });
        buttonBack.setOnClickListener(v -> finish());

        // Retrieve userId from intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void clean() {
        editTextProductName.setText("");
        editTextProductQuantity.setText("0");
    }

    private void saveData() {
        // Validate and retrieve input values
        String productName = editTextProductName.getText().toString().trim();
        String quantityStr = editTextProductQuantity.getText().toString().trim();

        if (productName.isEmpty()) {
            Toast.makeText(this, "Please enter a product name", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Product object
        Product product = new Product();
        product.setName(productName); // Set the product name, you can set other fields as needed

        // Create a new ShoppingListProduct object
        ShoppingListProduct shoppingListProduct = new ShoppingListProduct(null, product, quantity);

        // Save shoppingListProduct to Firebase
        if (userId != null) {
            DatabaseReference ref = FirebaseUtil.mDatabaseReference.child("shoppinglist_products").child(userId).push();
            String key = ref.getKey();
            if (key != null) {
                shoppingListProduct.setKey(key);
                FirebaseUtil.saveData("shoppinglist_products/" + userId, shoppingListProduct, new FirebaseUtil.FirebaseCallback() {
                    @Override
                    public void onCallback(boolean isSuccess) {
                        if (isSuccess) {
                            Toast.makeText(ShoppingListProductActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
                            // Clear input fields after successful save
                            clean();
                        } else {
                            Toast.makeText(ShoppingListProductActivity.this, "Failed to save product", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCallback(List<Object> list) {
                        // Not used in this context
                    }
                });
            }
        }
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
}
