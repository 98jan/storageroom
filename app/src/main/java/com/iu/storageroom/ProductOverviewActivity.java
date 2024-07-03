package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Activity class to display and manage products in a storage room.
 */
public class ProductOverviewActivity extends AppCompatActivity implements ProductAdapter.OnFavoriteClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
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

        // Initialize UI elements
        initUI();

        // Retrieve data from Intent
        retrieveIntentData();

        // Check validity of storageroomKey and userId
        if (storageroomKey == null || userId == null) {
            Toast.makeText(this, getString(R.string.invalid_storageroom), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set storageroomName if available
        if (storageroomName != null) {
            headerTextView.setText(storageroomName);
        }

        // Setup click listeners for buttons
        setupListeners();

        // Initialize and set up the ProductAdapter
        adapter = new ProductAdapter(this, productList, this, this::editProduct, this::deleteProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch products from Firebase
        fetchProductsFromFirebase();
    }

    /**
     * Initializes UI elements from layout.
     */
    private void initUI() {
        recyclerView = findViewById(R.id.productRecyclerView);
        emptyView = findViewById(R.id.productEmptyView);
        headerTextView = findViewById(R.id.headerTextView);
        createProductButton = findViewById(R.id.createProductButton);
        redirectToShoppingList = findViewById(R.id.shoppingListActionButton);
        backButton = findViewById(R.id.backButton);
    }

    /**
     * Retrieves data passed via Intent.
     */
    private void retrieveIntentData() {
        storageroomKey = getIntent().getStringExtra("storageroomKey");
        userId = getIntent().getStringExtra("userId");
        storageroomName = getIntent().getStringExtra("storageroomName");
    }

    /**
     * Sets up click listeners for floating action buttons.
     */
    private void setupListeners() {
        redirectToShoppingList.setOnClickListener(v -> openShoppingListActivity());
        createProductButton.setOnClickListener(v -> openAddProductActivity());
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductOverviewActivity.this, StorageroomOverviewActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Fetches products associated with the current storage room from Firebase.
     */
    private void fetchProductsFromFirebase() {
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
                        if (object instanceof Product) {
                            Product product = (Product) object;
                            productList.add(product);
                        }
                    }
                    updateUI();
                } else {
                    Log.d("ProductOverview", "No products found.");
                    updateUI();
                }
            }
        });
    }

    /**
     * Updates the UI based on the current state of productList.
     */
    private void updateUI() {
        if (productList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateProductList(productList);
        }
    }

    /**
     * Opens the ShoppingListOverviewActivity to view the shopping list associated with the current storage room.
     */
    private void openShoppingListActivity() {
        Intent intent = new Intent(this, ShoppingListOverviewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        startActivity(intent);
    }

    /**
     * Opens the ProductActivity to add a new product to the current storage room.
     */
    private void openAddProductActivity() {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        startActivity(intent);
    }

    /**
     * Opens the ProductActivity to edit an existing product.
     *
     * @param product The product to edit.
     */
    private void editProduct(Product product) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        intent.putExtra("storageroomName", storageroomName);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    /**
     * Deletes the specified product from Firebase and updates the UI accordingly.
     *
     * @param product The product to delete.
     */
    private void deleteProduct(Product product) {
        String key = product.getKey();
        if (key != null) {
            FirebaseUtil.deleteData("products/" + userId + "/" + storageroomKey , key, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    runOnUiThread(() -> {
                        if (isSuccess) {
                            productList.remove(product);
                            updateUI();
                            Toast.makeText(ProductOverviewActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductOverviewActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used in this context
                }
            });
        } else {
            Toast.makeText(this, "Failed to delete product: Key is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles click events on the favorite button of a product.
     *
     * @param product  The product whose favorite status has been changed.
     * @param position The position of the product in the list.
     */
    @Override
    public void onFavoriteClick(Product product, int position) {
        product.setFavourite(!product.isFavourite());

        FirebaseUtil.updateProductFavouriteStatus(userId, storageroomKey, product.getKey(), product.isFavourite(), new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                if (isSuccess) {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(ProductOverviewActivity.this, "Favoritenstatus aktualisiert", Toast.LENGTH_SHORT).show();
                } else {
                    product.setFavourite(!product.isFavourite()); // Rückgängigmachen der Änderung im Fehlerfall
                    adapter.notifyItemChanged(position);
                    Toast.makeText(ProductOverviewActivity.this, "Favoritenstatus konnte nicht aktualisiert werden", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCallback(List<Object> list) {
                // Not relevant in this context
            }
        });
    }
}