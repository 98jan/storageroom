package com.iu.storageroom.model;

public class ShoppingListProduct {

    private String key;              // Firebase key for identifying the product
    private String shoppingListKey;  // Key of the shopping list to which this product belongs
    private String productName;      // Name of the product
    private int quantity;            // Quantity of the product in the shopping list

    public ShoppingListProduct() {
        // Default constructor required for Firebase serialization
    }

    public ShoppingListProduct(String shoppingListKey, String productName, int quantity) {
        this.shoppingListKey = shoppingListKey;
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getShoppingListKey() {
        return shoppingListKey;
    }

    public void setShoppingListKey(String shoppingListKey) {
        this.shoppingListKey = shoppingListKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
