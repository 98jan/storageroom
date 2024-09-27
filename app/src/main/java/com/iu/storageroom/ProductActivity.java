package com.iu.storageroom;

import static com.iu.storageroom.utils.FirebaseUtil.updateData;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.model.ProductWrapper;
import com.iu.storageroom.utils.FirebaseUtil;
import com.iu.storageroom.utils.RequestHandler;

import android.Manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity for displaying and editing product information.
 */
public class ProductActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN_BARCODE = 1;

    private Product product;
    private TextView editTextProductLabel;
    private EditText editTextProductName;
    private Spinner productGroupSpinner;
    private EditText editTextProductNote;
    private EditText editTextProductBarcode;
    private ImageView imageViewProduct;
    private EditText editTextProductBrand;
    private EditText editTextProductQuantity;
    private EditText editTextProductStore;
    private EditText editTextProductRating;
    private CheckBox checkBoxProductFavourite;
    private ImageButton btnReadBarcode;
    private ImageButton btnSearchBarcode;
    private Button backButton;
    private Button btnSave;

    private String userId;
    private String storageroomKey;
    private String storageroomName;

    private String productKey;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        FirebaseUtil.initializeFirebase();
        initUI();


        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            FirebaseUtil.openFbReference("products/" + userId);
        } else {
            showToast(R.string.user_not_auth);
            finish();
            return;
        }

        storageroomKey = getIntent().getStringExtra("storageroomKey");
        storageroomName = getIntent().getStringExtra("storageroomName");

        product = (Product) getIntent().getSerializableExtra("product");

        initializeProductGroups();
        setupListeners();

        if (product != null) {
            populateFields(product);
        }
    }

    /**
     * Initializes UI elements from the layout.
     */
    private void initUI() {
        editTextProductLabel = findViewById(R.id.headerTextView);
        editTextProductName = findViewById(R.id.product_name);
        productGroupSpinner = findViewById(R.id.productGroupSpinner);
        editTextProductNote = findViewById(R.id.product_note);
        editTextProductBarcode = findViewById(R.id.product_barcode);
        imageViewProduct = findViewById(R.id.product_image_url);
        editTextProductBrand = findViewById(R.id.product_brand);
        editTextProductQuantity = findViewById(R.id.product_quantity);
        editTextProductStore = findViewById(R.id.product_store);
        editTextProductRating = findViewById(R.id.product_rating);
        checkBoxProductFavourite = findViewById(R.id.product_favourite);
        btnReadBarcode = findViewById(R.id.btnReadBarcode);
        btnSearchBarcode = findViewById(R.id.btnSearchBarcode);
        backButton = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }

    /**
     * Initializes the dropdown list of product groups.
     */
    private void initializeProductGroups() {
        String[] productGroupIds = new String[]{
                getString(R.string.product_group_placeholder),
                getString(R.string.product_group_food),
                getString(R.string.product_group_housekeeping),
                getString(R.string.product_group_cosmetics),
                getString(R.string.product_group_baby_child),
                getString(R.string.product_group_others)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productGroupIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productGroupSpinner.setAdapter(adapter);
    }

    /**
     * Sets up click listeners for buttons.
     */
    private void setupListeners() {
        btnSearchBarcode.setOnClickListener(v -> searchData());
        btnReadBarcode.setOnClickListener(v -> readData());
        backButton.setOnClickListener(v -> returnToProductOverview());
        btnSave.setOnClickListener(v -> {
            saveData();
            cleanFields();
        });
    }

    /**
     * Sets the spinner selection based on the given value.
     *
     * @param spinner The spinner to set the selection for.
     * @param value   The value to set as selected.
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    /**
     * Populates UI fields with data from a Product object.
     *
     * @param product The Product object containing data to populate.
     */
    private void populateFields(Product product) {
        if (product == null) {
            Toast.makeText(ProductActivity.this, getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
        } else {
            editTextProductName.setText(product.getName());
            editTextProductNote.setText(product.getNote());
            editTextProductBarcode.setText(product.getBarcode());
            editTextProductBrand.setText(product.getBrand());
            editTextProductQuantity.setText(product.getQuantity());
            editTextProductStore.setText(product.getStore());
            loadImageWithGlide(product.getImageUrl());
            editTextProductRating.setText(String.valueOf(product.getRating()));
            checkBoxProductFavourite.setChecked(product.isFavourite());

            if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                setSpinnerSelection(productGroupSpinner, product.getCategories().get(0));
            }
        }
    }

    /**
     * Clears all input fields.
     */
    private void cleanFields() {
        editTextProductName.setText("");
        productGroupSpinner.setSelection(0);
        editTextProductNote.setText("");
        editTextProductBarcode.setText("");
        editTextProductBrand.setText("");
        editTextProductQuantity.setText("");
        editTextProductStore.setText("");
        editTextProductRating.setText("");
        checkBoxProductFavourite.setChecked(false);
    }

    private void loadImageWithGlide(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_image_search_vd_black_24)
                    .error(R.drawable.ic_image_search_vd_black_24)
                    .into(imageViewProduct);
        } else {
            imageViewProduct.setImageResource(R.drawable.ic_image_search_vd_black_24);
        }
    }

    private void searchData() {
        if (userId != null) {
            String productBarcode = editTextProductBarcode.getText().toString().trim();
            if (TextUtils.isEmpty(productBarcode)) {
                editTextProductBarcode.setError("Please enter a product barcode");
                editTextProductBarcode.requestFocus();
                return;
            }

            // Example of handling product data retrieval or scanning
            // This section should be adjusted to fit your specific implementation
            // For simplicity, the below code assumes a requestHandler to fetch product details.
            ProductWrapper productWrapper = RequestHandler.getProduct(productBarcode);

            if (productWrapper != null && productWrapper.getProduct() != null) {
                populateFields(productWrapper.getProduct());
            } else {
                Toast.makeText(this, getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Initiates data reading process, typically for barcode scanning.
     */
    private void readData() {
        if (userId != null) {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        // check if permission for camera was already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // permission was already denied and can only be updated from App-Settings
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                Toast.makeText(this, "Kameraberechtigung wurde bereits abgelehnt, bitte in den App-Einstellungen anpassen", Toast.LENGTH_LONG).show();
            } else {
                // permission wasn't denied, request for needed permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        } else {
            // permission was already granted
            Toast.makeText(this, "Kameraberechtigung bereits erteilt", Toast.LENGTH_SHORT).show();
            // start QR Scanning process
            Intent intent = new Intent(this, LiveBarcodeScanningActivity.class);
            intent.putExtra("userId", getIntent().getStringExtra("userId"));
            startActivityForResult(intent, REQUEST_CODE_SCAN_BARCODE);
        }
    }

    // wait for user input in permission request and handle the response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            // check if permission for camera was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                Toast.makeText(this, "Kameraberechtigung erteilt", Toast.LENGTH_SHORT).show();
                // Hier können Sie Code hinzufügen, der ausgeführt werden soll, wenn die Berechtigung erteilt wurde
                Intent intent = new Intent(this, LiveBarcodeScanningActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN_BARCODE);
            } else {
                // permission denied
                Toast.makeText(this, "Kameraberechtigung abgelehnt", Toast.LENGTH_SHORT).show();
                // Hier können Sie Code hinzufügen, der ausgeführt werden soll, wenn die Berechtigung abgelehnt wurde
            }
        }
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_BARCODE && resultCode == RESULT_OK && data != null) {
            product = data.getParcelableExtra("product");
            if (product != null) {
                populateFields(product);
            }
        }
    }

    /**
     * Saves the entered data to Firebase.
     */
    private void saveData() {
        if (userId != null && storageroomKey != null) {
            // Validate and retrieve product data from UI components
            String productName = editTextProductName.getText().toString().trim();
            if (TextUtils.isEmpty(productName)) {
                editTextProductName.setError(getString(R.string.product_name_required));
                editTextProductName.requestFocus();
                return;
            }

            String productNote = editTextProductNote.getText().toString().trim();
            String productBarcode = editTextProductBarcode.getText().toString().trim();
            String productBrand = editTextProductBrand.getText().toString().trim();
            String productQuantity = editTextProductQuantity.getText().toString().trim();
            String productStore = editTextProductStore.getText().toString().trim();
            boolean productFavourite = checkBoxProductFavourite.isChecked();
            String productRatingText = editTextProductRating.getText().toString();
            int productRating;
            try {
                productRating = Integer.parseInt(productRatingText);
            } catch (NumberFormatException e) {
                productRating = 0;
            }

            List<String> productGroup = new ArrayList<>();
            String selectedProductGroup = (String) productGroupSpinner.getSelectedItem();
            if (!selectedProductGroup.equals(getString(R.string.product_group_placeholder))) {
                productGroup.add(selectedProductGroup);
            }

            // Create or update the product object
            if (product == null || product.getKey() == null) {
                // Creating a new product
                product = new Product(null, productName, productNote, productBarcode, imageUrl,
                        productBrand, productGroup, productQuantity, productStore, productRating, productFavourite);
                saveNewData(product);
            } else {
                // Use the existing productKey
                product = new Product(product.getKey(), productName, productNote, productBarcode, imageUrl,
                        productBrand, productGroup, productQuantity, productStore, productRating, productFavourite);
                updateData(product);
            }
        } else {
            showToast(R.string.user_not_auth);
        }
    }


