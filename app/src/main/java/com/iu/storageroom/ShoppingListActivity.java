package com.iu.storageroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.iu.storageroom.model.ShoppingList;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

/**
 * Activity class for managing shopping lists.
 */
public class ShoppingListActivity extends AppCompatActivity {

    private EditText inputShoppingListName;
    private Spinner iconSpinner;
    private Button btnSaveShoppingList;
    private Button btnCancel;

    private int[] iconResIds = {
            R.drawable.image_placeholder_icon,
            R.drawable.home_icon,
            R.drawable.work_icon,
            R.drawable.home_work_icon,
            R.drawable.fitness_center_icon,
            R.drawable.mode_cool_icon,
    };

    private int selectedIconResId;
    private boolean editMode;
    private String userId;
    private String previousActivity;
    private String shoppinglistKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        initializeUI();
        getIntentExtras();
        FirebaseUtil.initializeFirebase();

        if (userId != null) {
            FirebaseUtil.openFbReference("shoppinglists/" + userId);
        } else {
            showToastAndFinish(R.string.user_not_auth);
            return;
        }

        setupSpinner();
        setupEditMode();

        btnSaveShoppingList.setOnClickListener(v -> {
            saveData();
            clean();
            returnToPreviousActivity();
        });

        btnCancel.setOnClickListener(v -> {
            clean();
            returnToPreviousActivity();
        });

        adjustLayoutForSystemInsets();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        inputShoppingListName = findViewById(R.id.storageroom_name);
        iconSpinner = findViewById(R.id.iconSpinner);
        btnSaveShoppingList = findViewById(R.id.btnSaveShoppingList);
        btnCancel = findViewById(R.id.btnCancel);
    }

    /**
     * Retrieves intent extras passed to this activity.
     */
    private void getIntentExtras() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        editMode = intent.getBooleanExtra("editMode", false);
        shoppinglistKey = intent.getStringExtra("shoppinglistKey");
        previousActivity = intent.getStringExtra("previousActivity");
    }

    /**
     * Sets up the spinner with icon resources.
     */
    private void setupSpinner() {
        IconAdapter iconAdapter = new IconAdapter(this, iconResIds);
        iconSpinner.setAdapter(iconAdapter);
        selectedIconResId = iconResIds[0];

        iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIconResId = iconResIds[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedIconResId = iconResIds[0];
            }
        });
    }

    /**
     * Sets up the edit mode, pre-filling fields if necessary.
     */
    private void setupEditMode() {
        if (editMode) {
            Intent intent = getIntent();
            String shoppinglistName = intent.getStringExtra("shoppinglistName");
            int shoppinglistIcon = intent.getIntExtra("shoppinglistIcon", iconResIds[0]);

            inputShoppingListName.setText(shoppinglistName);
            int spinnerPosition = getIconPosition(shoppinglistIcon);
            iconSpinner.setSelection(spinnerPosition);
        }
    }

    /**
     * Adjusts the layout for system insets.
     */
    private void adjustLayoutForSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Returns the position of the given icon resource ID in the iconResIds array.
     *
     * @param iconResId the resource ID of the icon
     * @return the position of the icon in the array
     */
    private int getIconPosition(int iconResId) {
        for (int i = 0; i < iconResIds.length; i++) {
            if (iconResIds[i] == iconResId) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Clears the input fields and resets the spinner selection.
     */
    private void clean() {
        inputShoppingListName.setText("");
        iconSpinner.setSelection(0);
    }

    /**
     * Saves the shopping list data to Firebase.
     */
    private void saveData() {
        if (userId != null) {
            String name = inputShoppingListName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.shoppinglist_name_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedIconStr = String.valueOf(selectedIconResId);

            ShoppingList shoppingList;
            if (shoppinglistKey != null) {
                shoppingList = new ShoppingList(shoppinglistKey, name, userId, System.currentTimeMillis(), 0, null, null);
                updateData(shoppingList);
            } else {
                DatabaseReference reference = FirebaseUtil.mDatabaseReference.push();
                //String key = reference.getKey();
                shoppingList = new ShoppingList(null, name, userId, System.currentTimeMillis(), 0, null, null);
                saveNewData(shoppingList);
            }
        } else {
            showToastAndFinish(R.string.user_not_auth);
        }
    }

    /**
     * Saves new shopping list data to Firebase.
     *
     * @param shoppingList the shopping list to save
     */
    private void saveNewData(ShoppingList shoppingList) {
        FirebaseUtil.saveData("shoppinglists/" + userId, shoppingList, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                showToast(isSuccess ? R.string.data_save_success : R.string.data_save_fail);
                if (isSuccess) finish();
            }

            @Override
            public void onCallback(List<Object> list) {
                // Not used in this context
            }
        });
    }

    /**
     * Updates existing shopping list data in Firebase.
     *
     * @param shoppingList the shopping list to update
     */
    private void updateData(ShoppingList shoppingList) {
        FirebaseUtil.updateData("shoppinglists/" + userId, shoppingList, new FirebaseUtil.FirebaseCallback() {
            @Override
            public void onCallback(boolean isSuccess) {
                if (isSuccess) {
                    Toast.makeText(ShoppingListActivity.this, "shoppinglist updated successfully", Toast.LENGTH_SHORT).show();
                    returnToPreviousActivity();
                } else {
                    Toast.makeText(ShoppingListActivity.this, "Failed to update shoppinglist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCallback(List<Object> list) {
                // Not used in this context
            }
        });
    }

    /**
     * Shows a toast message and finishes the activity.
     *
     * @param messageId the resource ID of the message to display
     */
    private void showToastAndFinish(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Shows a toast message.
     *
     * @param messageId the resource ID of the message to display
     */
    private void showToast(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns to the previous activity.
     */
    private void returnToPreviousActivity() {
        if (previousActivity != null) {
            try {
                Class<?> previousActivityClass = Class.forName(previousActivity);
                Intent intent = new Intent(this, previousActivityClass);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to return to previous activity", Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
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
        Intent intent = new Intent(ShoppingListActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
