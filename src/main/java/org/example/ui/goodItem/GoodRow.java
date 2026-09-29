package org.example.ui.goodItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.example.data.GoodItem;

public class GoodRow {

    private final GoodItem source;
    private final SimpleStringProperty name, desc, temp, photoUrl;
    private final SimpleIntegerProperty qty;
    private final SimpleDoubleProperty price;

    // 🆕 New hierarchy fields
    private final SimpleStringProperty floorTitle, aisleNumber, shelfNumber;

    // ============================================================
    // 🧩 Constructor from individual values
    // ============================================================
    public GoodRow(String name, String desc, int qty, double price, String temp, String photoUrl,
                   String floorTitle, String aisleNumber, String shelfNumber) {
        this(null, name, desc, qty, price, temp, photoUrl, floorTitle, aisleNumber, shelfNumber);
    }

    private GoodRow(GoodItem source, String name, String desc, int qty, double price, String temp,
                    String photoUrl, String floorTitle, String aisleNumber, String shelfNumber) {
        this.source = source;
        this.name = new SimpleStringProperty(name);
        this.desc = new SimpleStringProperty(desc);
        this.qty = new SimpleIntegerProperty(qty);
        this.price = new SimpleDoubleProperty(price);
        this.temp = new SimpleStringProperty(temp);
        this.photoUrl = new SimpleStringProperty(photoUrl);
        this.floorTitle = new SimpleStringProperty(floorTitle);
        this.aisleNumber = new SimpleStringProperty(aisleNumber);
        this.shelfNumber = new SimpleStringProperty(shelfNumber);
    }

    // ============================================================
    // 🧩 Constructor from GoodItem
    // ============================================================
    public GoodRow(GoodItem g) {
        this(
                g,
                g.getName(),
                g.getDescription(),
                g.getQuantity(),
                g.getUnitPrice(),
                g.getTemperature() != null ? g.getTemperature().name() : "UNSPECIFIED",
                g.getPhotoUrl(),
                g.getParentShelf() != null && g.getParentShelf().getParentAisle() != null &&
                        g.getParentShelf().getParentAisle().getParentArea() != null
                        ? g.getParentShelf().getParentAisle().getParentArea().getTitle()
                        : "Unknown Floor",
                g.getParentShelf() != null && g.getParentShelf().getParentAisle() != null
                        ? String.valueOf(g.getParentShelf().getParentAisle().getName())
                        : "Unknown Aisle",
                g.getParentShelf() != null
                        ? String.valueOf(g.getParentShelf().getNumber())
                        : "Unknown Shelf"
        );
    }

    // ============================================================
    // 🔍 Getters
    // ============================================================
    public String getName() { return name.get(); }
    public String getDesc() { return desc.get(); }
    public int getQty() { return qty.get(); }
    public double getPrice() { return price.get(); }
    public String getTemp() { return temp.get(); }
    public String getPhotoUrl() { return photoUrl.get(); }
    public String getFloorTitle() { return floorTitle.get(); }
    public String getAisleNumber() { return aisleNumber.get(); }
    public String getShelfNumber() { return shelfNumber.get(); }
    public GoodItem getSource() { return source; }
}