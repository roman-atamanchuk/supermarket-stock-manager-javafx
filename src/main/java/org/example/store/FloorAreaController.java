package org.example.store;

import org.example.data.FloorArea;
import org.example.data.Aisle;
import org.example.data.Supermarket;
import org.example.data.Temperature;
import org.example.util.Node;

public class FloorAreaController {

    private final FloorArea floorArea;

    public FloorAreaController(FloorArea floorArea) {
        this.floorArea = floorArea;
    }

    // --- Add new aisle ---
    public boolean addAisle(String name, Temperature temperature, double length, double width) {
        if (name == null || name.trim().isEmpty()) {
            return false; // invalid name
        }

        String normalizedName = name.trim().toLowerCase();

        // ✅ Check all aisles in all floor areas of this supermarket
        Supermarket supermarket = floorArea.getParentSupermarket();
        if (supermarket != null && supermarket.getFloorAreas() != null) {
            Node<FloorArea> floorNode = supermarket.getFloorAreas().getHead();
            while (floorNode != null) {
                FloorArea area = floorNode.data;
                if (area != null && area.getAisles() != null) {
                    Node<Aisle> aisleNode = area.getAisles().getHead();
                    while (aisleNode != null) {
                        Aisle existing = aisleNode.data;
                        if (existing != null && existing.getName().trim().equalsIgnoreCase(normalizedName)) {
                            return false; // ❌ name already exists anywhere
                        }
                        aisleNode = aisleNode.next;
                    }
                }
                floorNode = floorNode.next;
            }
        }

        // ✅ Passed uniqueness check — create and link
        Aisle newAisle = new Aisle(name, temperature, length, width);
        newAisle.setParentArea(floorArea);
        floorArea.addAisle(newAisle);

        return true; // ✅ success
    }

    // --- Remove aisle by name ---
    public void removeAisle(String name) {
        if (name == null || name.isBlank()) {
            System.out.println("⚠️ Invalid aisle name.");
            return;
        }

        Aisle temp = new Aisle(name, Temperature.UNREFRIGERATED, 0, 0);
        boolean removed = floorArea.getAisles().remove(temp);

        if (removed) {
            System.out.println("🗑️ Removed aisle: " + name);
        } else {
            System.out.println("⚠️ Aisle not found: " + name);
        }
    }

    // --- Search for an aisle ---
    public Aisle searchAisle(String name) {
        if (name == null || name.isBlank()) {
            System.out.println("⚠️ Invalid search name.");
            return null;
        }

        Aisle temp = new Aisle(name, Temperature.UNREFRIGERATED, 0, 0);
        Aisle found = floorArea.getAisles().find(temp);

        if (found != null) {
            System.out.println("🔍 Found aisle: " + found);
        } else {
            System.out.println("❌ Aisle not found: " + name);
        }

        return found;
    }

    // --- Print all aisles ---
    public void printAllAisles() {
        System.out.println("📋 Aisles in " + floorArea.getTitle() + ":");
        if (floorArea.getAisles().isEmpty()) {
            System.out.println("   (no aisles)");
        } else {
            floorArea.getAisles().printAll();
        }
    }

    // --- Getter ---
    public FloorArea getFloorArea() {
        return floorArea;
    }
}