package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN_BARCODE = 1;

    private Product product;
    private Button btnReadBarcode;
    private Button btnDelete;
    private Button btnEdit;
    private Button btnSave;
    private EditText editTextProductName;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        btnReadBarcode = findViewById(R.id.btnReadBarcode);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        editTextProductName = findViewById(R.id.product_name);

        // Initialize Firebase
        FirebaseUtil.initializeFirebase();

        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            FirebaseUtil.openFbReference("products/" + userId);
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnReadBarcode.setOnClickListener(v -> readData());

        btnDelete.setOnClickListener(v -> deleteData(product));

        btnEdit.setOnClickListener(v -> updateData(product));

        btnSave.setOnClickListener(v -> {
            saveData();
            clean();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the product from the intent if it exists
        product = getIntent().getParcelableExtra("product");
        if (product != null) {
            editTextProductName.setText(product.getName());
        }
    }

    private void clean() {
        if (editTextProductName != null) {
            editTextProductName.setText("");
        }
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
        if (requestCode == REQUEST_CODE_SCAN_BARCODE && resultCode == RESULT_OK) {
            if (data != null) {
                product = data.getParcelableExtra("product");
                if (product != null) {
                    editTextProductName.setText(product.getName());
                }
            }
        }
    }

    private void saveData() {
        if (userId != null) {
            DatabaseReference reference = FirebaseUtil.mDatabaseReference.push();
            String key = reference.getKey();
            product = getIntent().getParcelableExtra("product");
            if (product != null) {
                product.setKey(key);  // Set the key to the product
                FirebaseUtil.saveData("products/" + userId, product, new FirebaseUtil.FirebaseCallback() {
                    @Override
                    public void onCallback(boolean isSuccess) {
                        if (isSuccess) {
                            // Data saved successfully
                            Toast.makeText(ProductActivity.this, getString(R.string.data_save_success), Toast.LENGTH_SHORT).show();

                            // Start ProductActivity and pass the product
                            Intent intent = new Intent(ProductActivity.this, ProductActivity.class);
                            intent.putExtra("product", product);
                            startActivity(intent);
                        } else {
                            // Failed to save data
                            Toast.makeText(ProductActivity.this, getString(R.string.data_save_fail), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCallback(List<Object> list) {
                        // Not used
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(Product product) {
        if (userId != null) {
            FirebaseUtil.updateData("products/" + userId, product.getKey(), product, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_update_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_update_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteData(Product product) {
        if (userId != null) {
            FirebaseUtil.deleteData("products/" + userId, product.getKey(), new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_delete_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_delete_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }
}
