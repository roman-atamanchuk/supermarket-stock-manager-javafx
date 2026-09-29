package org.example.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShelfTest {

    private Shelf shelf;

    @BeforeEach
    void setup() {
        shelf = new Shelf(5);
    }

    // ============================================================
    // BASIC CONSTRUCTOR TESTS
    // ============================================================

    @Test
    void testConstructor() {
        assertEquals(5, shelf.getNumber());
        assertNotNull(shelf.getGoods());
        assertTrue(shelf.getGoods().isEmpty());
        assertNotNull(shelf.getId());
    }

    // ============================================================
    // PARENT LINK
    // ============================================================

    @Test
    void testSetParentAisle() {
        Aisle aisle = new Aisle("A1", Temperature.UNREFRIGERATED, 1.0, 1.0);
        shelf.setParentAisle(aisle);

        assertSame(aisle, shelf.getParentAisle());
    }

    // ============================================================
    // TOTAL VALUE + QUANTITY
    // ============================================================

    @Test
    void testTotalValueAndQuantity() {

        GoodItem item1 = new GoodItem(
                "Apple", "Fresh", 100, 1.00, 3,
                Temperature.UNREFRIGERATED, "a.jpg"
        );

        GoodItem item2 = new GoodItem(
                "Milk", "Whole", 1000, 1.50, 2,
                Temperature.REFRIGERATED, "m.jpg"
        );

        shelf.getGoods().add(item1);
        shelf.getGoods().add(item2);

        double expectedValue = (1.00 * 3) + (1.50 * 2);  // 3 + 3 = 6
        int expectedQty = 3 + 2;                        // 5

        assertEquals(expectedValue, shelf.getTotalValue(), 0.001);
        assertEquals(expectedQty, shelf.getTotalQuantity());
    }

    @Test
    void testTotalsEmptyShelf() {
        assertEquals(0, shelf.getTotalValue());
        assertEquals(0, shelf.getTotalQuantity());
    }

    // ============================================================
    // EQUALS + HASHCODE
    // ============================================================

    @Test
    void testEqualsSameNumber() {
        Shelf s1 = new Shelf(10);
        Shelf s2 = new Shelf(10);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testNotEqualsDifferentNumber() {
        Shelf s1 = new Shelf(1);
        Shelf s2 = new Shelf(2);

        assertNotEquals(s1, s2);
    }

    @Test
    void testEqualsSelf() {
        assertEquals(shelf, shelf);
    }

    @Test
    void testEqualsDifferentType() {
        assertNotEquals(shelf, "text");
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(shelf, null);
    }

    // ============================================================
    // TO STRING
    // ============================================================

    @Test
    void testToStringContainsNumber() {
        String text = shelf.toString();
        assertTrue(text.contains("number=5"));
    }
}