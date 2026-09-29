package org.example.util;

import org.example.data.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoogleSheetsServiceTest {

    // Saves to rows and loads back, the same way the app does with the sheet.
    private Supermarket roundTrip(Supermarket market) {
        List<List<Object>> rows = GoogleSheetsService.buildRows(market);
        return GoogleSheetsService.parseRows(rows.subList(1, rows.size())); // skip header
    }

    @Test
    void emptyFloorDoesNotGainBlankAisleAfterReload() {
        Supermarket market = new Supermarket("Shop");
        FloorArea floor = new FloorArea("Dairy", "Ground");
        market.getFloorAreas().add(floor);

        Supermarket loaded = roundTrip(market);

        FloorArea loadedFloor = loaded.getFloorAreas().get(0);
        assertEquals("Dairy", loadedFloor.getTitle());
        assertTrue(loadedFloor.getAisles().isEmpty());
    }

    @Test
    void emptyAisleDoesNotGainShelfZeroAfterReload() {
        Supermarket market = new Supermarket("Shop");
        FloorArea floor = new FloorArea("Dairy", "Ground");
        floor.addAisle(new Aisle("Cheese", Temperature.REFRIGERATED, 10, 2));
        market.getFloorAreas().add(floor);

        Supermarket loaded = roundTrip(market);

        Aisle loadedAisle = loaded.getFloorAreas().get(0).getAisles().get(0);
        assertEquals("Cheese", loadedAisle.getName());
        assertTrue(loadedAisle.getShelves().isEmpty());
    }

    @Test
    void goodsSurviveReloadWithParentLinks() {
        Supermarket market = new Supermarket("Shop");
        FloorArea floor = new FloorArea("Dairy", "Ground");
        Aisle aisle = new Aisle("Milk", Temperature.REFRIGERATED, 10, 2);
        Shelf shelf = new Shelf(0);
        shelf.getGoods().add(new GoodItem("Milk", "Full fat", 1000, 1.5, 10,
                Temperature.REFRIGERATED, ""));
        aisle.addShelf(shelf);
        floor.addAisle(aisle);
        market.getFloorAreas().add(floor);

        Supermarket loaded = roundTrip(market);

        Shelf loadedShelf = loaded.getFloorAreas().get(0).getAisles().get(0).getShelves().get(0);
        assertEquals(0, loadedShelf.getNumber());
        GoodItem good = loadedShelf.getGoods().get(0);
        assertEquals("Full fat", good.getDescription());
        assertEquals(10, good.getQuantity());
        assertSame(loadedShelf, good.getParentShelf());
        assertEquals(15.0, loaded.getTotalValue(), 1e-9);
    }
}
