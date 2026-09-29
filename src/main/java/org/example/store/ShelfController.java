package org.example.store;

import org.example.data.Shelf;
import org.example.data.GoodItem;
import org.example.data.Temperature;
import org.example.util.Node;

public class ShelfController {

    private final Shelf shelf;

    public ShelfController(Shelf shelf) {
        if (shelf == null) {
            throw new IllegalArgumentException("Shelf cannot be null");
        }
        this.shelf = shelf;
    }

    // --- Add or Merge a GoodItem ---
    public void addGoodItem(String name, String description, double sizeWeight, double unitPrice,
                            int quantity, Temperature temperature, String photoUrl) {

        if (name == null || name.isBlank() || description == null || description.isBlank()) {
            System.out.println("⚠️ Name and description are required.");
            return;
        }
        if (quantity <= 0) {
            System.out.println("⚠️ Quantity must be positive.");
            return;
        }
        if (unitPrice < 0) {
            System.out.println("⚠️ Price cannot be negative.");
            return;
        }

        GoodItem newItem = new GoodItem(name.trim(), description.trim(),
                sizeWeight, unitPrice, quantity, temperature, photoUrl);

        Node<GoodItem> node = shelf.getGoods().getHead();
        while (node != null) {
            GoodItem existing = node.getData();
            if (existing.equals(newItem)) {
                existing.merge(newItem);
                System.out.printf("🔄 Merged existing item: %s (%s) → total %d units%n",
                        name, description, existing.getQuantity());
                return;
            }
            node = node.getNext();
        }
        newItem.setParentShelf(shelf);

        shelf.getGoods().add(newItem);
        System.out.printf("✅ Added new item: %s (%s) — %d units, €%.2f each%n",
                name, description, quantity, unitPrice);
        System.out.println("📦 Location info:"+newItem.getParentShelf());
    }

    // --- Remove GoodItem (by name + description + weight) ---
    // --- Remove GoodItem with quantity handling ---
    public void removeGoodItem(String name, String description, double sizeWeight, int qtyToRemove) {
        if (qtyToRemove <= 0) {
            System.out.println("⚠️ Quantity to remove must be positive.");
            return;
        }


        GoodItem temp = new GoodItem(name, description, sizeWeight, 0, 0,
                Temperature.UNREFRIGERATED, "");

        Node<GoodItem> node = shelf.getGoods().getHead();

        while (node != null) {
            GoodItem existing = node.getData();

            if (existing.equals(temp)) {

                // --- If remove all or more → delete completely ---
                if (qtyToRemove >= existing.getQuantity()) {
                    shelf.getGoods().remove(existing);
                    System.out.printf("🗑️ Removed ENTIRE item: %s (%s, %.2f)\n",
                            name, description, sizeWeight);
                    return;
                }

                // --- Otherwise remove part of quantity ---
                existing.setQuantity(existing.getQuantity() - qtyToRemove);
                System.out.printf("➖ Removed %d units from %s — remaining: %d\n",
                        qtyToRemove, name, existing.getQuantity());
                return;
            }
            node = node.getNext();
        }

        System.out.printf("⚠️ Item not found: %s (%s, %.2f g/ml)\n",
                name, description, sizeWeight);
    }
//    public void removeGoodItem(String name, String description, double sizeWeight) {
//        GoodItem temp = new GoodItem(name, description, sizeWeight, 0, 0,
//                Temperature.UNREFRIGERATED, "");
//
//        boolean removed = shelf.getGoods().remove(temp);
//
//        if (removed) {
//            System.out.printf("🗑️ Removed item: %s (%s, %.2f g/ml)%n",
//                    name, description, sizeWeight);
//        } else {
//            System.out.printf("⚠️ Item not found: %s (%s, %.2f g/ml)%n",
//                    name, description, sizeWeight);
//        }
//    }

    // --- Search for a GoodItem ---
    public GoodItem searchGoodItem(String name, String description, double sizeWeight) {
        GoodItem temp = new GoodItem(name, description, sizeWeight, 0, 0,
                Temperature.UNREFRIGERATED, "");

        GoodItem found = shelf.getGoods().find(temp);

        if (found != null) {
            System.out.println("🔍 Found item: " + found);
        } else {
            System.out.printf("❌ Item not found: %s (%s, %.2f g/ml)%n",
                    name, description, sizeWeight);
        }

        return found;
    }

    // --- Print all goods on this shelf ---
    public void printAllGoods() {
        System.out.println("📋 Goods on shelf #" + shelf.getNumber() + ":");
        if (shelf.getGoods().isEmpty()) {
            System.out.println("   (no goods)");
        } else {
            shelf.getGoods().printAll();
        }
    }

    // --- Getter ---
    public Shelf getShelf() {
        return shelf;
    }
}

