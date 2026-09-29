package org.example.store;

import org.example.data.*;
import org.example.util.SinglyLinkedList;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SupermarketControllerTest {

    private Supermarket supermarket;
    private SupermarketController controller;

    @BeforeEach
    void setUp() {
        supermarket = new Supermarket("My Market");
        controller = new SupermarketController(supermarket);
    }

    // =====================================================================
    // ADD
    // =====================================================================

    @Test
    void testAddFloorArea() {
        boolean added = controller.addFloorArea("Ground Floor", "Level 0");

        assertTrue(added);
        assertEquals(1, supermarket.getFloorAreas().size());

        FloorArea f = supermarket.getFloorAreas().getHead().getData();
        assertEquals("Ground Floor", f.getTitle());
        assertEquals("Level 0", f.getLevel());
        assertEquals(supermarket, f.getParentSupermarket());
    }

    @Test
    void testAddFloorAreaDuplicate() {
        controller.addFloorArea("Electronics", "L1");
        boolean addedAgain = controller.addFloorArea("Electronics", "L2");

        assertFalse(addedAgain, "Duplicate title must be rejected");
        assertEquals(1, supermarket.getFloorAreas().size());
    }

    @Test
    void testAddFloorAreaInvalid() {
        boolean added = controller.addFloorArea("   ", "Level X");
        assertFalse(added);
        assertEquals(0, supermarket.getFloorAreas().size());
    }

    // =====================================================================
    // REMOVE
    // =====================================================================

    @Test
    void testRemoveFloorArea() {
        controller.addFloorArea("Books", "L1");

        controller.removeFloorArea("Books");

        assertEquals(0, supermarket.getFloorAreas().size());
    }

    @Test
    void testRemoveFloorAreaNotFound() {
        controller.removeFloorArea("DoesNotExist");
        assertEquals(0, supermarket.getFloorAreas().size());
    }

    // =====================================================================
    // SEARCH EXACT MATCH
    // =====================================================================

    @Test
    void testSearchFloorAreaFound() {
        controller.addFloorArea("Clothing", "L1");

        FloorArea found = supermarket.getFloorAreas().find(
                new FloorArea("Clothing", "")
        );

        assertNotNull(found);
        assertEquals("Clothing", found.getTitle());
    }

    @Test
    void testSearchFloorAreaNotFound() {
        FloorArea result = supermarket.getFloorAreas().find(
                new FloorArea("Ghost", "")
        );
        assertNull(result);
    }

    // =====================================================================
    // PROGRESSIVE SEARCH
    // =====================================================================

    @Test
    void testProgressiveSearch() {
        controller.addFloorArea("Food", "L1");
        controller.addFloorArea("Furniture", "L2");
        controller.addFloorArea("Fitness", "L3");

        // ✔ Correct expectation: only Furniture starts with "Fu"
        SinglyLinkedList<FloorArea> result = controller.searchFloorAreasProgressive("Fu");

        assertEquals(1, result.size(), "Only Furniture starts with 'Fu'");
    }

    @Test
    void testProgressiveSearchNoMatch() {
        controller.addFloorArea("Toys", "L1");

        SinglyLinkedList<FloorArea> result = controller.searchFloorAreasProgressive("XYZ");

        assertEquals(0, result.size());
    }

    @Test
    void testProgressiveSearchSingleLetter() {
        controller.addFloorArea("Food", "L1");
        controller.addFloorArea("Furniture", "L2");
        controller.addFloorArea("Fitness", "L3");

        SinglyLinkedList<FloorArea> result = controller.searchFloorAreasProgressive("F");

        assertEquals(3, result.size(), "Food + Furniture + Fitness expected");
    }

    // =====================================================================
    // SEARCH GOODS BY NAME
    // =====================================================================

    @Test
    void testSearchGoodsByName() {
        FloorArea f = new FloorArea("Groceries", "L0");
        supermarket.getFloorAreas().add(f);

        Aisle aisle = new Aisle("A1", Temperature.UNREFRIGERATED, 0.0, 0.0);
        f.getAisles().add(aisle);

        Shelf shelf = new Shelf(1);
        aisle.getShelves().add(shelf);

        GoodItem apple = new GoodItem(
                "Apple", "Fresh", 100, 0.50, 10,
                Temperature.UNREFRIGERATED, "a.jpg"
        );
        GoodItem pineapple = new GoodItem(
                "Pineapple", "Tropical", 300, 2.00, 5,
                Temperature.UNREFRIGERATED, "p.jpg"
        );
        GoodItem cola = new GoodItem(
                "Cola", "Drink", 500, 1.20, 8,
                Temperature.UNREFRIGERATED, "c.jpg"
        );

        shelf.getGoods().add(apple);
        shelf.getGoods().add(pineapple);
        shelf.getGoods().add(cola);

        var results = controller.searchGoodsByName("apple");

        assertEquals(2, results.size(), "Should find Apple and Pineapple");
        assertTrue(results.contains(apple));
        assertTrue(results.contains(pineapple));
    }

    @Test
    void testSearchGoodsByNameNotFound() {
        SinglyLinkedList<GoodItem> result = controller.searchGoodsByName("xyz");
        assertEquals(0, result.size());
    }

    @Test
    void testSearchGoodsByNameInvalidTerm() {
        SinglyLinkedList<GoodItem> result = controller.searchGoodsByName("");
        assertEquals(0, result.size());
    }

    // =====================================================================
    // GETTERS + SETTERS
    // =====================================================================

    @Test
    void testGetSupermarket() {
        assertSame(supermarket, controller.getSupermarket());
    }

    @Test
    void testSetSupermarket() {
        Supermarket another = new Supermarket("NewShop");
        controller.setSupermarket(another);
        assertSame(another, controller.getSupermarket());
    }
}