package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class StorageroomOverviewActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> storageroomList;
    private ArrayAdapter<String> adapter;

    private FloatingActionButton createStorageroom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_overview);

        listView = findViewById(R.id.listView);
        storageroomList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storageroomList);
        listView.setAdapter(adapter);
        createStorageroom = findViewById(R.id.createStorageroomButton);

        createStorageroom.setOnClickListener(v -> createStorageroomView());

        // Retrieve storageroom data from Firebase
        FirebaseUtil.readData("storagerooms/" + FirebaseUtil.getCurrentUser().getUid(), Storageroom.class, new FirebaseUtil.FirebaseCallback() {
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
                            storageroomList.add("Key: " + storageroom.getKey() + ", Name: " + storageroom.getName());
                        }
                    }
                    // Notify adapter about data change
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void createStorageroomView() {
        Intent intent = new Intent(getApplicationContext(), StorageroomActivity.class);
        FirebaseUser user = FirebaseUtil.getCurrentUser();
        intent.putExtra("userId", user.getUid());
        startActivity(intent);
        finish();
    }
}
