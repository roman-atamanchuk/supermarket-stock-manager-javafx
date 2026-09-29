package org.example.data;

import org.example.util.SinglyLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SupermarketTest {

    @Test
    void testConstructorAndGetters() {
        Supermarket s = new Supermarket("SuperValu");

        assertNotNull(s.getId());
        assertEquals("SuperValu", s.getName());
        assertNotNull(s.getFloorAreas());
        assertTrue(s.getFloorAreas().isEmpty());
    }

    @Test
    void testSetName() {
        Supermarket s = new Supermarket("OldName");
        s.setName("NewName");

        assertEquals("NewName", s.getName());
    }

    @Test
    void testTotalValueAndQuantity_Empty() {
        Supermarket s = new Supermarket("MyShop");

        assertEquals(0.0, s.getTotalValue());
        assertEquals(0, s.getTotalQuantity());
    }

    @Test
    void testTotalValueAndQuantity() {
        Supermarket s = new Supermarket("MyShop");

        // --- Create FloorArea ---
        FloorArea fa = new FloorArea("Ground", "L0");
        s.getFloorAreas().add(fa);

        // --- Create Aisle ---
        Aisle aisle = new Aisle("A1", Temperature.UNREFRIGERATED, 1.0, 1.0);
        fa.getAisles().add(aisle);

        // --- Create Shelf ---
        Shelf shelf = new Shelf(1);
        aisle.getShelves().add(shelf);

        // --- Add items ---
        GoodItem apple = new GoodItem(
                "Apple", "Fresh", 100, 1.00, 10,
                Temperature.UNREFRIGERATED, "a.jpg"
        ); // total = 10 €

        GoodItem milk = new GoodItem(
                "Milk", "Whole", 1000, 2.00, 5,
                Temperature.REFRIGERATED, "m.jpg"
        ); // total = 10 €

        shelf.getGoods().add(apple);
        shelf.getGoods().add(milk);

        // --- Validate totals ---
        assertEquals(20.0, s.getTotalValue());
        assertEquals(15, s.getTotalQuantity());
    }

    @Test
    void testToStringContainsNameAndCount() {
        Supermarket s = new Supermarket("ShopX");

        String text = s.toString();

        assertTrue(text.contains("ShopX"));
        assertTrue(text.contains("floorAreas=0"));
    }
}