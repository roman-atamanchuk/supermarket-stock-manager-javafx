package org.example.data;

import java.util.Objects;
import java.util.UUID;

public class GoodItem {
    private final String id;
    private String name;
    private String description;
    private double sizeWeight;
    private double unitPrice;
    private int quantity;
    private Temperature temperature;
    private String photoUrl;
    private Shelf parentShelf;

    // --- Constructor ---
    public GoodItem(String name, String description, double sizeWeight, double unitPrice,
                    int quantity, Temperature temperature, String photoUrl) {
        this.id = UUID.randomUUID().toString();
        this.name = (name != null && !name.isBlank()) ? name.trim() : "Unnamed Item";
        this.description = (description != null) ? description.trim() : "";
        this.sizeWeight = Math.max(sizeWeight, 0);
        this.unitPrice = Math.max(unitPrice, 0);
        this.quantity = Math.max(quantity, 0);
        this.temperature = (temperature != null) ? temperature : Temperature.UNREFRIGERATED;
        this.photoUrl = (photoUrl != null) ? photoUrl.trim() : "";
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public Shelf getParentShelf() { return parentShelf; }
    public void setParentShelf(Shelf parentShelf) { this.parentShelf = parentShelf; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name.trim();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public double getSizeWeight() { return sizeWeight; }
    public void setSizeWeight(double sizeWeight) {
        if (sizeWeight < 0) throw new IllegalArgumentException("Size/weight cannot be negative");
        this.sizeWeight = sizeWeight;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.unitPrice = unitPrice;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = quantity;
    }

    public Temperature getTemperature() { return temperature; }
    public void setTemperature(Temperature temperature) {
        this.temperature = (temperature != null) ? temperature : Temperature.UNREFRIGERATED;
    }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = (photoUrl != null) ? photoUrl.trim() : "";
    }

    // --- Derived Values ---
    public double getTotalValue() { return unitPrice * quantity; }

    // ✅ Quantity getter used by the shelf/aisle/floor totals
    public int getTotalQuantity() {
        return quantity;
    }

    // --- Merge Logic ---
    public void merge(GoodItem other) {
        if (other == null) return;

        // ✔ Add the quantities together
        this.quantity += other.quantity;

        // ✔ Take the newer price
        if (this.unitPrice != other.unitPrice) {
            this.unitPrice = other.unitPrice;
        }


        if (other.photoUrl != null && !other.photoUrl.isBlank()) {
            this.photoUrl = other.photoUrl;
        }


        this.temperature = other.temperature;



        System.out.println("🔄 Merged item: now " + this.quantity + " units");
    }

    // --- Equality & Hash ---
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GoodItem)) return false;
        GoodItem other = (GoodItem) obj;
        return name.equalsIgnoreCase(other.name)
                && description.equalsIgnoreCase(other.description)
                && Double.compare(sizeWeight, other.sizeWeight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), description.toLowerCase(), sizeWeight);
    }

    // --- Readable Output ---
    @Override
    public String toString() {
        return String.format(
                "GoodItem{id='%s', name='%s', desc='%s', size=%.2fg, price=€%.2f, qty=%d, temp=%s, total=€%.2f}",
                id, name, description, sizeWeight, unitPrice, quantity, temperature, getTotalValue()
        );
    }
}