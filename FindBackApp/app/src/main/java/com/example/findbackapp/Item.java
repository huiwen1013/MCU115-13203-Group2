package com.example.findbackapp;

public class Item {
    public static final String TYPE_LOST = "Lost";
    public static final String TYPE_FOUND = "Found";

    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_RESOLVED = "Resolved";

    private long itemId;
    private long userId;
    private String itemType;
    private String itemName;
    private String category;
    private String color;
    private String description;
    private String location;
    private String itemDate;
    private String contact;
    private String imageUri;
    private String status;
    private String createdAt;

    public Item() {}

    public Item(long itemId, long userId, String itemType, String itemName, String category,
                String color, String description, String location, String itemDate,
                String contact, String imageUri, String status, String createdAt) {
        this.itemId = itemId;
        this.userId = userId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.category = category;
        this.color = color;
        this.description = description;
        this.location = location;
        this.itemDate = itemDate;
        this.contact = contact;
        this.imageUri = imageUri;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
