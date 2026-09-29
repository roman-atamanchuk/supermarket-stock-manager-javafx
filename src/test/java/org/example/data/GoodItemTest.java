package org.example.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoodItemTest {

    private GoodItem item;

    @BeforeEach
    void setup() {
        item = new GoodItem(
                "Apple",
                "Fresh red apple",
                100,
                1.00,
                5,
                Temperature.UNREFRIGERATED,
                "photo.jpg"
        );
    }

    // ============================================================
    // CONSTRUCTOR BEHAVIOUR
    // ============================================================

    @Test
    void testConstructorNormalValues() {
        assertEquals("Apple", item.getName());
        assertEquals("Fresh red apple", item.getDescription());
        assertEquals(100, item.getSizeWeight());
        assertEquals(1.00, item.getUnitPrice());
        assertEquals(5, item.getQuantity());
        assertEquals(Temperature.UNREFRIGERATED, item.getTemperature());
        assertEquals("photo.jpg", item.getPhotoUrl());
        assertNotNull(item.getId());
    }

    @Test
    void testConstructorNegativeValuesCorrected() {
        GoodItem i = new GoodItem(
                "Test", "Desc", -50, -1.5, -3,
                Temperature.REFRIGERATED, null
        );

        assertEquals(0, i.getSizeWeight());
        assertEquals(0, i.getUnitPrice());
        assertEquals(0, i.getQuantity());
        assertEquals("", i.getPhotoUrl());
    }

    @Test
    void testConstructorNullNameReplaced() {
        GoodItem i = new GoodItem(
                null, "D", 10, 1.0, 1, Temperature.UNREFRIGERATED, null
        );

        assertEquals("Unnamed Item", i.getName());
    }

    // ============================================================
    // SETTERS VALIDATION
    // ============================================================

    @Test
    void testSetNameValid() {
        item.setName(" Banana ");
        assertEquals("Banana", item.getName());
    }

    @Test
    void testSetNameInvalid() {
        assertThrows(IllegalArgumentException.class, () -> item.setName(""));
        assertThrows(IllegalArgumentException.class, () -> item.setName("   "));
        assertThrows(IllegalArgumentException.class, () -> item.setName(null));
    }

    @Test
    void testSetDescriptionNullAllowed() {
        item.setDescription(null);
        assertEquals("", item.getDescription());
    }

    @Test
    void testSetSizeWeight() {
        item.setSizeWeight(250);
        assertEquals(250, item.getSizeWeight());
    }

    @Test
    void testSetSizeWeightNegative() {
        assertThrows(IllegalArgumentException.class, () -> item.setSizeWeight(-10));
    }

    @Test
    void testSetPrice() {
        item.setUnitPrice(2.5);
        assertEquals(2.5, item.getUnitPrice());
    }

    @Test
    void testSetPriceNegative() {
        assertThrows(IllegalArgumentException.class, () -> item.setUnitPrice(-1));
    }

    @Test
    void testSetQuantity() {
        item.setQuantity(20);
        assertEquals(20, item.getQuantity());
    }

    @Test
    void testSetQuantityNegative() {
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(-3));
    }

    @Test
    void testSetTemperatureNullDefaults() {
        item.setTemperature(null);
        assertEquals(Temperature.UNREFRIGERATED, item.getTemperature());
    }

    // ============================================================
    // TOTAL VALUE
    // ============================================================

    @Test
    void testGetTotalValue() {
        assertEquals(1.00 * 5, item.getTotalValue());
    }

    @Test
    void testGetTotalQuantity() {
        assertEquals(5, item.getTotalQuantity());
    }

    // ============================================================
    // MERGE LOGIC
    // ============================================================

    @Test
    void testMergeAddsQuantities() {
        GoodItem other = new GoodItem(
                "Apple",
                "Fresh",
                100,
                1.00,
                3,
                Temperature.UNREFRIGERATED,
                "img.jpg"
        );

        item.merge(other);
        assertEquals(8, item.getQuantity());
    }

    @Test
    void testMergeUpdatesPrice() {
        GoodItem other = new GoodItem(
                "Apple", "Fresh", 100,
                2.50, 1,
                Temperature.UNREFRIGERATED, "p.jpg"
        );

        item.merge(other);
        assertEquals(2.50, item.getUnitPrice());
    }

    @Test
    void testMergeUpdatesPhoto() {
        GoodItem other = new GoodItem(
                "Apple", "Fresh", 100,
                1.00, 1,
                Temperature.UNREFRIGERATED, "newPic.png"
        );

        item.merge(other);
        assertEquals("newPic.png", item.getPhotoUrl());
    }

    @Test
    void testMergeTemperatureUpdated() {
        GoodItem other = new GoodItem(
                "Apple", "Fresh", 100,
                1.00, 1,
                Temperature.FROZEN, "x.jpg"
        );

        item.merge(other);
        assertEquals(Temperature.FROZEN, item.getTemperature());
    }

    @Test
    void testMergeDoesNotChangeDescription() {
        String originalDesc = item.getDescription();

        GoodItem other = new GoodItem(
                "Apple", "DIFFERENT DESC", 100,
                1.00, 1,
                Temperature.UNREFRIGERATED, "x"
        );

        item.merge(other);
        assertEquals(originalDesc, item.getDescription());
    }

    @Test
    void testMergeNullDoesNothing() {
        item.merge(null);
        assertEquals(5, item.getQuantity());
    }

    // ============================================================
    // EQUALITY & HASHCODE
    // ============================================================

    @Test
    void testEqualsSameNameDescriptionAndWeight() {
        GoodItem a = new GoodItem("Milk", "Full fat", 500, 1, 1, Temperature.REFRIGERATED, "");
        GoodItem b = new GoodItem("milk", "FULL FAT", 500, 2, 3, Temperature.UNREFRIGERATED, "");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testNotEqualsDifferentDescription() {
        GoodItem a = new GoodItem("Milk", "Full fat", 500, 1, 1, Temperature.REFRIGERATED, "");
        GoodItem b = new GoodItem("Milk", "Skimmed", 500, 1, 1, Temperature.REFRIGERATED, "");

        assertNotEquals(a, b);
    }

    @Test
    void testNotEqualsDifferentName() {
        GoodItem a = new GoodItem("Milk", "D", 500, 1, 1, Temperature.REFRIGERATED, "");
        GoodItem b = new GoodItem("Water", "D", 500, 1, 1, Temperature.REFRIGERATED, "");

        assertNotEquals(a, b);
    }

    @Test
    void testNotEqualsDifferentWeight() {
        GoodItem a = new GoodItem("Milk", "D", 500, 1, 1, Temperature.REFRIGERATED, "");
        GoodItem b = new GoodItem("Milk", "D", 1000, 1, 1, Temperature.REFRIGERATED, "");

        assertNotEquals(a, b);
    }

    @Test
    void testEqualsSelf() {
        assertEquals(item, item);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(item, null);
    }

    @Test
    void testEqualsDifferentType() {
        assertNotEquals(item, "text");
    }

    // ============================================================
    // PARENT SHELF
    // ============================================================

    @Test
    void testParentShelf() {
        Shelf shelf = new Shelf(1);
        item.setParentShelf(shelf);

        assertSame(shelf, item.getParentShelf());
    }

    // ============================================================
    // TO STRING
    // ============================================================

    @Test
    void testToStringContainsName() {
        String text = item.toString();
        assertTrue(text.contains("Apple"));
        assertTrue(text.contains("qty=5"));
    }
}