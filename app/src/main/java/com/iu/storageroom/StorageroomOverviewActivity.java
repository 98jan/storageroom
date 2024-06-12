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
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageroomOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StorageroomAdapter adapter;
    private List<Storageroom> storageroomList;
    private FloatingActionButton createStorageroom;
    private TextView emptyView;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_overview);

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        createStorageroom = findViewById(R.id.createStorageroomButton);

        storageroomList = new ArrayList<>();
        adapter = new StorageroomAdapter(this, storageroomList, this::editStorageroom, this::deleteStorageroom);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        createStorageroom.setOnClickListener(v -> createStorageroomView());

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrieve storageroom data from Firebase
        FirebaseUtil.readData("storagerooms/" + getIntent().getStringExtra("userId"), Storageroom.class, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                // Not used for reading data
            }

            @Override
            public void onCallback(List<Object> list) {
                if (list != null) {
                    storageroomList.clear();
                    for (Object object : list) {
                        if (object instanceof Storageroom storageroom) {
                            // Add key and name of each storageroom to the list
                            storageroomList.add(storageroom);
                        }
                    }
                    updateUI();
                }else{
                    updateUI();
                }
            }
        });
    }

    private void updateUI() {
        if (storageroomList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void createStorageroomView() {
        Intent intent = new Intent(getApplicationContext(), StorageroomActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void editStorageroom(Storageroom storageroom) {
        Intent intent = new Intent(getApplicationContext(), StorageroomActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("editMode", true);
        intent.putExtra("storageroomKey", storageroom.getKey());
        intent.putExtra("storageroomName", storageroom.getName());
        intent.putExtra("storageroomIcon", storageroom.getSelectedIcon());
        startActivity(intent);
    }

    private void deleteStorageroom(Storageroom storageroom) {
        String key = storageroom.getKey();
        if (key != null) {
            FirebaseUtil.deleteData("storagerooms/" + userId, key, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    runOnUiThread(() -> {
                        if (isSuccess) {
                            storageroomList.remove(storageroom);
                            updateUI();
                            Toast.makeText(StorageroomOverviewActivity.this, "Storageroom deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StorageroomOverviewActivity.this, "Failed to delete storageroom", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used in this context
                }
            });
        } else {
            Toast.makeText(this, "Failed to delete storageroom: Key is null", Toast.LENGTH_SHORT).show();
        }
    }
}