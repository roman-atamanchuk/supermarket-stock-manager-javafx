package org.example.data;

import java.util.UUID;
import org.example.util.SinglyLinkedList;

public class Supermarket {

    // --- Fields ---
    private final String id;                        // Unique ID for this supermarket
    private String name;                            // e.g. "SuperValu"
    private SinglyLinkedList<FloorArea> floorAreas; // All floor areas in the supermarket

    // --- Constructor ---
    public Supermarket(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.floorAreas = new SinglyLinkedList<>();
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public SinglyLinkedList<FloorArea> getFloorAreas() { return floorAreas; }

    // --- Setter ---
    public void setName(String name) { this.name = name; }


    // === TOTALS ===
    public double getTotalValue() {
        double total = 0;
        var node = floorAreas.getHead();
        while (node != null) {
            total += node.getData().getTotalValue();
            node = node.getNext();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        var node = floorAreas.getHead();
        while (node != null) {
            total += node.getData().getTotalQuantity();
            node = node.getNext();
        }
        return total;
    }

    // --- ToString for debugging ---
    @Override
    public String toString() {
        return "Supermarket{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", floorAreas=" + floorAreas.size() +
                '}';
    }
}