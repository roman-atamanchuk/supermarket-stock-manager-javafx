package org.example.store;

import org.example.data.GoodItem;
import org.example.data.Shelf;
import org.example.data.Temperature;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ShelfControllerTest {

    private Shelf shelf;
    private ShelfController controller;

    @BeforeEach
    void setUp() {
        shelf = new Shelf(1);
        controller = new ShelfController(shelf);
    }

    @AfterEach
    void tearDown() {
        shelf = null;
        controller = null;
    }

    // =====================================================================
    // ✅ ADD NEW ITEM
    // =====================================================================
    @Test
    void testAddGoodItem() {
        controller.addGoodItem(
                "Apple", "Fresh red apple",
                150, 0.50, 10,
                Temperature.UNREFRIGERATED,
                "photo.jpg"
        );

        assertEquals(1, shelf.getGoods().size(), "1 item expected");

        GoodItem item = shelf.getGoods().getHead().getData();

        assertEquals("Apple", item.getName());
        assertEquals("Fresh red apple", item.getDescription());
        assertEquals(150, item.getSizeWeight());
        assertEquals(0.50, item.getUnitPrice());
        assertEquals(10, item.getQuantity());
        assertEquals(Temperature.UNREFRIGERATED, item.getTemperature());
        assertEquals("photo.jpg", item.getPhotoUrl());
        assertEquals(shelf, item.getParentShelf());
    }

    // =====================================================================
    // ❌ INVALID INPUT
    // =====================================================================
    @Test
    void testAddInvalidName() {
        controller.addGoodItem(
                "", "Fresh", 100, 1.0, 5,
                Temperature.UNREFRIGERATED, "img"
        );
        assertEquals(0, shelf.getGoods().size());
    }

    @Test
    void testAddInvalidDescription() {
        controller.addGoodItem(
                "Apple", "   ", 100, 1.0, 5,
                Temperature.UNREFRIGERATED, "img"
        );
        assertEquals(0, shelf.getGoods().size());
    }

    @Test
    void testAddInvalidQuantity() {
        controller.addGoodItem(
                "Apple", "Fresh", 100, 1.0, -5,
                Temperature.UNREFRIGERATED, "img"
        );
        assertEquals(0, shelf.getGoods().size());
    }

    @Test
    void testAddInvalidPrice() {
        controller.addGoodItem(
                "Apple", "Fresh", 100, -2.0, 5,
                Temperature.UNREFRIGERATED, "img"
        );
        assertEquals(0, shelf.getGoods().size());
    }

    // =====================================================================
    // 🔄 MERGE LOGIC
    // =====================================================================
    @Test
    void testMergeGoodItem() {
        controller.addGoodItem(
                "Apple", "Fresh red apple",
                150, 0.50, 10,
                Temperature.UNREFRIGERATED, "a.jpg"
        );

        controller.addGoodItem(
                "Apple", "Fresh red apple",
                150, 0.70, 5,
                Temperature.REFRIGERATED, "b.jpg"
        );

        assertEquals(1, shelf.getGoods().size(), "Should merge into 1 item");

        GoodItem merged = shelf.getGoods().getHead().getData();

        assertEquals(15, merged.getQuantity());
        assertEquals(0.70, merged.getUnitPrice());
        assertEquals("b.jpg", merged.getPhotoUrl());
        assertEquals(Temperature.REFRIGERATED, merged.getTemperature());
    }

    // =====================================================================
    // 🗑 REMOVE FULL QUANTITY
    // =====================================================================
    @Test
    void testRemoveItemFull() {
        controller.addGoodItem(
                "Milk", "Whole milk",
                1000, 1.20, 3,
                Temperature.REFRIGERATED, "milk.jpg"
        );

        assertEquals(1, shelf.getGoods().size());
        controller.removeGoodItem("Milk", "Whole milk", 1000, 3);
        assertEquals(0, shelf.getGoods().size(), "Item should be fully removed");
    }

    // =====================================================================
    // 🗑 REMOVE PARTIAL QUANTITY
    // =====================================================================
    @Test
    void testRemoveItemPartial() {
        controller.addGoodItem(
                "Juice", "Orange juice",
                500, 2.00, 10,
                Temperature.UNREFRIGERATED, "juice.jpg"
        );

        controller.removeGoodItem("Juice", "Orange juice", 500, 4);

        GoodItem item = shelf.getGoods().getHead().getData();

        assertEquals(6, item.getQuantity(), "Quantity should be reduced by 4");
    }

    // =====================================================================
    // 🗑 REMOVE MORE THAN AVAILABLE (should remove item)
    // =====================================================================
    @Test
    void testRemoveItemMoreThanAvailable() {
        controller.addGoodItem(
                "Eggs", "Organic eggs",
                50, 0.20, 6,
                Temperature.UNREFRIGERATED, "eggs.jpg"
        );

        controller.removeGoodItem("Eggs", "Organic eggs", 50, 20);

        assertEquals(0, shelf.getGoods().size(), "Item should be deleted completely");
    }

    // =====================================================================
    // 🗑 REMOVE NOT FOUND
    // =====================================================================
    @Test
    void testRemoveItemNotFound() {
        controller.removeGoodItem("Ghost", "None", 100, 1);
        assertEquals(0, shelf.getGoods().size());
    }

    // =====================================================================
    // 🔍 SEARCH
    // =====================================================================
    @Test
    void testSearchFound() {
        controller.addGoodItem(
                "Bread", "Brown bread",
                500, 1.00, 2,
                Temperature.UNREFRIGERATED, "bread.jpg"
        );

        GoodItem found = controller.searchGoodItem("Bread", "Brown bread", 500);
        assertNotNull(found);
        assertEquals("Bread", found.getName());
    }

    @Test
    void testSearchNotFound() {
        GoodItem result = controller.searchGoodItem("NotHere", "None", 50);
        assertNull(result);
    }

    // =====================================================================
    // GETTER
    // =====================================================================
    @Test
    void testGetShelf() {
        assertSame(shelf, controller.getShelf());
    }

    @Test
    void testRemoveNegativeQuantityIsIgnored() {
        controller.addGoodItem("Milk", "Full fat", 1000, 1.50, 10,
                Temperature.REFRIGERATED, "");

        controller.removeGoodItem("Milk", "Full fat", 1000, -5);

        assertEquals(10, shelf.getGoods().getHead().getData().getQuantity());
    }

    @Test
    void testSameNameDifferentDescriptionIsNotMerged() {
        controller.addGoodItem("Milk", "Full fat", 1000, 1.50, 10,
                Temperature.REFRIGERATED, "");
        controller.addGoodItem("Milk", "Skimmed", 1000, 1.20, 4,
                Temperature.REFRIGERATED, "");

        assertEquals(2, shelf.getGoods().size());
        assertEquals(10, shelf.getGoods().get(0).getQuantity());
        assertEquals(4, shelf.getGoods().get(1).getQuantity());
    }
}
