package org.example.data;

import org.example.util.SinglyLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AisleTest {

    @Test
    void testConstructorAndGetters() {
        Aisle a = new Aisle("Bread", Temperature.REFRIGERATED, 5.0, 2.0);

        assertNotNull(a.getId());
        assertEquals("Bread", a.getName());
        assertEquals(Temperature.REFRIGERATED, a.getTemperature());
        assertEquals(5.0, a.getLength());
        assertEquals(2.0, a.getWidth());
        assertNotNull(a.getShelves());
        assertEquals(0, a.getShelves().size());
    }

    @Test
    void testSetParentArea() {
        Aisle a = new Aisle("Dairy", Temperature.REFRIGERATED, 4.0, 1.5);
        FloorArea area = new FloorArea("Food", "L1");

        a.setParentArea(area);

        assertSame(area, a.getParentArea());
    }

    @Test
    void testAddShelf() {
        Aisle aisle = new Aisle("Snacks", Temperature.UNREFRIGERATED, 3.0, 1.0);
        Shelf shelf = new Shelf(1);

        aisle.addShelf(shelf);

        assertEquals(1, aisle.getShelves().size());
        assertSame(aisle, shelf.getParentAisle(), "Shelf must get parent aisle assigned.");
    }

    @Test
    void testTotalsEmpty() {
        Aisle aisle = new Aisle("Frozen", Temperature.FROZEN, 2.0, 1.0);

        assertEquals(0.0, aisle.getTotalValue());
        assertEquals(0, aisle.getTotalQuantity());
    }

    @Test
    void testTotalsWithShelvesAndItems() {
        Aisle aisle = new Aisle("Drinks", Temperature.UNREFRIGERATED, 2.0, 1.0);

        // Create shelves
        Shelf s1 = new Shelf(1);
        Shelf s2 = new Shelf(2);

        aisle.addShelf(s1);
        aisle.addShelf(s2);

        // Add items
        GoodItem cola = new GoodItem(
                "Cola", "Drink", 500, 1.20, 10,
                Temperature.UNREFRIGERATED, "c.jpg"
        ); // total 12.00

        GoodItem juice = new GoodItem(
                "Juice", "Orange", 500, 2.00, 3,
                Temperature.UNREFRIGERATED, "j.jpg"
        ); // total 6.00

        s1.getGoods().add(cola);
        s2.getGoods().add(juice);

        assertEquals(18.00, aisle.getTotalValue());
        assertEquals(13, aisle.getTotalQuantity());
    }

    @Test
    void testEqualsAndHashCode() {
        Aisle a1 = new Aisle("Milk", Temperature.REFRIGERATED, 1.0, 1.0);
        Aisle a2 = new Aisle("milk", Temperature.UNREFRIGERATED, 2.0, 3.0);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testEqualsDifferentNames() {
        Aisle a1 = new Aisle("Fruit", Temperature.UNREFRIGERATED, 1, 1);
        Aisle a2 = new Aisle("Vegetables", Temperature.UNREFRIGERATED, 1, 1);

        assertNotEquals(a1, a2);
    }

    @Test
    void testToStringContainsNameAndShelfCount() {
        Aisle aisle = new Aisle("Cheese", Temperature.REFRIGERATED, 5, 2);
        String str = aisle.toString();

        assertTrue(str.contains("Cheese"));
        assertTrue(str.contains("shelves=0"));
    }
}