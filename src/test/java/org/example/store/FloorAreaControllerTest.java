package org.example.store;

import org.example.data.Aisle;
import org.example.data.FloorArea;
import org.example.data.Supermarket;
import org.example.data.Temperature;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class FloorAreaControllerTest {

    private Supermarket market;
    private FloorArea floorArea;
    private FloorAreaController controller;

    @BeforeEach
    void setUp() {
        market = new Supermarket("MyMarket");

        // Real constructor: FloorArea(String title, String level)
        floorArea = new FloorArea("Area1", "ground");
        floorArea.setParentSupermarket(market);

        // Add the FloorArea to the supermarket's list
        market.getFloorAreas().add(floorArea);

        controller = new FloorAreaController(floorArea);
    }

    @AfterEach
    void tearDown() {
        market = null;
        floorArea = null;
        controller = null;
    }

    // ============================
    //       ADD AISLE TEST
    // ============================
    @Test
    void addAisle() {
        boolean added = controller.addAisle("Cheese", Temperature.REFRIGERATED, 4, 2);
        assertTrue(added, "Aisle 'Cheese' should be added");
        assertEquals(1, floorArea.getAisles().size(), "Floor area should have 1 aisle");

        // Duplicate in same FloorArea
        boolean duplicateSame = controller.addAisle("Cheese", Temperature.REFRIGERATED, 4, 2);
        assertFalse(duplicateSame, "Duplicate aisle must NOT be added");

        // Duplicate in ANOTHER FloorArea of same supermarket
        FloorArea other = new FloorArea("Area2", "ground");
        other.setParentSupermarket(market);
        market.getFloorAreas().add(other);

        other.addAisle(new Aisle("Milk", Temperature.UNREFRIGERATED, 3, 2));

        boolean duplicateOther = controller.addAisle("Milk", Temperature.UNREFRIGERATED, 3, 2);
        assertFalse(duplicateOther, "Duplicate name across the supermarket must NOT be added");
    }

    // ============================
    //     REMOVE AISLE TEST
    // ============================
    @Test
    void removeAisle() {
        controller.addAisle("Bread", Temperature.UNREFRIGERATED, 3, 2);
        controller.addAisle("Fruits", Temperature.FROZEN, 4, 3);

        assertEquals(2, floorArea.getAisles().size(), "Two aisles added");

        controller.removeAisle("Bread");
        assertEquals(1, floorArea.getAisles().size(), "Bread should be removed");

        // Removing missing aisle
        controller.removeAisle("NoSuchAisle");
        assertEquals(1, floorArea.getAisles().size(), "List must remain unchanged");
    }

    // ============================
    //     SEARCH AISLE TEST
    // ============================
    @Test
    void searchAisle() {
        controller.addAisle("Yogurt", Temperature.REFRIGERATED, 5, 2);

        Aisle found = controller.searchAisle("Yogurt");
        assertNotNull(found, "Should find existing aisle");
        assertEquals("Yogurt", found.getName());

        Aisle missing = controller.searchAisle("NothingHere");
        assertNull(missing, "Search must return null if no aisle found");
    }

    // ============================
    //     PRINT AISLES TEST
    // ============================
    @Test
    void printAllAisles() {
        controller.addAisle("A1", Temperature.FROZEN, 2, 1);
        controller.addAisle("A2", Temperature.REFRIGERATED, 2, 1);

        System.out.println("\n--- PRINT TEST (FloorAreaController) ---");
        controller.printAllAisles();
        System.out.println("----------------------------------------");

        assertEquals(2, floorArea.getAisles().size(),
                "Two aisles should exist and be printed");
    }

    // ============================
    //     GETTER TEST
    // ============================
    @Test
    void getFloorArea() {
        assertEquals("Area1", controller.getFloorArea().getTitle());
        assertSame(floorArea, controller.getFloorArea());
    }
}