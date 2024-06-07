package com.iu.storageroom.model;

import java.util.List;

public class ShoppingList {

    private String key;
    private String name;

    private int storageroomKey;

    private long purchaseDate;

    private int state;

    private ShoppingListProduct shoppingListProduct;

    private List<Product> products;

    public ShoppingList() {
    }

    public ShoppingList(String key, String name, int storageroomKey, long purchaseDate, int state, ShoppingListProduct shoppingListProduct, List<Product> products) {
        this.key = key;
        this.name = name;
        this.storageroomKey = storageroomKey;
        this.purchaseDate = purchaseDate;
        this.state = state;
        this.shoppingListProduct = shoppingListProduct;
        this.products = products;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStorageroomKey() {
        return storageroomKey;
    }

    public void setStorageroomKey(int storageroomKey) {
        this.storageroomKey = storageroomKey;
    }

    public long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ShoppingListProduct getShoppingListProduct() {
        return shoppingListProduct;
    }

    public void setShoppingListProduct(ShoppingListProduct shoppingListProduct) {
        this.shoppingListProduct = shoppingListProduct;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
