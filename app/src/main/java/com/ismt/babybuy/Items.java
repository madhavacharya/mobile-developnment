package com.ismt.babybuy;

import java.io.Serializable;

public class Items implements Serializable {

    private int id;
    private String itemsName;
    private byte[] images;
    private int purchased;
    private float price;
    private String description;


    public Items(int id, String itemsName, byte[] images, int purchased, float price, String description) {
        this.id = id;
        this.itemsName = itemsName;
        this.images = images;
        this.purchased = purchased;
        this.price = price;
        this.description = description;
    }

    public Items() {

    }

    public byte[] getImages() {
        return images;
    }

    public void setImages(byte[] images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemsName() {
        return itemsName;
    }

    public void setItemsName(String itemsName) {
        this.itemsName = itemsName;
    }

    public int getPurchased() {
        return purchased;
    }

    public void setPurchased(int purchased) {
        this.purchased = purchased;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
