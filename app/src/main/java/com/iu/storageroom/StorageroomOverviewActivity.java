package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageroomOverviewActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ListView listView;
    private ImageView imageView;
    private List<String> storageroomList;
    private ArrayAdapter<String> adapter;

    private FloatingActionButton createStorageroom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_overview);

        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.imageView);
        storageroomList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storageroomList);
        listView.setAdapter(adapter);
        createStorageroom = findViewById(R.id.createStorageroomButton);
        imageView.setOnClickListener(v -> openFileChooser());
        createStorageroom.setOnClickListener(v -> createStorageroomView());

        // Retrieve storageroom data from Firebase
        FirebaseUtil.readData("storagerooms/" + getIntent().getStringExtra("userId"), Storageroom.class, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                // Not used for reading data
            }

            @Override
            public void onCallback(List<Object> list) {
                if (list != null) {
                    for (Object object : list) {
                        if (object instanceof Storageroom) {
                            Storageroom storageroom = (Storageroom) object;
                            // Add key and name of each storageroom to the list
                            storageroomList.add("Key: " + storageroom.getKey() + ", Name: " + storageroom.getName() + ", Image: " + storageroom.getImageUrl());
                        }
                    }
                    // Notify adapter about data change
                    adapter.notifyDataSetChanged();
                    String imageUrl = "https://firebasestorage.googleapis.com/v0/b/storageroom-db50a.appspot.com/o/storageroom%2Fstorage-rooms_small.png?alt=media&token=f64e3646-0ff3-4e1b-a54a-1219be9af8ec";
                    Glide.with(StorageroomOverviewActivity.this)
                            .load(imageUrl)
                            .into(imageView);
                }

                    // Load image, if exists
                    /*
                    if (!storageroomList.isEmpty() && storageroomList.get(0) != null && !storageroomList.get(0).isEmpty()) {
                        Glide.with(StorageroomOverviewActivity.this)
                                .load(storageroomList.get(0))
                                .into(imageView);
                    }
                     */
                }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void createStorageroomView() {
        Intent intent = new Intent(getApplicationContext(), StorageroomActivity.class);
        intent.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(intent);
        finish();
    }
}