/**
     * Saves new product data to Firebase.
     *
     * @param product The Product object to save.
     */
    private void saveNewData (Product product){
        if (userId != null) {
            FirebaseUtil.saveData("products/" + userId + "/" + storageroomKey, product, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_save_success), Toast.LENGTH_SHORT).show();
                        finish(); // Return to the overview activity
                    } else {
                        Toast.makeText(ProductActivity.this, getString(R.string.data_save_fail), Toast.LENGTH_SHORT).show();
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


    private void updateData(Product updatedProduct) {
        if (userId != null) {
            String path = "products/" + userId + "/" + storageroomKey;

            FirebaseUtil.updateData(path, updatedProduct, new FirebaseUtil.FirebaseCallback() {
                @Override
                public void onCallback(boolean isSuccess) {
                    if (isSuccess) {
                        Toast.makeText(ProductActivity.this, "Product updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductActivity.this, "Failed to update product.", Toast.LENGTH_SHORT).show();
                    }
                    returnToProductOverview();
                }

                @Override
                public void onCallback(List<Object> list) {

                }
            });
        } else {
            showToast(R.string.user_not_auth);
        }
    }


    /**
     * Displays a toast message with the specified message.
     *
     * @param messageId The resource ID of the string message to display.
     */
    private void showToast ( int messageId){
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns to the product overview activity.
     */
    private void returnToProductOverview () {
        Intent intent = new Intent(ProductActivity.this, ProductOverviewActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("storageroomKey", storageroomKey);
        intent.putExtra("storageroomName", storageroomName);
        startActivity(intent);
        finish();
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
        Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}