package org.example.store;

import org.example.data.GoodItem;
import org.example.data.Temperature;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class GoodItemControllerTest {

    private GoodItem item;
    private GoodItemController controller;

    @BeforeEach
    void setUp() {
        item = new GoodItem(
                "Apple",
                "Fresh red apple",
                150,
                0.50,
                10,
                Temperature.UNREFRIGERATED,
                "photo.jpg"
        );

        controller = new GoodItemController(item);
    }

    @AfterEach
    void tearDown() {
        item = null;
        controller = null;
    }

    // --- Update Name ---
    @Test
    void testUpdateName() {
        controller.updateName("Green Apple");
        assertEquals("Green Apple", item.getName());
    }

    @Test
    void testUpdateNameInvalid() {
        controller.updateName("   ");
        assertEquals("Apple", item.getName());
    }

    // --- Update Description ---
    @Test
    void testUpdateDescription() {
        controller.updateDescription("Very fresh apple");
        assertEquals("Very fresh apple", item.getDescription());
    }

    @Test
    void testUpdateDescriptionInvalid() {
        controller.updateDescription("");
        assertEquals("Fresh red apple", item.getDescription());
    }

    // --- Update Quantity ---
    @Test
    void testUpdateQuantity() {
        controller.updateQuantity(20);
        assertEquals(20, item.getQuantity());
    }

    @Test
    void testUpdateQuantityInvalid() {
        controller.updateQuantity(-5);
        assertEquals(10, item.getQuantity());
    }

    // --- Update Price ---
    @Test
    void testUpdatePrice() {
        controller.updatePrice(1.25);
        assertEquals(1.25, item.getUnitPrice());
    }

    @Test
    void testUpdatePriceInvalid() {
        controller.updatePrice(-3.0);
        assertEquals(0.50, item.getUnitPrice());
    }

    // --- Update Weight ---
    @Test
    void testUpdateWeight() {
        controller.updateWeight(200);
        assertEquals(200, item.getSizeWeight());
    }

    @Test
    void testUpdateWeightInvalid() {
        controller.updateWeight(-1.0);
        assertEquals(150, item.getSizeWeight());
    }

    // --- Update Photo URL ---
    @Test
    void testUpdatePhoto() {
        controller.updatePhoto("newphoto.png");
        assertEquals("newphoto.png", item.getPhotoUrl());
    }

    @Test
    void testUpdatePhotoInvalid() {
        controller.updatePhoto(" ");
        assertEquals("photo.jpg", item.getPhotoUrl());
    }

    // --- Update Temperature ---
    @Test
    void testUpdateTemperature() {
        controller.updateTemperature(Temperature.REFRIGERATED);
        assertEquals(Temperature.REFRIGERATED, item.getTemperature());
    }

    @Test
    void testUpdateTemperatureInvalid() {
        controller.updateTemperature(null);
        assertEquals(Temperature.UNREFRIGERATED, item.getTemperature());
    }

    // --- Merge ---
    @Test
    void testMergeWith() {
        GoodItem other = new GoodItem(
                "Apple",
                "Fresh",
                5,
                0.60,
                100,
                Temperature.UNREFRIGERATED,
                "img2.jpg"
        );

        controller.mergeWith(other);

        // default merge behavior: quantity adds
        assertEquals(110, item.getQuantity(), "Quantities should be added together");
        assertEquals("Fresh red apple", item.getDescription(), "Description should stay original");
    }

    @Test
    void testMergeWithNull() {
        controller.mergeWith(null);
        assertEquals(10, item.getQuantity(), "Should not merge with null");
    }

    // --- Getter ---
    @Test
    void testGetGoodItem() {
        assertSame(item, controller.getGoodItem());
    }
}