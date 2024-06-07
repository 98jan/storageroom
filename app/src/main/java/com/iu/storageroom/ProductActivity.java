package com.iu.storageroom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private EditText input;
    private Button btnSaveData;
    private Button btnReadData;
    private TextView textView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        btnSaveData = findViewById(R.id.btn);
        input = findViewById(R.id.input);
        btnReadData = findViewById(R.id.btnReaddata);
        textView = findViewById(R.id.textView);

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

        btnReadData.setOnClickListener(v -> readData());

        btnSaveData.setOnClickListener(v -> {
            saveData();
            clean();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void clean() {
        input.setText("");
    }

    private void saveData() {
        if (userId != null) {
            DatabaseReference reference = FirebaseUtil.mDatabaseReference.push();
            String key = reference.getKey();
            Product product = new Product(key, "Kartoffel", "Ungesund", "00-00-000", "test.com", 2, true);
            FirebaseUtil.saveData("products/" + userId, product, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        // Daten wurden erfolgreich gespeichert
                        Toast.makeText(ProductActivity.this, getString(R.string.data_save_success), Toast.LENGTH_SHORT).show();
                    } else {
                        // Fehler beim Speichern der Daten
                        Toast.makeText(ProductActivity.this, getString(R.string.data_save_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCallback(List<Object> list) {

                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void readData() {
        if (userId != null) {
            FirebaseUtil.readData("products/" + userId, Product.class, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                }

                @Override
                public void onCallback(List<Object> list) {
                    if (list!=null) {
                        StringBuilder dataBuilder = new StringBuilder();
                        for (Object object : list) {
                            if (object instanceof Product product){
                                dataBuilder.append(product).append("\n");
                            }
                        }
                        textView.setText(dataBuilder.toString());
                    } else {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_read_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(Product product) {
        if (userId != null) {
            FirebaseUtil.updateData("products/" + userId, product.getKey(), product, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    Toast.makeText(ProductActivity.this, getString(R.string.data_update_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCallback(List<Object> list) {
                    Toast.makeText(ProductActivity.this, getString(R.string.data_update_fail), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProductActivity.this, getString(R.string.data_delete_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCallback(List<Object> list) {
                    Toast.makeText(ProductActivity.this, getString(R.string.data_delete_fail), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }
}
