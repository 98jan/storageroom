package com.iu.storageroom;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iu.storageroom.model.ShoppingListProduct;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListProductActivity extends AppCompatActivity {

    private EditText editTextProductName;
    private EditText editTextProductQuantity;
    private RecyclerView recyclerViewProducts;
    private ShoppingListProductAdapter adapter;
    private List<ShoppingListProduct> shoppingListProductList;
    private Button buttonSave;
    private Button buttonBack;
    private TextView textViewEmpty;
    private String userId;
    private String shoppinglistKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist_product);

        // Initialize Views
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductQuantity = findViewById(R.id.editTextProductQuantity);
        recyclerViewProducts = findViewById(R.id.recyclerViewShoppingListProducts);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);

        // Initialize RecyclerView and Adapter
        shoppingListProductList = new ArrayList<>();
        adapter = new ShoppingListProductAdapter(this, shoppingListProductList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);

        // Set Click Listeners
        buttonSave.setOnClickListener(v -> saveData());
        buttonBack.setOnClickListener(v -> finish());

        // Retrieve userId and shoppinglistKey from Intent
        userId = getIntent().getStringExtra("userId");
        shoppinglistKey = getIntent().getStringExtra("shoppinglistKey");

        if (userId == null || shoppinglistKey == null) {
            Toast.makeText(this, "User ID or Shopping List Key not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing products from Firebase
        loadShoppingListProducts();

        // Set click listener for edit button in adapter
        adapter.setOnItemClickListener(new ShoppingListProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click if needed
            }

            @Override
            public void onEditClick(int position) {
                ShoppingListProduct product = shoppingListProductList.get(position);
                editTextProductName.setText(product.getProductName());
                editTextProductQuantity.setText(String.valueOf(product.getQuantity()));

                // Update the save button to update instead of saving new
                buttonSave.setText("Update"); // Change button text to indicate update action

                // Set a tag on the save button to indicate that it is in update mode
                buttonSave.setTag(product); // Tag the button with the product to be updated
                buttonSave.setOnClickListener(v -> updateData(product)); // Set click listener for update action
            }

            @Override
            public void onDeleteClick(int position) {
                ShoppingListProduct deletedProduct = shoppingListProductList.remove(position);
                DatabaseReference productRef = FirebaseUtil.mDatabaseReference
                        .child("shoppinglist_products")
                        .child(userId)
                        .child(deletedProduct.getKey());
                productRef.removeValue(); // Delete from Firebase
                adapter.notifyItemRemoved(position); // Notify adapter
            }
        });
    }

    private void updateData(ShoppingListProduct productToUpdate) {
        // Retrieve updated input data
        String updatedProductName = editTextProductName.getText().toString().trim();
        String updatedProductQuantityStr = editTextProductQuantity.getText().toString().trim();

        // Validate updated input
        if (updatedProductName.isEmpty()) {
            editTextProductName.setError("Product name cannot be empty");
            editTextProductName.requestFocus();
            return;
        }

        int updatedProductQuantity;
        try {
            updatedProductQuantity = Integer.parseInt(updatedProductQuantityStr);
            if (updatedProductQuantity <= 0) {
                editTextProductQuantity.setError("Product quantity must be greater than zero");
                editTextProductQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editTextProductQuantity.setError("Enter a valid number for quantity");
            editTextProductQuantity.requestFocus();
            return;
        }

        // Update the existing ShoppingListProduct object
        productToUpdate.setProductName(updatedProductName);
        productToUpdate.setQuantity(updatedProductQuantity);


        DatabaseReference productRef = FirebaseUtil.mDatabaseReference
                .child(shoppinglistKey)
                .child("shoppinglist_products")
                .child(productToUpdate.getKey()); // Use the existing product key
        productRef.setValue(productToUpdate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ShoppingListProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear input fields after successful update
                    buttonSave.setText("Save"); // Change button text back to "Save" after update
                    buttonSave.setTag(null); // Clear the button tag after update
                    buttonSave.setOnClickListener(v -> saveData()); // Set click listener for save action
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ShoppingListProductActivity.this, "Failed to update product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveData() {
        // Retrieve input data
        String productName = editTextProductName.getText().toString().trim();
        String productQuantityStr = editTextProductQuantity.getText().toString().trim();

        // Validate input
        if (productName.isEmpty()) {
            editTextProductName.setError("Product name cannot be empty");
            editTextProductName.requestFocus();
            return;
        }

        int productQuantity;
        try {
            productQuantity = Integer.parseInt(productQuantityStr);
            if (productQuantity <= 0) {
                editTextProductQuantity.setError("Product quantity must be greater than zero");
                editTextProductQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editTextProductQuantity.setError("Enter a valid number for quantity");
            editTextProductQuantity.requestFocus();
            return;
        }

        // Create a new ShoppingListProduct object
        ShoppingListProduct shoppingListProduct = new ShoppingListProduct(shoppinglistKey, productName, productQuantity);

        // Save to Firebase under shoppinglist_products -> userId
        DatabaseReference productsRef = FirebaseUtil.mDatabaseReference
                .child(shoppinglistKey)
                .child("shoppinglist_products")
                .push(); // Push creates a unique key for the product
        shoppingListProduct.setKey(productsRef.getKey()); // Set the key locally

        // Set value to Firebase
        productsRef.setValue(shoppingListProduct)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ShoppingListProductActivity.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear input fields after successful save
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ShoppingListProductActivity.this, "Failed to save product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadShoppingListProducts() {
        DatabaseReference ref = FirebaseUtil.mDatabaseReference.child(shoppinglistKey).child("shoppinglist_products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingListProductList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    ShoppingListProduct product = productSnapshot.getValue(ShoppingListProduct.class);
                    shoppingListProductList.add(product);
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShoppingListProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        editTextProductName.setText("");
        editTextProductQuantity.setText("");
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
