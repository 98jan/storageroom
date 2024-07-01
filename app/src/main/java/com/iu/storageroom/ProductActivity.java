package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.Arrays;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN_BARCODE = 1;

    private Product product;
    private EditText editTextProductName;
    private Spinner productGroupSpinner;
    private EditText editTextProductNote;
    private EditText editTextProductBarcode;
    private EditText editTextProductImageUrl;
    private EditText editTextProductBrand;
    private EditText editTextProductQuantity;
    private EditText editTextProductStore;
    private EditText editTextProductRating;
    private CheckBox checkBoxProductFavourite;
    private Button btnReadBarcode;
    private Button btnDelete;
    private Button btnEdit;
    private Button btnSave;
    private ImageView imageView;

    private String userId;
    private String storageroomKey;

    // Product groups
    private String[] productGroupIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initUI();

        FirebaseUtil.initializeFirebase();

        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            FirebaseUtil.openFbReference("products/" + userId);
        } else {
            showToast(R.string.user_not_auth);
            finish();
            return;
        }

        storageroomKey = getIntent().getStringExtra("storageroomKey");

        initializeProductGroups();
        setupListeners();

        product = getIntent().getParcelableExtra("product");
        if (product != null) {
            populateFields(product);
        }
    }

    private void initUI() {
        editTextProductName = findViewById(R.id.product_name);
        productGroupSpinner = findViewById(R.id.productGroupSpinner);
        editTextProductNote = findViewById(R.id.product_note);
        editTextProductBarcode = findViewById(R.id.product_barcode);
        editTextProductImageUrl = findViewById(R.id.product_image_url);
        editTextProductBrand = findViewById(R.id.product_brand);
        editTextProductQuantity = findViewById(R.id.product_quantity);
        editTextProductStore = findViewById(R.id.product_store);
        editTextProductRating = findViewById(R.id.product_rating);
        checkBoxProductFavourite = findViewById(R.id.product_favourite);
        btnReadBarcode = findViewById(R.id.btnReadBarcode);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        imageView = findViewById(R.id.imageView); // Initialize the ImageView
    }

    private void initializeProductGroups() {
        productGroupIds = new String[]{
                getString(R.string.product_group_placeholder),
                getString(R.string.product_group_food),
                getString(R.string.product_group_housekeeping),
                getString(R.string.product_group_cosmetics),
                getString(R.string.product_group_baby_child),
                getString(R.string.product_group_others)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productGroupIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productGroupSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        btnReadBarcode.setOnClickListener(v -> readData());
        btnDelete.setOnClickListener(v -> {
            if (product != null) {
                deleteData(product);
            } else {
                showToast(R.string.product_not_found);
            }
        });
        btnEdit.setOnClickListener(v -> {
            if (product != null) {
                updateData(product);
            } else {
                showToast(R.string.product_not_found);
            }
        });
        btnSave.setOnClickListener(v -> {
            saveData();
            cleanFields();
        });
    }

    private void populateFields(Product product) {
        editTextProductName.setText(product.getName());
        editTextProductNote.setText(product.getNote());
        editTextProductBrand.setText(product.getBrand());
        editTextProductQuantity.setText(product.getQuantity());
        editTextProductStore.setText(product.getStore());
        editTextProductBarcode.setText(product.getBarcode());
        editTextProductImageUrl.setText(product.getImageUrl());
        editTextProductRating.setText(String.valueOf(product.getRating()));
        checkBoxProductFavourite.setChecked(product.isFavourite());

        // Set the spinner selection based on the product's first category
        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
            String productCategory = product.getCategories().get(0);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) productGroupSpinner.getAdapter();
            int spinnerPosition = adapter.getPosition(productCategory);
            if (spinnerPosition >= 0) {
                productGroupSpinner.setSelection(spinnerPosition);
            }
        }

        // Display product image if available
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this).load(product.getImageUrl()).into(imageView);
        }
    }

    private void cleanFields() {
        editTextProductName.setText("");
        productGroupSpinner.setSelection(0);
        editTextProductNote.setText("");
        editTextProductBarcode.setText("");
        editTextProductImageUrl.setText("");
        editTextProductBrand.setText("");
        editTextProductQuantity.setText("");
        editTextProductStore.setText("");
        editTextProductRating.setText("");
        checkBoxProductFavourite.setChecked(false);
    }

    private void readData() {
        if (userId != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN_BARCODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_BARCODE && resultCode == RESULT_OK && data != null) {
            product = data.getParcelableExtra("product");
            if (product != null) {
                populateFields(product);
            }
        }
    }

    private void saveData() {
        if (userId != null && storageroomKey != null) {
            // Retrieve product details from UI
            String productName = editTextProductName.getText().toString();
            String productGroup = (String) productGroupSpinner.getSelectedItem();
            String productNote = editTextProductNote.getText().toString();
            String productBarcode = editTextProductBarcode.getText().toString();
            String productImageUrl = editTextProductImageUrl.getText().toString();
            String productBrand = editTextProductBrand.getText().toString();
            String productQuantity = editTextProductQuantity.getText().toString();
            String productStore = editTextProductStore.getText().toString();

            String productRatingText = editTextProductRating.getText().toString();
            int productRating;
            try {
                productRating = Integer.parseInt(productRatingText);
            } catch (NumberFormatException e) {
                productRating = 0;
            }

            boolean productFavourite = checkBoxProductFavourite.isChecked();

            // Check if the product is new or existing
            // Check if the product is new or existing
            if (product == null || product.getKey() == null) {
                product = new Product(null, productName, productNote, productBarcode, productImageUrl,
                        productBrand, Arrays.asList(productGroup), productQuantity, productStore, productRating, productFavourite);
                saveNewData(product);
            } else {
                // Use the existing productKey
                product = new Product(product.getKey(), productName, productNote, productBarcode, productImageUrl,
                        productBrand, Arrays.asList(productGroup), productQuantity, productStore, productRating, productFavourite);
                updateData(product);
            }
        } else {
            showToast(R.string.user_not_auth);
        }
    }

    private void saveNewData(Product product) {
        if (userId != null) {
            FirebaseUtil.saveData("products/" + userId + "/" + storageroomKey, product, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_save_success), Toast.LENGTH_SHORT).show();
                        finish(); // Return to the overview activity
                    } else {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_save_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used in this context
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(Product product) {
        if (userId != null && product != null) {
            FirebaseUtil.updateData("products/" + userId + "/" + storageroomKey, product, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    showToast(isSuccess ? R.string.data_update_success : R.string.data_update_fail);
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used
                }
            });
        } else {
            showToast(R.string.user_not_auth);
        }
    }

    private void deleteData(Product product) {
        if (userId != null && product != null) {
            FirebaseUtil.deleteData("products/" + userId + "/" + storageroomKey, product.getKey(), new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    showToast(isSuccess ? R.string.data_delete_success : R.string.data_delete_fail);
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used
                }
            });
        } else {
            showToast(R.string.user_not_auth);
        }
    }

    private void showToast(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    private void returnToProductOverview() {
        Intent intent = new Intent(ProductActivity.this, ProductOverviewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        startActivity(intent);
        finish();
    }
}