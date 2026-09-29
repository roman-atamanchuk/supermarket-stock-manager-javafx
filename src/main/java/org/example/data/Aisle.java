package org.example.data;

import java.util.UUID;
import org.example.util.SinglyLinkedList;

public class Aisle {
    private final String id;
    private String name;              // e.g. "Cheese", "Bread"
    private Temperature temperature;     // e.g. "Refrigerated"
    private double length;
    private double width;
    private FloorArea parentArea;
    private SinglyLinkedList<Shelf> shelves;

    public Aisle(String name, Temperature temperature, double length, double width) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.temperature = temperature;
        this.length = length;
        this.width = width;
        this.shelves = new SinglyLinkedList<>();
    }
    public void addShelf(Shelf shelf) {
        if (shelf == null) return;
        shelf.setParentAisle(this);
        shelves.add(shelf);
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Temperature getTemperature() {
        return temperature;
    }
    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }
    public FloorArea getParentArea() {
        return parentArea;
    }
    public void setParentArea(FloorArea parentArea) {
        this.parentArea = parentArea;
    }
    public SinglyLinkedList<Shelf> getShelves() {
        return shelves;
    }
    // === TOTALS ===
    public double getTotalValue() {
        double total = 0;
        var node = shelves.getHead();
        while (node != null) {
            total += node.getData().getTotalValue();
            node = node.getNext();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        var node = shelves.getHead();
        while (node != null) {
            total += node.getData().getTotalQuantity();
            node = node.getNext();
        }
        return total;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Aisle)) return false;
        Aisle other = (Aisle) obj;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "Aisle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", temp='" + temperature + '\'' +
                ", size=" + length + "x" + width +
                ", shelves=" + shelves.size() +
                '}';
    }
}