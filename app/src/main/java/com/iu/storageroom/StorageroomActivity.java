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
import com.iu.storageroom.model.Storageroom;
import com.iu.storageroom.utils.FirebaseUtil;

import java.util.List;

/**
 * Activity class for managing storage rooms.
 */
public class StorageroomActivity extends AppCompatActivity {

    private EditText inputStorageroomName;
    private Spinner iconSpinner;
    private Button btnSaveStorageroom;
    private Button btnCancel;

    // Icons for spinner
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
    private String storageroomKey;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        inputStorageroomName = findViewById(R.id.storageroom_name);
        iconSpinner = findViewById(R.id.iconSpinner);
        btnSaveStorageroom = findViewById(R.id.btnSaveStorageroom);
        btnCancel = findViewById(R.id.btnCancel);

        // Get intent extras
        userId = getIntent().getStringExtra("userId");
        editMode = getIntent().getBooleanExtra("editMode", false);
        storageroomKey = getIntent().getStringExtra("storageroomKey");
        previousActivity = getIntent().getStringExtra("previousActivity");

        // Initialize Firebase
        FirebaseUtil.initializeFirebase();

        // Check if userId is provided
        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            FirebaseUtil.openFbReference("storagerooms/" + userId);
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up spinner with icons
        IconAdapter iconAdapter = new IconAdapter(this, iconResIds);
        iconSpinner.setAdapter(iconAdapter);

        // If edit mode, pre-fill the fields with existing data
        if (editMode) {
            String storageroomName = getIntent().getStringExtra("storageroomName");
            int storageroomIcon = getIntent().getIntExtra("storageroomIcon", iconResIds[0]);

            inputStorageroomName.setText(storageroomName);
            int spinnerPosition = getIconPosition(storageroomIcon);
            iconSpinner.setSelection(spinnerPosition);
        }

        // Set default icon selection
        selectedIconResId = iconResIds[0];

        // Listener for spinner item selection
        iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIconResId = iconResIds[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedIconResId = iconResIds[0]; // Default selection
            }
        });

        // Set click listener for save button
        btnSaveStorageroom.setOnClickListener(v -> {
            saveData();
            clean();
            returnToPreviousActivity();
        });

        // Set click listener for cancel button
        btnCancel.setOnClickListener(v -> {
            clean();
            returnToPreviousActivity();
        });

        // Adjust layout for system insets
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
        inputStorageroomName.setText("");
        iconSpinner.setSelection(0); // Reset spinner selection to first item
    }

    /**
     * Saves the storage room data to Firebase.
     */
    private void saveData() {
        if (userId != null) {
            DatabaseReference reference = FirebaseUtil.mDatabaseReference.push();
            String key = reference.getKey();

            String name = inputStorageroomName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.storageroom_name_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected icon resource ID
            int selectedIconResId = iconResIds[iconSpinner.getSelectedItemPosition()];

            // Convert selectedIconResId to String
            String selectedIconStr = String.valueOf(selectedIconResId);

            // Use the first icon from the array if no icon was selected
            if (selectedIconResId == 0 && iconResIds.length > 0) {
                // Convert to String
                selectedIconStr = String.valueOf(iconResIds[0]);
            }

            // Set timestamps for creation and expiration (2 weeks later)
            long currentTimeMillis = System.currentTimeMillis();
            long timeInTwoWeeks = currentTimeMillis + 14 * 24 * 60 * 60 * 1000;

            Storageroom storageroom;
            String storageroomKey = getIntent().getStringExtra("storageroomKey");

            // If storageroomKey exists, update the existing storageroom
            if (storageroomKey != null) {
                storageroom = new Storageroom(storageroomKey, name, selectedIconResId, currentTimeMillis, timeInTwoWeeks, 1, 2, null);
                updateData(storageroom);
            } else {
                storageroom = new Storageroom(null, name, selectedIconResId, currentTimeMillis, timeInTwoWeeks, 1, 2, null);
                saveNewData(storageroom);
            }
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves new storage room data to Firebase.
     *
     * @param storageroom the storage room to save
     */
    private void saveNewData(Storageroom storageroom) {
        if (userId != null) {
            FirebaseUtil.saveData("storagerooms/" + userId, storageroom, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(StorageroomActivity.this, getString(R.string.data_save_success), Toast.LENGTH_SHORT).show();
                        finish(); // Return to the overview activity
                    } else {
                        Toast.makeText(StorageroomActivity.this, getString(R.string.data_save_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCallback(List<Object> list) {
                    // Not used in this context
                }
            });
        }else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reads storage room data from Firebase.
     */
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
                        //textView.setText(dataBuilder.toString());
                    } else {
                        Toast.makeText(StorageroomActivity.this, getString(R.string.data_read_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_auth), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates existing storage room data in Firebase.
     *
     * @param storageroom the storage room to update
     */
    private void updateData(Storageroom storageroom) {
        if (userId != null) {
            FirebaseUtil.updateData("storagerooms/" + userId, storageroom, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(StorageroomActivity.this, "Storageroom updated successfully", Toast.LENGTH_SHORT).show();
                        returnToPreviousActivity();
                    } else {
                        Toast.makeText(StorageroomActivity.this, "Failed to update storageroom", Toast.LENGTH_SHORT).show();
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

    /**
     * Deletes storage room data from Firebase.
     *
     * @param storageroom the storage room to delete
     */
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
            // If previousActivity is null, just finish this activity
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
        Intent intent = new Intent(StorageroomActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}