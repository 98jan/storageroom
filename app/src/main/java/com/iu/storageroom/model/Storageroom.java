package com.iu.storageroom.model;

public class Storageroom {

    private String key;

    private String name;

    private String image;

    private long expiryDate;
    private long consumptionDate;

    private int refill_flag;

    private int state;

    private Product product;

    public Storageroom() {}

    public Storageroom(String key, String name, long expiryDate, long consumptionDate, int refill_flag, int state, Product product) {
        this.key = key;
        this.name = name;
        this.expiryDate = expiryDate;
        this.consumptionDate = consumptionDate;
        this.refill_flag = refill_flag;
        this.state = state;
        this.product = product;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(long consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public int getRefill_flag() {
        return refill_flag;
    }

    public void setRefill_flag(int refill_flag) {
        this.refill_flag = refill_flag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

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
