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

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying and managing storage rooms.
 */
public class StorageroomOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StorageroomAdapter adapter;
    private List<Storageroom> storageroomList;
    private FloatingActionButton createStorageroom;
    private TextView emptyView;
    private String userId;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_overview);

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        createStorageroom = findViewById(R.id.createStorageroomButton);

        storageroomList = new ArrayList<>();
        adapter = new StorageroomAdapter(this, storageroomList, this::editStorageroom, this::deleteStorageroom, this::openProductOverview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set click listener for create storage room button
        createStorageroom.setOnClickListener(v -> createStorageroomView());

        // Check if userId is provided
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
                        if (object instanceof Storageroom) {
                            Storageroom storageroom = (Storageroom) object;
                            // Add key and name of each storageroom to the list
                            storageroomList.add(storageroom);
                        }
                    }
                    updateUI();
                }else{
                    updateUI();
                }
        });
    }

    /**
     * Updates the UI based on the storage room list.
     * If the list is empty, shows the empty view; otherwise, displays the recycler view.
     */
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

    /**
     * Opens the activity to create a new storage room.
     */
    private void createStorageroomView() {
        Intent intent = new Intent(getApplicationContext(), StorageroomActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    /**
     * Opens the activity to edit a storage room.
     *
     * @param storageroom the storage room to edit
     */
    private void editStorageroom(Storageroom storageroom) {
        Intent intent = new Intent(getApplicationContext(), StorageroomActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("editMode", true);
        intent.putExtra("storageroomKey", storageroom.getKey());
        intent.putExtra("storageroomName", storageroom.getName());
        intent.putExtra("storageroomIcon", storageroom.getSelectedIcon());
        startActivity(intent);
    }

    /**
     * Deletes a storage room from Firebase.
     *
     * @param storageroom the storage room to delete
     */
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

    /**
     * Opens the activity to view products in a storage room.
     *
     * @param storageroom the storage room to view products for
     */
    private void openProductOverview(Storageroom storageroom) {
        Intent intent = new Intent(this, ProductOverviewActivity.class);
        intent.putExtra("storageroomKey", storageroom.getKey());
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomName", storageroom.getName());
        startActivity(intent);
    }
}