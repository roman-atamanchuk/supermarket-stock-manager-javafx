package org.example.data;

import org.example.util.SinglyLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorAreaTest {

    private FloorArea area;

    @BeforeEach
    void setup() {
        area = new FloorArea("Dairy", "Level 0");
    }

    // ============================================================
    // BASIC FIELD TESTS
    // ============================================================

    @Test
    void testConstructorAndGetters() {
        assertEquals("Dairy", area.getTitle());
        assertEquals("Level 0", area.getLevel());
        assertNotNull(area.getAisles());
        assertTrue(area.getAisles().isEmpty());
        assertNotNull(area.getId());
    }

    @Test
    void testSetters() {
        area.setTitle("Frozen");
        area.setLevel("Level 1");

        assertEquals("Frozen", area.getTitle());
        assertEquals("Level 1", area.getLevel());
    }

    // ============================================================
    // ADD AISLE
    // ============================================================

    @Test
    void testAddAisleSetsParent() {
        Aisle aisle = new Aisle("Cheese", Temperature.REFRIGERATED, 10, 3);

        area.addAisle(aisle);

        assertEquals(1, area.getAisles().size());
        assertEquals(area, aisle.getParentArea());
    }

    @Test
    void testAddAisleNullSafe() {
        area.addAisle(null);
        assertEquals(0, area.getAisles().size());
    }

    // ============================================================
    // TOTAL VALUE + QUANTITY
    // ============================================================

    @Test
    void testTotalValueAndQuantity() {
        // ========== Aisle 1 ==========
        Aisle a1 = new Aisle("A1", Temperature.UNREFRIGERATED, 5, 2);
        Shelf s1 = new Shelf(1);
        a1.addShelf(s1);

        s1.getGoods().add(
                new GoodItem("Milk", "Fresh", 1000, 1.50, 10,
                        Temperature.REFRIGERATED, "milk.jpg")
        );
        s1.getGoods().add(
                new GoodItem("Yogurt", "Strawberry", 200, 1.00, 5,
                        Temperature.REFRIGERATED, "yo.jpg")
        );

        // ========== Aisle 2 ==========
        Aisle a2 = new Aisle("A2", Temperature.FROZEN, 5, 2);
        Shelf s2 = new Shelf(2);
        a2.addShelf(s2);

        s2.getGoods().add(
                new GoodItem("IceCream", "Vanilla", 500, 3.00, 4,
                        Temperature.FROZEN, "ice.jpg")
        );

        // Add aisles to area
        area.addAisle(a1);
        area.addAisle(a2);

        // Manually expected totals
        double expectedValue =
                (1.50 * 10)   // Milk
                        + (1.00 * 5)    // Yogurt
                        + (3.00 * 4);   // Ice Cream

        int expectedQty = 10 + 5 + 4;

        assertEquals(expectedValue, area.getTotalValue(), 0.001);
        assertEquals(expectedQty, area.getTotalQuantity());
    }

    @Test
    void testTotalsEmpty() {
        assertEquals(0, area.getTotalValue());
        assertEquals(0, area.getTotalQuantity());
    }

    // ============================================================
    // EQUALS + HASHCODE
    // ============================================================

    @Test
    void testEqualsSameTitleIgnoreCase() {
        FloorArea a1 = new FloorArea("Dairy", "L1");
        FloorArea a2 = new FloorArea("dairY", "L2");

        assertEquals(a1, a2);
    }

    @Test
    void testNotEqualsDifferentTitle() {
        FloorArea a1 = new FloorArea("Dairy", "X");
        FloorArea a2 = new FloorArea("Meat", "Y");

        assertNotEquals(a1, a2);
    }

    @Test
    void testHashCodeMatchesEquals() {
        FloorArea a1 = new FloorArea("Frozen", "L1");
        FloorArea a2 = new FloorArea("frozen", "L2");

        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testEqualsSelf() {
        assertEquals(area, area);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(area, null);
    }

    @Test
    void testEqualsDifferentObjectType() {
        assertNotEquals(area, "NotAFloorArea");
    }

    // ============================================================
    // TO STRING
    // ============================================================

    @Test
    void testToStringNotEmpty() {
        String text = area.toString();
        assertTrue(text.contains("Dairy"));
        assertTrue(text.contains("Level 0"));
    }
}