package org.example.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.example.data.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Supermarket System";
    // Key file lookup order: $SUPERMARKET_CREDENTIALS, ~/.config/supermarket/credentials.json,
    // then credentials.json on the classpath.
    private static final String CREDENTIALS_ENV = "SUPERMARKET_CREDENTIALS";
    private static final Path CREDENTIALS_DEFAULT_PATH =
            Path.of(System.getProperty("user.home"), ".config", "supermarket", "credentials.json");
    private static final String SPREADSHEET_ID = "101fuSwu3hrMMhhW2kNAn8W68BxK7GfMsT8m6qs__7Gk";

    // ======================================================
    // 🔗 Connect to Google Sheets
    // ======================================================
    private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials;
        try (InputStream in = openCredentials()) {
            credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        }

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName(APPLICATION_NAME).build();
    }

    private static InputStream openCredentials() throws IOException {
        String envPath = System.getenv(CREDENTIALS_ENV);
        if (envPath != null && !envPath.isBlank()) {
            return new FileInputStream(envPath);
        }
        if (Files.exists(CREDENTIALS_DEFAULT_PATH)) {
            return Files.newInputStream(CREDENTIALS_DEFAULT_PATH);
        }
        InputStream resource = GoogleSheetsService.class.getResourceAsStream("/credentials.json");
        if (resource != null) {
            return resource;
        }
        throw new IOException("Google credentials not found. Set " + CREDENTIALS_ENV
                + " or place the key at " + CREDENTIALS_DEFAULT_PATH);
    }

    // ======================================================
    // 💾 SAVE supermarket
    // ======================================================
    public static void saveSupermarket(Supermarket market)
            throws IOException, GeneralSecurityException {
        writeRows(market.getName(), buildRows(market));
    }

    /**
     * Converts the supermarket into sheet rows. Call this on the thread that owns the
     * data (the JavaFX thread) so the lists are not read while they are being changed.
     */
    public static List<List<Object>> buildRows(Supermarket market) {
        List<List<Object>> values = new ArrayList<>();
        values.add(List.of(
                "Supermarket", "Floor", "Level", "Aisle", "AisleTemp", "Length", "Width",
                "Shelf", "Good", "Description", "SizeWeight", "Price", "Quantity", "GoodTemp", "PhotoURL"
        ));

        Node<FloorArea> floorNode = market.getFloorAreas().getHead();
        while (floorNode != null) {
            FloorArea floor = floorNode.data;

            Node<Aisle> aisleNode = floor.getAisles().getHead();
            if (aisleNode == null) {
                values.add(List.of(market.getName(), floor.getTitle(), floor.getLevel()));
            }

            while (aisleNode != null) {
                Aisle aisle = aisleNode.data;
                Node<Shelf> shelfNode = aisle.getShelves().getHead();

                String aisleTemp = (aisle.getTemperature() != null)
                        ? aisle.getTemperature().name()
                        : "UNREFRIGERATED";

                if (shelfNode == null) {
                    values.add(List.of(
                            market.getName(),
                            floor.getTitle(),
                            floor.getLevel(),
                            aisle.getName(),
                            aisleTemp,
                            aisle.getLength(),
                            aisle.getWidth()
                    ));
                }

                while (shelfNode != null) {
                    Shelf shelf = shelfNode.data;
                    Node<GoodItem> goodNode = shelf.getGoods().getHead();

                    if (goodNode == null) {
                        values.add(List.of(
                                market.getName(),
                                floor.getTitle(),
                                floor.getLevel(),
                                aisle.getName(),
                                aisleTemp,
                                aisle.getLength(),
                                aisle.getWidth(),
                                shelf.getNumber()
                        ));
                    }

                    while (goodNode != null) {
                        GoodItem g = goodNode.data;
                        String goodTemp = (g.getTemperature() != null)
                                ? g.getTemperature().name()
                                : "UNREFRIGERATED";

                        values.add(List.of(
                                market.getName(),
                                floor.getTitle(),
                                floor.getLevel(),
                                aisle.getName(),
                                aisleTemp,
                                aisle.getLength(),
                                aisle.getWidth(),
                                shelf.getNumber(),
                                g.getName(),
                                g.getDescription(),
                                g.getSizeWeight(),
                                g.getUnitPrice(),
                                g.getQuantity(),
                                goodTemp,
                                g.getPhotoUrl()
                        ));
                        goodNode = goodNode.next;
                    }
                    shelfNode = shelfNode.next;
                }
                aisleNode = aisleNode.next;
            }
            floorNode = floorNode.next;
        }

        return values;
    }

    /**
     * Writes the rows starting at A1, then clears any old rows below them. Writing first
     * means a failed upload leaves the previous data in place instead of an empty sheet.
     */
    public static void writeRows(String marketName, List<List<Object>> values)
            throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        Spreadsheet spreadsheet = service.spreadsheets().get(SPREADSHEET_ID).execute();
        String sheetName = spreadsheet.getSheets().get(0).getProperties().getTitle();

        // Pad every row to 15 columns so short rows (empty floor/aisle/shelf)
        // overwrite any old values left in the same row.
        List<List<Object>> padded = new ArrayList<>();
        for (List<Object> row : values) {
            List<Object> full = new ArrayList<>(row);
            while (full.size() < 15) full.add("");
            padded.add(full);
        }

        ValueRange body = new ValueRange().setValues(padded);
        service.spreadsheets().values()
                .update(SPREADSHEET_ID, sheetName + "!A1", body)
                .setValueInputOption("RAW")
                .execute();

        service.spreadsheets().values()
                .clear(SPREADSHEET_ID, sheetName + "!A" + (values.size() + 1) + ":O",
                        new ClearValuesRequest())
                .execute();

        System.out.println("✅ Saved supermarket '" + marketName + "' to Google Sheets (" + sheetName + ")");
    }

    // ======================================================
    // 📥 LOAD supermarket
    // ======================================================
    public static Supermarket loadSupermarket()
            throws IOException, GeneralSecurityException {

        Sheets service = getSheetsService();
        Spreadsheet spreadsheet = service.spreadsheets().get(SPREADSHEET_ID).execute();
        String sheetName = spreadsheet.getSheets().get(0).getProperties().getTitle();

        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, sheetName + "!A2:O")
                .execute();

        List<List<Object>> rows = response.getValues();
        if (rows == null || rows.isEmpty()) {
            System.out.println("⚠️ No data found in Google Sheets!");
            return null;
        }

        Supermarket market = parseRows(rows);
        System.out.println("✅ Loaded supermarket: " + market.getName());
        return market;
    }

    /**
     * Rebuilds the supermarket from sheet rows (without the header row).
     */
    public static Supermarket parseRows(List<List<Object>> rows) {
        Supermarket market = null;

        for (List<Object> row : rows) {
            String marketName = get(row, 0);
            String floorTitle = get(row, 1);
            String floorLevel = get(row, 2);
            String aisleName = get(row, 3);
            Temperature aisleTemp = parseTemp(get(row, 4));
            double length = parseDouble(get(row, 5));
            double width = parseDouble(get(row, 6));
            String shelfText = get(row, 7);
            int shelfNum = parseInt(shelfText);
            String goodName = get(row, 8);
            String desc = get(row, 9);
            double sizeWeight = parseDouble(get(row, 10));
            double price = parseDouble(get(row, 11));
            int qty = parseInt(get(row, 12));
            Temperature goodTemp = parseTemp(get(row, 13));
            String photo = get(row, 14);

            if (market == null)
                market = new Supermarket(marketName.isEmpty() ? "Unnamed Supermarket" : marketName);

            // ✅ Create or find floor, aisle, shelf.
            // Rows for an empty floor have no aisle, and rows for an empty aisle have no shelf.
            FloorArea floor = findOrCreateFloor(market.getFloorAreas(), floorTitle, floorLevel);
            if (aisleName.isEmpty()) continue;

            Aisle aisle = findOrCreateAisle(floor.getAisles(), aisleName, aisleTemp, length, width);
            if (shelfText.isEmpty()) continue;

            Shelf shelf = findOrCreateShelf(aisle.getShelves(), shelfNum);

            // ✅ Add goods
            if (!goodName.isEmpty()) {
                GoodItem good = new GoodItem(goodName, desc, sizeWeight, price, qty, goodTemp, photo);
                shelf.getGoods().add(good);
            }
        }

        // ✅ Link floors back to supermarket
        Node<FloorArea> fNode = market.getFloorAreas().getHead();
        while (fNode != null) {
            fNode.data.setParentSupermarket(market);
            Node<Aisle> aNode = fNode.data.getAisles().getHead();
            while (aNode != null) {
                aNode.data.setParentArea(fNode.data);
                Node<Shelf> sNode = aNode.data.getShelves().getHead();
                while (sNode != null) {
                    sNode.data.setParentAisle(aNode.data);
                    Node<GoodItem> gNode = sNode.data.getGoods().getHead();
                    while (gNode != null) {
                        gNode.data.setParentShelf(sNode.data);
                        gNode = gNode.next;
                    }
                    sNode = sNode.next;
                }
                aNode = aNode.next;
            }
            fNode = fNode.next;
        }

        return market;
    }

    // ======================================================
    // 🔧 Helpers
    // ======================================================
    private static String get(List<Object> row, int index) {
        return index < row.size() ? row.get(index).toString() : "";
    }

    private static double parseDouble(String s) {
        try { return s.isEmpty() ? 0 : Double.parseDouble(s.replace(",", ".")); }
        catch (Exception e) { return 0; }
    }

    private static int parseInt(String s) {
        try { return s.isEmpty() ? 0 : Integer.parseInt(s.split("\\.")[0]); }
        catch (Exception e) { return 0; }
    }

    private static Temperature parseTemp(String s) {
        try { return Temperature.valueOf(s.toUpperCase()); }
        catch (Exception e) { return Temperature.UNREFRIGERATED; }
    }

    private static FloorArea findOrCreateFloor(SinglyLinkedList<FloorArea> list, String title, String level) {
        Node<FloorArea> node = list.getHead();
        while (node != null) {
            if (node.data.getTitle().equalsIgnoreCase(title)) return node.data;
            node = node.next;
        }
        FloorArea f = new FloorArea(title, level);
        list.add(f);
        return f;
    }

    private static Aisle findOrCreateAisle(SinglyLinkedList<Aisle> list, String name, Temperature t, double l, double w) {
        Node<Aisle> node = list.getHead();
        while (node != null) {
            if (node.data.getName().equalsIgnoreCase(name)) return node.data;
            node = node.next;
        }
        Aisle a = new Aisle(name, t, l, w);
        list.add(a);
        return a;
    }

    private static Shelf findOrCreateShelf(SinglyLinkedList<Shelf> list, int number) {
        Node<Shelf> node = list.getHead();
        while (node != null) {
            if (node.data.getNumber() == number) return node.data;
            node = node.next;
        }
        Shelf s = new Shelf(number);
        list.add(s);
        return s;
    }
    public static void resetDatabase() throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        Spreadsheet spreadsheet = service.spreadsheets().get(SPREADSHEET_ID).execute();

        String sheetName = spreadsheet.getSheets().get(0).getProperties().getTitle();

        // Completely clear the sheet
        service.spreadsheets().values()
                .clear(SPREADSHEET_ID, sheetName, new ClearValuesRequest())
                .execute();

        System.out.println("🧹 Google Sheets database fully reset!");
    }
}