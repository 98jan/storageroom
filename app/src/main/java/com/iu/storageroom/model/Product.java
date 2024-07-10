package com.iu.storageroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a product model with various attributes.
 */
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
    private String quantity = "1";
    @JsonProperty("stores")
    private String store;

    private int rating;
    private boolean favourite;

    /**
     * Default constructor for Firebase serialization.
     */
    public Product(){}

    /**
     * Constructor to initialize all attributes of the product.
     *
     * @param key       The unique identifier of the product.
     * @param name      The name of the product.
     * @param note      Additional notes or description about the product.
     * @param barcode   The barcode of the product.
     * @param imageUrl  The URL of the product image.
     * @param brand     The brand or manufacturer of the product.
     * @param categories The categories or tags associated with the product.
     * @param quantity  The quantity or size of the product.
     * @param store     The store or location where the product is available.
     * @param rating    The rating of the product.
     * @param favourite Whether the product is marked as a favorite.
     */
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

    /**
     * Retrieves the unique key of the product.
     *
     * @return The key of the product.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the unique key of the product.
     *
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Retrieves the name of the product.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the additional notes or description about the product.
     *
     * @return The note about the product.
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets additional notes or description about the product.
     *
     * @param note The note to set.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Retrieves the barcode of the product.
     *
     * @return The barcode of the product.
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets the barcode of the product.
     *
     * @param barcode The barcode to set.
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Retrieves the URL of the product image.
     *
     * @return The URL of the product image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the product image.
     *
     * @param imageUrl The URL of the image to set.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Retrieves the rating of the product.
     *
     * @return The rating of the product.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating of the product.
     *
     * @param rating The rating to set.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Checks whether the product is marked as favorite.
     *
     * @return True if the product is favorite, false otherwise.
     */
    public boolean isFavourite() {
        return favourite;
    }

    /**
     * Sets whether the product is marked as favorite.
     *
     * @param favourite True to mark the product as favorite, false otherwise.
     */
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    /**
     * Retrieves the brand or manufacturer of the product.
     *
     * @return The brand of the product.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the brand or manufacturer of the product.
     *
     * @param brand The brand to set.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Retrieves the categories or tags associated with the product.
     *
     * @return The list of categories/tags of the product.
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Sets the categories or tags associated with the product.
     *
     * @param categories The list of categories/tags to set.
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * Retrieves the quantity or size of the product.
     *
     * @return The quantity or size of the product.
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity or size of the product.
     *
     * @param quantity The quantity or size to set.
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Retrieves the store or location where the product is available.
     *
     * @return The store or location of the product.
     */
    public String getStore() {
        return store;
    }

    /**
     * Sets the store or location where the product is available.
     *
     * @param store The store or location to set.
     */
    public void setStore(String store) {
        this.store = store;
    }

    /**
     * Returns a string representation of the Product object.
     *
     * @return A string representation of the Product.
     */
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
