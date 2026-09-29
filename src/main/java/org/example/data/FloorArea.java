package org.example.data;

import java.util.UUID;
import org.example.util.SinglyLinkedList;

public class FloorArea {
    private final String id;
    private String title;              // e.g. "Dairy"
    private String level;              // e.g. "Ground Floor"
    private SinglyLinkedList<Aisle> aisles;
    private Supermarket parentSupermarket;


    public FloorArea(String title, String level) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.level = level;
        this.aisles = new SinglyLinkedList<>();
    }
    public void setParentSupermarket(Supermarket parent) {
        this.parentSupermarket = parent;
    }

    public Supermarket getParentSupermarket() {
        return parentSupermarket;
    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLevel() {
        return level;
    }

    public SinglyLinkedList<Aisle> getAisles() {
        return aisles;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * ✅ Adds an aisle to this floor and automatically links the parent.
     */
    public void addAisle(Aisle aisle) {
        if (aisle == null) return;
        aisle.setParentArea(this);   // link back to this floor
        aisles.add(aisle);
    }
    // === TOTALS ===
    public double getTotalValue() {
        double total = 0;
        var node = aisles.getHead();
        while (node != null) {
            total += node.getData().getTotalValue();
            node = node.getNext();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        var node = aisles.getHead();
        while (node != null) {
            total += node.getData().getTotalQuantity();
            node = node.getNext();
        }
        return total;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FloorArea)) return false;
        FloorArea other = (FloorArea) obj;
        return title.equalsIgnoreCase(other.title);
    }

    @Override
    public int hashCode() {
        return title.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "FloorArea{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", aisles=" + aisles.size() +
                '}';
    }
}