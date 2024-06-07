package com.iu.storageroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("product")
public class Product {

    private String key;
    @JsonProperty("product_name")
    private String name;
    private String note;
    @JsonProperty("code")
    private String barcode;
    private String image;
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

    public Product(String key, String name, String note, String barcode, String image, int rating, boolean favourite) {
        this.key = key;
        this.name = name;
        this.note = note;
        this.barcode = barcode;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @Override
    public String toString() {
        return "Product{" +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", barcode='" + barcode + '\'' +
                ", image='" + image + '\'' +
                ", rating=" + rating +
                ", favourite=" + favourite +
                '}';
    }

}
