package com.iu.storageroom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

public class StorageroomActivity extends AppCompatActivity {

    private EditText inputStorageroomName;
    private EditText inputDescription;
    private ImageView imageView;
    private Button btnSaveStorageroom;
    private Button btnRead;
    private TextView textView;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom);

        btnSaveStorageroom = findViewById(R.id.btnSaveStorageroom);
        inputStorageroomName = findViewById(R.id.storageroom_name);
        inputDescription = findViewById(R.id.storageroom_description);
        imageView = findViewById(R.id.storageroom_image);
        btnRead = findViewById(R.id.btnReaddata);
        textView = findViewById(R.id.textView);

        // Initialize Firebase
        FirebaseUtil.initializeFirebase();

        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            FirebaseUtil.openFbReference("storagerooms/" + userId);
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnRead.setOnClickListener(v -> readData());

        btnSaveStorageroom.setOnClickListener(v -> {
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
        inputStorageroomName.setText("");
        inputDescription.setText("");
    }

    private void saveData() {
        if (userId != null) {
            DatabaseReference reference = FirebaseUtil.mDatabaseReference.push();
            String key = reference.getKey();
            long timeInTwoWeeks = 14 * 24 * 60 * 60 * 1000;
            Storageroom storageroom = new Storageroom(key, "Büro", "image", System.currentTimeMillis(), System.currentTimeMillis() + timeInTwoWeeks, 1, 2, new Product());
            FirebaseUtil.saveData("storagerooms/" + userId, storageroom, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(StorageroomActivity.this, getString(R.string.data_save_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StorageroomActivity.this, getString(R.string.data_save_fail), Toast.LENGTH_SHORT).show();
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
            FirebaseUtil.readData("storagerooms/" + userId, Storageroom.class, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                }

                @Override
                public void onCallback(List<Object> list) {

                    if (list != null) {
                        StringBuilder dataBuilder = new StringBuilder();
                        for (Object object : list) {
                            if (object instanceof Storageroom storageroom) {
                                dataBuilder.append(storageroom).append("\n");
                            }
                        }
                        textView.setText(dataBuilder.toString());
                    }
                    else {
                        Toast.makeText(StorageroomActivity.this, getString(R.string.data_read_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(Storageroom storageroom) {
        if (userId != null) {
            FirebaseUtil.updateData("storagerooms/" + userId, storageroom.getKey(), storageroom, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    Toast.makeText(StorageroomActivity.this, getString(R.string.data_update_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCallback(List<Object> list) {
                    Toast.makeText(StorageroomActivity.this, getString(R.string.data_update_fail), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteData(Storageroom storageroom) {
        if (userId != null) {
            FirebaseUtil.deleteData("storagerooms/" + userId, storageroom.getKey(), new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    Toast.makeText(StorageroomActivity.this, getString(R.string.data_delete_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCallback(List<Object> list) {
                    Toast.makeText(StorageroomActivity.this, getString(R.string.data_delete_fail), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }
}
