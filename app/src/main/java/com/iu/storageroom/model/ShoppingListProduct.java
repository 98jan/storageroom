package com.iu.storageroom.model;

public class ShoppingListProduct {

    private String key;              // Firebase key for identifying the product
    private ShoppingList shoppingList; // Represents the shopping list to which this product belongs
    private Product product;           // Represents the product details
    private int quantity;              // Represents the quantity of the product in the shopping list

    public ShoppingListProduct() {
        // Default constructor required for Firebase serialization
    }

    public ShoppingListProduct(ShoppingList shoppingList, Product product, int quantity) {
        this.shoppingList = shoppingList;
        this.product = product;
        this.quantity = quantity;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
