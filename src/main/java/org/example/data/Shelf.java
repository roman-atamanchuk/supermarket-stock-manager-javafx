package org.example.data;

import java.util.UUID;
import org.example.util.SinglyLinkedList;

public class Shelf {
    private final String id;
    private int number;                        // Shelf number
    private SinglyLinkedList<GoodItem> goods;  // Items stored on this shelf
    private Aisle parentAisle;


    public Shelf(int number) {
        this.id = UUID.randomUUID().toString();
        this.number = number;
        this.goods = new SinglyLinkedList<>();
    }

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public SinglyLinkedList<GoodItem> getGoods() {
        return goods;
    }
    public Aisle getParentAisle() {
        return parentAisle;
    }

    public void setParentAisle(Aisle parentAisle) {
        this.parentAisle = parentAisle;
    }
    // === TOTALS ===
    public double getTotalValue() {
        double total = 0;
        var node = goods.getHead();
        while (node != null) {
            total += node.getData().getTotalValue();
            node = node.getNext();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        var node = goods.getHead();
        while (node != null) {
            total += node.getData().getQuantity();
            node = node.getNext();
        }
        return total;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Shelf)) return false;
        Shelf other = (Shelf) obj;
        return this.number == other.number;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }

    @Override
    public String toString() {
        return "Shelf{" +
                "id='" + id + '\'' +
                ", number=" + number +
                ", goods=" + goods.size() +
                '}';
    }
}