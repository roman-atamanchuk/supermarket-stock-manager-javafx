package org.example.store;

import org.example.data.Aisle;
import org.example.data.Shelf;

public class AisleController {

    private Aisle aisle;

    public AisleController(Aisle aisle) {
        this.aisle = aisle;
    }


    public void addShelf(int number) {
        Shelf newShelf = new Shelf(number);

        // Prevent duplicates
        if (aisle.getShelves().contains(newShelf)) {
            System.out.println("⚠️ Shelf already exists: #" + number);
            return;
        }

        aisle.addShelf(newShelf);
        System.out.println("✅ Added shelf #" + number);
    }
    // --- Remove shelf by number ---
    public void removeShelf(int number) {
        Shelf temp = new Shelf(number);
        boolean removed = aisle.getShelves().remove(temp);

        if (removed) {
            System.out.println("🗑️ Removed shelf #" + number);
        } else {
            System.out.println("⚠️ Shelf not found: #" + number);
        }
    }
    public Shelf findShelf(int number) {
        Shelf temp = new Shelf(number);
        return aisle.getShelves().find(temp);
    }
    // --- Search for shelf by number ---
    public void searchShelf(int number) {
        Shelf temp = new Shelf(number);
        Shelf found = aisle.getShelves().find(temp);

        if (found != null) {
            System.out.println("🔍 Found shelf: " + found);
        } else {
            System.out.println("❌ Shelf not found: #" + number);
        }
    }

    // --- Print all shelves ---
    public void printAllShelves() {
        System.out.println("📋 Shelves in aisle: " + aisle.getName());
        if (aisle.getShelves().isEmpty()) {
            System.out.println("   (no shelves)");
        } else {
            aisle.getShelves().printAll();
        }
    }

    // --- Getter for aisle ---
    public Aisle getAisle() {
        return aisle;
    }
}