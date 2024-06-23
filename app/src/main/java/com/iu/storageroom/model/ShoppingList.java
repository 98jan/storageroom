package com.iu.storageroom.model;

import com.iu.storageroom.R;

import java.util.List;

public class ShoppingList {

    private String key;
    private String name;
    private String storageroomKey;

    private int selectedIconInt;
    private String selectedIcon;
    private long purchaseDate;
    private int state;
    private ShoppingListProduct shoppingListProduct;
    private List<Product> products;

    public ShoppingList() {
    }

    public ShoppingList(String key, String name, String storageroomKey, long purchaseDate, int state, ShoppingListProduct shoppingListProduct, List<Product> products) {
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

    public String getStorageroomKey() {
        return storageroomKey;
    }

    public void setStorageroomKey(String storageroomKey) {
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

    /**
     * Placeholder method to get the selected icon integer value.
     * Replace with actual implementation.
     */
    public int getSelectedIconInt() {
        return selectedIconInt;
    }

    /**
     * Sets the selected icon as an integer and updates the string representation of the selected icon.
     *
     * @param selectedIconInt the selected icon integer to set
     */
    public void setSelectedIconInt(int selectedIconInt) {
        this.selectedIconInt = selectedIconInt;
        this.selectedIcon = String.valueOf(selectedIconInt);
    }

    /**
     * Gets the selected icon as a string.
     *
     * @return the selected icon as a string
     */
    public String getSelectedIcon() {
        return selectedIcon;
    }

    /**
     * Sets the selected icon as a string.
     *
     * @param selectedIcon the selected icon string to set
     */
    public void setSelectedIcon(String selectedIcon) {
        this.selectedIcon = selectedIcon;
        try {
            this.selectedIconInt = Integer.parseInt(selectedIcon);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Fehlerbehandlung hier, falls die Umwandlung fehlschlägt
        }
    }
}
