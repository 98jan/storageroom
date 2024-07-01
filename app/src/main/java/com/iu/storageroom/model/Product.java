package com.iu.storageroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("product")
public class Product implements Serializable {

    private String key;
    @JsonProperty("product_name")
    private String name;
    private String note;
    @JsonProperty("code")
    private String barcode;
    @JsonProperty("image_front_url")
    private String imageUrl;
    @JsonProperty("brands")
    private String brand;
    @JsonProperty("categories_tags")
    private List<String> categories;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("stores")
    private String store;

    private int rating;
    private boolean favourite;

    public Product(){}

    public Product(String key, String name, String note, String barcode, String imageUrl, String brand, List<String> categories, String quantity, String store, int rating, boolean favourite) {
        this.key = key;
        this.name = name;
        this.note = note;
        this.barcode = barcode;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.categories = categories;
        this.quantity = quantity;
        this.store = store;
        this.rating = rating;
        this.favourite = favourite;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }


    @Override
    public String toString() {
        return "Product{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", barcode='" + barcode + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", brand='" + brand + '\'' +
                ", categories=" + categories +
                ", quantity='" + quantity + '\'' +
                ", store='" + store + '\'' +
                ", rating=" + rating +
                ", favourite=" + favourite +
                '}';
    }
}
