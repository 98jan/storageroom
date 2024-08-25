package com.iu.storageroom.model;

import android.util.Log;

/**
 * Represents a storage room which contains various items and their details.
 */
public class Storageroom {
    private static final String TAG = "Storageroom";

    private String key;
    private String name;
    private int selectedIconInt;
    private String selectedIcon;
    private long expiryDate;
    private long consumptionDate;
    private int refill_flag;
    private int state;

    private Product product;

    /**
     * Default constructor for Storageroom.
     */
    public Storageroom() {}

    /**
     * Constructs a Storageroom with the specified details.
     *
     * @param key             the unique identifier for the storage room
     * @param name            the name of the storage room
     * @param selectedIconInt the integer representing the selected icon
     * @param expiryDate      the expiry date of the storage room items
     * @param consumptionDate the consumption date of the storage room items
     * @param refill_flag     the flag indicating if a refill is needed
     * @param state           the state of the storage room
     * @param product         the product contained in the storage room
     */
    public Storageroom(String key, String name, int selectedIconInt, long expiryDate, long consumptionDate, int refill_flag, int state, Product product) {
        this.key = key;
        this.name = name;
        this.selectedIconInt = selectedIconInt;
        this.selectedIcon = String.valueOf(selectedIconInt);
        this.expiryDate = expiryDate;
        this.consumptionDate = consumptionDate;
        this.refill_flag = refill_flag;
        this.state = state;
        this.product = product;
    }

    /**
     * Gets the key of the storage room.
     *
     * @return the key of the storage room
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the storage room.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the name of the storage room.
     *
     * @return the name of the storage room
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the storage room.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the selected icon as an integer.
     *
     * @return the selected icon as an integer
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
            // selection of icon failed
            Log.e(TAG, "Icon selection failed");
        }
    }

    /**
     * Gets the expiry date of the storage room items.
     *
     * @return the expiry date of the storage room items
     */
    public long getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the expiry date of the storage room items.
     *
     * @param expiryDate the expiry date to set
     */
    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Gets the consumption date of the storage room items.
     *
     * @return the consumption date of the storage room items
     */
    public long getConsumptionDate() {
        return consumptionDate;
    }

    /**
     * Sets the consumption date of the storage room items.
     *
     * @param consumptionDate the consumption date to set
     */
    public void setConsumptionDate(long consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    /**
     * Gets the refill flag of the storage room.
     *
     * @return the refill flag of the storage room
     */
    public int getRefill_flag() {
        return refill_flag;
    }

    /**
     * Sets the refill flag of the storage room.
     *
     * @param refill_flag the refill flag to set
     */
    public void setRefill_flag(int refill_flag) {
        this.refill_flag = refill_flag;
    }

    /**
     * Gets the state of the storage room.
     *
     * @return the state of the storage room
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the state of the storage room.
     *
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Gets the product contained in the storage room.
     *
     * @return the product contained in the storage room
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product contained in the storage room.
     *
     * @param product the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Returns a string representation of the storage room.
     *
     * @return a string representation of the storage room
     */
    @Override
    public String toString() {
        return "Storageroom{" +
                " name='" + name + '\'' +
                ", expiryDate=" + expiryDate +
                ", consumptionDate=" + consumptionDate +
                ", refill_flag=" + refill_flag +
                ", state=" + state +
                ", product=" + product +
                '}';
    }
}