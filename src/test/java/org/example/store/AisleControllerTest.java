package org.example.store;

import org.example.data.Aisle;
import org.example.data.Shelf;
import org.example.data.Temperature;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AisleControllerTest {

    private Aisle aisle;
    private AisleController controller;

    @BeforeEach
    void setUp() {
        // MUST be 4 args — as required
        aisle = new Aisle("A1", Temperature.UNREFRIGERATED, 0.0, 0.0);
        controller = new AisleController(aisle);
    }

    @AfterEach
    void tearDown() {
        aisle = null;
        controller = null;
    }

    // ✅ Test adding shelves
    @Test
    void testAddShelf() {
        controller.addShelf(1);
        assertEquals(1, aisle.getShelves().size(), "Should add one shelf");

        // Duplicate attempt
        controller.addShelf(1);
        assertEquals(1, aisle.getShelves().size(), "Duplicate should NOT be added");

        // Add another
        controller.addShelf(2);
        assertEquals(2, aisle.getShelves().size(), "Should add second shelf");
    }

    // ✅ Test removing shelves
    @Test
    void testRemoveShelf() {
        controller.addShelf(3);
        controller.addShelf(4);

        assertEquals(2, aisle.getShelves().size(), "Two shelves added");

        controller.removeShelf(3);
        assertEquals(1, aisle.getShelves().size(), "Shelf #3 removed");

        controller.removeShelf(99); // does not exist
        assertEquals(1, aisle.getShelves().size(), "No change removing missing shelf");
    }

    // ✅ Test finding shelf
    @Test
    void testFindShelf() {
        controller.addShelf(10);

        Shelf found = controller.findShelf(10);
        assertNotNull(found, "Should find shelf #10");
        assertEquals(10, found.getNumber(), "Shelf number should match");

        Shelf missing = controller.findShelf(999);
        assertNull(missing, "Missing shelf should return null");
    }

    // ✅ searchShelf (via find)
    @Test
    void testSearchShelf() {
        controller.addShelf(5);

        assertNotNull(controller.findShelf(5), "Should find shelf #5");
        assertNull(controller.findShelf(77), "Shelf #77 should NOT exist");
    }

    // ✅ printAllShelves does not break anything
    @Test
    void testPrintAllShelves() {
        controller.addShelf(1);
        controller.addShelf(2);

        System.out.println("\n--- PRINT TEST (AisleController) ---");
        controller.printAllShelves();
        System.out.println("------------------------------------");

        assertEquals(2, aisle.getShelves().size(), "Two shelves printed");
    }

    // ✅ Getter test
    @Test
    void testGetAisle() {
        assertEquals("A1", controller.getAisle().getName(), "Name should match");
        assertTrue(controller.getAisle().getShelves().isEmpty(), "New aisle starts empty");
    }
}