package org.example.store;

import org.example.data.*;
import org.example.util.DataSync;
import org.example.util.Node;
import org.example.util.SinglyLinkedList;

public class SupermarketController {

    private Supermarket supermarket;

    // --- Existing constructor ---
    public SupermarketController(String name) {
        this.supermarket = new Supermarket(name);
    }

    // --- ✅ New constructor for loaded supermarkets ---
    public SupermarketController(Supermarket loadedMarket) {
        this.supermarket = loadedMarket;
    }

    // --- Add new floor area ---
    public boolean addFloorArea(String title, String level) {
        if (title == null || title.trim().isEmpty()) {
            return false; // invalid title
        }

        // Normalize title for case-insensitive comparison
        String normalizedTitle = title.trim().toLowerCase();

        // ✅ Check for existing floor area with the same title
        Node<FloorArea> current = supermarket.getFloorAreas().getHead();
        while (current != null) {
            if (current.data.getTitle().trim().equalsIgnoreCase(normalizedTitle)) {
                return false; // duplicate found
            }
            current = current.next;
        }

        // ✅ Create and link new floor
        FloorArea newArea = new FloorArea(title, level);
        newArea.setParentSupermarket(supermarket);
        supermarket.getFloorAreas().add(newArea);

        return true; // successfully added
    }

    // --- Remove floor area by title ---
    public void removeFloorArea(String title) {
        FloorArea temp = new FloorArea(title, "");
        boolean removed = supermarket.getFloorAreas().remove(temp);
        if (removed) {
            System.out.println("🗑️ Removed floor area: " + title);
        } else {
            System.out.println("⚠️ Floor area not found: " + title);
        }
    }

    // --- Exact search by title ---
    public void searchFloorArea(String title) {
        FloorArea temp = new FloorArea(title, "");
        FloorArea found = supermarket.getFloorAreas().find(temp);
        if (found != null) {
            System.out.println("🔍 Found floor area: " + found);
        } else {
            System.out.println("❌ Floor area not found: " + title);
        }
    }

    // --- Progressive search (character by character) ---
    public SinglyLinkedList<FloorArea> searchFloorAreasProgressive(String term) {

        SinglyLinkedList<FloorArea> result = new SinglyLinkedList<>();
        if (term == null || term.isBlank()) return result;

        term = term.toLowerCase();

        var node = supermarket.getFloorAreas().getHead();
        while (node != null) {
            FloorArea floor = node.getData();
            String title = floor.getTitle().toLowerCase();

            if (title.length() >= term.length() &&
                    title.substring(0, term.length()).equals(term)) {

                result.add(floor);
            }

            node = node.getNext();
        }

        return result;
    }

    // --- Print all floor areas ---
    public void printAllFloorAreas() {
        supermarket.getFloorAreas().printAll();
    }

    // --- Getter ---
    public Supermarket getSupermarket() {
        return supermarket;
    }

    // --- ✅ Optional setter (useful if we later reload data) ---
    public void setSupermarket(Supermarket supermarket) {
        this.supermarket = supermarket;
    }
    // ============================================================
// 🔥 FULL DATABASE RESET — deletes ALL floors, aisles, shelves, goods
// ============================================================
    public void resetDatabase() {
        // Remove everything
        supermarket.getFloorAreas().clear();  // your SinglyLinkedList supports clear()

        // Optionally regenerate an empty supermarket structure (if needed)
        // supermarket.getFloorAreas() = new SinglyLinkedList<>();

        try {
            DataSync.save(supermarket);
            System.out.println("🔥 Supermarket database fully reset.");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to save after reset: " + e.getMessage());
        }
    }
    // ============================================================
    // 🆕 NEW METHOD: Search for goods by name substring (case-insensitive)
    // ============================================================
    public SinglyLinkedList<GoodItem> searchGoodsByName(String term) {
        SinglyLinkedList<GoodItem> result = new SinglyLinkedList<>();

        if (term == null || term.isBlank()) return result;

        String lowerTerm = term.trim().toLowerCase();

        for (Node<FloorArea> floorNode = supermarket.getFloorAreas().getHead();
             floorNode != null; floorNode = floorNode.next) {

            FloorArea floor = floorNode.data;
            if (floor.getAisles() == null) continue;

            for (Node<Aisle> aisleNode = floor.getAisles().getHead();
                 aisleNode != null; aisleNode = aisleNode.next) {

                Aisle aisle = aisleNode.data;
                if (aisle.getShelves() == null) continue;

                for (Node<Shelf> shelfNode = aisle.getShelves().getHead();
                     shelfNode != null; shelfNode = shelfNode.next) {

                    Shelf shelf = shelfNode.data;
                    if (shelf.getGoods() == null) continue;

                    for (Node<GoodItem> goodNode = shelf.getGoods().getHead();
                         goodNode != null; goodNode = goodNode.next) {

                        GoodItem g = goodNode.data;
                        String name = g.getName();
                        if (name != null && name.toLowerCase().contains(lowerTerm)) {
                            result.add(g);
                        }
                    }
                }
            }
        }

        return result;
    }
}