# Supermarket Stock Manager (JavaFX)

A JavaFX desktop app for managing a supermarket's layout and stock, saved to
Google Sheets. Built as a Data Structures assignment: every collection in the
app is a hand-written generic singly linked list, not a Java `List`.

## Features

- Model the store as **Supermarket → Floor areas → Aisles → Shelves → Goods**.
- Add, remove and search floors, aisles, shelves and goods.
- Adding a product that is already on the shelf (same name, description and
  size) increases its quantity instead of creating a duplicate.
- Remove part of an item's stock or all of it.
- Search goods by name across the whole store.
- Running totals of stock value and quantity at every level.
- Every change auto-saves to a Google Sheet; the store is loaded back on start.

## Running

Requires JDK 21 and Maven.

```
mvn javafx:run          # start the app
mvn test                # run the unit tests
```

In IntelliJ, run `org.example.Launcher` (running `App` directly fails with
"JavaFX runtime components are missing").

### Google Sheets setup

The app needs a Google Cloud **service account** key with access to the Sheets
API, and the target spreadsheet shared with the service account's email.

1. Create a service account and download its JSON key.
2. Save it as `~/.config/supermarket/credentials.json`, or point the
   `SUPERMARKET_CREDENTIALS` environment variable at it.
3. Set `SPREADSHEET_ID` in `GoogleSheetsService` to your spreadsheet.

Never commit the key: `credentials.json` is in `.gitignore`.

Without a key the app still starts, with a new empty supermarket; saving fails
with a message in the console.

## Data structures

| Structure | Where | Purpose |
|---|---|---|
| `SinglyLinkedList<T>` with head and tail pointers | `util` | Every collection: floors, aisles, shelves, goods, search results. `add` is O(1) thanks to the tail pointer; `get`, `find`, `contains` and `remove` are O(n). |
| `Node<T>` | `util` | One element of the linked list. |
| Tree of parent/child links | `data` | Each shelf knows its aisle, each aisle its floor, each floor its supermarket, so a good can show its full location. |

Equality is defined per level so the lists can find and de-duplicate
entries: floors by title, aisles by name, shelves by number, and goods by
name + description + size (all case-insensitive).

## How saving works

- `GoogleSheetsService.buildRows` flattens the tree into one sheet row per
  good (or per empty floor / aisle / shelf).
- `DataSync.save` takes that snapshot on the UI thread and uploads it on a
  single background thread, so saves happen in order and never block the UI.
- Upload writes the new rows first, then clears any old rows below them, so a
  failed upload never leaves the sheet empty.
- `GoogleSheetsService.parseRows` rebuilds the tree on start-up.

## Project structure

```
src/main/java/org/example/
  App.java, Launcher.java      entry points
  data/                        Supermarket, FloorArea, Aisle, Shelf, GoodItem, Temperature
  store/                       controllers with the add/remove/search logic
  ui/                          JavaFX screens (dashboard, aisles, shelves, goods)
  util/                        SinglyLinkedList, Node, GoogleSheetsService, DataSync
src/main/resources/org/example/ui/   FXML layouts
src/test/java/org/example/           JUnit 5 tests (139)
```
