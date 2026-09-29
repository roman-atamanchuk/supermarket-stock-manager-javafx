package org.example.store;

import org.example.data.GoodItem;
import org.example.data.Temperature;

public class GoodItemController {

    private final GoodItem goodItem;

    public GoodItemController(GoodItem goodItem) {
        if (goodItem == null) {
            throw new IllegalArgumentException("GoodItem cannot be null");
        }
        this.goodItem = goodItem;
    }

    // --- Update Name ---
    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            System.out.println("⚠️ Name cannot be empty.");
            return;
        }
        String oldName = goodItem.getName();
        goodItem.setName(newName.trim());
        System.out.println("📝 Name updated: '" + oldName + "' → '" + newName + "'");
    }

    // --- Update Description ---
    public void updateDescription(String newDesc) {
        if (newDesc == null || newDesc.isBlank()) {
            System.out.println("⚠️ Description cannot be empty.");
            return;
        }
        String oldDesc = goodItem.getDescription();
        goodItem.setDescription(newDesc.trim());
        System.out.println("🧾 Description updated: '" + oldDesc + "' → '" + newDesc + "'");
    }

    // --- Update Quantity ---
    public void updateQuantity(int newQuantity) {
        if (newQuantity < 0) {
            System.out.println("⚠️ Quantity cannot be negative.");
            return;
        }
        int oldQuantity = goodItem.getQuantity();
        goodItem.setQuantity(newQuantity);
        System.out.printf("✅ Quantity updated: %d → %d%n", oldQuantity, newQuantity);
    }

    // --- Update Price ---
    public void updatePrice(double newPrice) {
        if (newPrice < 0) {
            System.out.println("⚠️ Price cannot be negative.");
            return;
        }
        double oldPrice = goodItem.getUnitPrice();
        goodItem.setUnitPrice(newPrice);
        System.out.printf("💰 Price updated: €%.2f → €%.2f%n", oldPrice, newPrice);
    }

    // --- Update Weight ---
    public void updateWeight(double newWeight) {
        if (newWeight < 0) {
            System.out.println("⚠️ Weight cannot be negative.");
            return;
        }
        double oldWeight = goodItem.getSizeWeight();
        goodItem.setSizeWeight(newWeight);
        System.out.printf("⚖️ Weight updated: %.2fg → %.2fg%n", oldWeight, newWeight);
    }

    // --- Update Photo URL ---
    public void updatePhoto(String newPhotoUrl) {
        if (newPhotoUrl == null || newPhotoUrl.isBlank()) {
            System.out.println("⚠️ Photo URL cannot be empty.");
            return;
        }
        String oldPhoto = goodItem.getPhotoUrl();
        goodItem.setPhotoUrl(newPhotoUrl.trim());
        System.out.println("🖼️ Photo updated from '" + oldPhoto + "' to '" + newPhotoUrl + "'");
    }

    // --- Update Temperature ---
    public void updateTemperature(Temperature newTemp) {
        if (newTemp == null) {
            System.out.println("⚠️ Temperature cannot be null.");
            return;
        }
        Temperature oldTemp = goodItem.getTemperature();
        goodItem.setTemperature(newTemp);
        System.out.println("🌡️ Temperature changed: " + oldTemp + " → " + newTemp);
    }

    // --- Merge with another item ---
    public void mergeWith(GoodItem other) {
        if (other == null) {
            System.out.println("⚠️ Cannot merge with null item.");
            return;
        }
        goodItem.merge(other);
    }

    // --- Print item info ---
    public void printDetails() {
        System.out.println("📦 Item Details: " + goodItem);
    }

    // --- Getter ---
    public GoodItem getGoodItem() {
        return goodItem;
    }
}