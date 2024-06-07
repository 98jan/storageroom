package com.iu.storageroom.model;

public class ShoppingListProduct {

    private ShoppingList shoppingList;
    private Product product;
    private int quantity;

    public ShoppingListProduct() {
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
}
