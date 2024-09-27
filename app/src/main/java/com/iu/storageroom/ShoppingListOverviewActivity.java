package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iu.storageroom.model.ShoppingList;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying and managing shopping lists.
 */
public class ShoppingListOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private List<ShoppingList> shoppingListCollection;
    private FloatingActionButton createShoppingList;
    private TextView emptyView;
    private FloatingActionButton backButton;
    private String userId;
    private String storageroomKey;
    private String storageroomName;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist_overview);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        storageroomKey = getIntent().getStringExtra("storageroomKey");
        storageroomName = getIntent().getStringExtra("storageroomName");

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        backButton = findViewById(R.id.btnCancel);
        createShoppingList = findViewById(R.id.createShoppingListButton);

        shoppingListCollection = new ArrayList<>();
        adapter = new ShoppingListAdapter(this, shoppingListCollection, this::editShoppingList, this::deleteShoppingList, this::openShoppingList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set click listener for create shopping list button
        createShoppingList.setOnClickListener(v -> createShoppingListView());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingListOverviewActivity.this, ProductOverviewActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("storageroomKey", storageroomKey);
            intent.putExtra("storageroomName", storageroomName);
            startActivity(intent);
            finish();
        });

        // Check if userId is provided
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrieve shopping list data from Firebase
        FirebaseUtil.readData("shoppinglists/" + userId  + "/" + storageroomKey, ShoppingList.class, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                // Not used for reading data
            }

            @Override
            public void onCallback(List<Object> list) {
                if (list != null) {
                    shoppingListCollection.clear();
                    for (Object object : list) {
                        if (object instanceof ShoppingList) {
                            shoppingListCollection.add((ShoppingList) object);
                        }
                    }
                    updateUI();
                } else {
                    updateUI();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Handle logout action
            //Toast.makeText(this, getString(R.string.logout_description), Toast.LENGTH_SHORT).show();
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseUtil.signOut();
        navigateToLoginActivity();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(ShoppingListOverviewActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Updates the UI based on the shopping list.
     * If the list is empty, shows the empty view; otherwise, displays the recycler view.
     */
    private void updateUI() {
        if (shoppingListCollection.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Opens the activity to create a new shopping list.
     */
    private void createShoppingListView() {
        Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        intent.putExtra("storageroomName", storageroomName);
        startActivity(intent);
    }

    /**
     * Opens the activity to edit a shopping list.
     *
     * @param shoppingList the shopping list to edit
     */
    private void editShoppingList(ShoppingList shoppingList) {
        Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("editMode", true);
        intent.putExtra("shoppinglistKey", shoppingList.getKey());
        intent.putExtra("shoppinglistName", shoppingList.getName());
        intent.putExtra("shoppinglistIcon", shoppingList.getSelectedIcon());
        intent.putExtra("storageroomKey", storageroomKey);
        startActivity(intent);
    }

    /**
     * Deletes a shopping list from Firebase.
     *
     * @param shoppingList the shopping list to delete
     */
    private void deleteShoppingList(ShoppingList shoppingList) {
        String key = shoppingList.getKey();
        if (key != null) {
            FirebaseUtil.deleteData("shoppinglists/" + userId  + "/" + storageroomKey, key, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        runOnUiThread(() -> {
                            shoppingListCollection.remove(shoppingList);
                            updateUI();
                            Toast.makeText(ShoppingListOverviewActivity.this, "Shopping list deleted successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(ShoppingListOverviewActivity.this, "Failed to delete shopping list", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used in this context
                }
            });
        } else {
            Toast.makeText(this, "Failed to delete shopping list: Key is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the activity to view products in a shopping list.
     *
     * @param shoppingList the shopping list to view products for
     */
    private void openShoppingList(ShoppingList shoppingList) {
        Intent intent = new Intent(this, ShoppingListProductActivity.class);
        intent.putExtra("shoppinglistKey", shoppingList.getKey());
        intent.putExtra("storageroomName", storageroomName);
        intent.putExtra("storageroomKey", storageroomKey);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
