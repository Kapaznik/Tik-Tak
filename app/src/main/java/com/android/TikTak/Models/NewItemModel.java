package com.android.TikTak.Models;

import java.util.ArrayList;
public class NewItemModel {
    String itemName;
    double itemPrice;
    String itemColor;
    String itemType;
    String itemSize;
    String itemCondition;
    String itemLocation;
    String itemCreation;
    String itemKey;
    ArrayList<String> itemImageURL;

    public NewItemModel() {

    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public NewItemModel(String itemName, double itemPrice, String itemColor, String itemType,
                        String itemSize, String itemCondition, String itemLocation, String itemCreation, ArrayList<String> itemImageURL) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemColor = itemColor;
        this.itemType = itemType;
        this.itemSize = itemSize;
        this.itemCondition = itemCondition;
        this.itemLocation = itemLocation;
        this.itemImageURL = itemImageURL;
        this.itemCreation = itemCreation;
    }

    public void setItemCreation(String itemCreation) {
        this.itemCreation = itemCreation;
    }

    public String getItemCreation() {
        return itemCreation;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public void setItemImageURL(ArrayList<String> itemImageURL) {
        this.itemImageURL = itemImageURL;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public String getItemColor() {
        return itemColor;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemSize() {
        return itemSize;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public ArrayList<String> getItemImageURL() {
        return itemImageURL;
    }
}
