package com.topbloc.codechallenge.db;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseManager {
    private static final String jdbcPrefix = "jdbc:sqlite:";
    private static final String dbName = "challenge.db";
    private static String connectionString;
    private static Connection conn;

    static {
        File dbFile = new File(dbName);
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
    }

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            System.out.println("Connection to SQLite has been established.");
            conn = connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Schema function to reset the database if needed - do not change
    public static void resetDatabase() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
        connect();
        applySchema();
        seedDatabase();
    }

    // Schema function to reset the database if needed - do not change
    private static void applySchema() {
        String itemsSql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String inventorySql = "CREATE TABLE IF NOT EXISTS inventory (\n"
                + "id integer PRIMARY KEY,\n"
                + "item integer NOT NULL UNIQUE references items(id) ON DELETE CASCADE,\n"
                + "stock integer NOT NULL,\n"
                + "capacity integer NOT NULL\n"
                + ");";
        String distributorSql = "CREATE TABLE IF NOT EXISTS distributors (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String distributorPricesSql = "CREATE TABLE IF NOT EXISTS distributor_prices (\n"
                + "id integer PRIMARY KEY,\n"
                + "distributor integer NOT NULL references distributors(id) ON DELETE CASCADE,\n"
                + "item integer NOT NULL references items(id) ON DELETE CASCADE,\n"
                + "cost float NOT NULL\n" +
                ");";

        try {
            System.out.println("Applying schema");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Schema applied");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Schema function to reset the database if needed - do not change
    private static void seedDatabase() {
        String itemsSql = "INSERT INTO items (id, name) VALUES (1, 'Licorice'), (2, 'Good & Plenty'),\n"
            + "(3, 'Smarties'), (4, 'Tootsie Rolls'), (5, 'Necco Wafers'), (6, 'Wax Cola Bottles'), (7, 'Circus Peanuts'), (8, 'Candy Corn'),\n"
            + "(9, 'Twix'), (10, 'Snickers'), (11, 'M&Ms'), (12, 'Skittles'), (13, 'Starburst'), (14, 'Butterfinger'), (15, 'Peach Rings'), (16, 'Gummy Bears'), (17, 'Sour Patch Kids')";
        String inventorySql = "INSERT INTO inventory (item, stock, capacity) VALUES\n"
                + "(1, 22, 25), (2, 4, 20), (3, 15, 25), (4, 30, 50), (5, 14, 15), (6, 8, 10), (7, 10, 10), (8, 30, 40), (9, 17, 70), (10, 43, 65),\n" +
                "(11, 32, 55), (12, 25, 45), (13, 8, 45), (14, 10, 60), (15, 20, 30), (16, 15, 35), (17, 14, 60)";
        String distributorSql = "INSERT INTO distributors (id, name) VALUES (1, 'Candy Corp'), (2, 'The Sweet Suite'), (3, 'Dentists Hate Us')";
        String distributorPricesSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES \n" +
                "(1, 1, 0.81), (1, 2, 0.46), (1, 3, 0.89), (1, 4, 0.45), (2, 2, 0.18), (2, 3, 0.54), (2, 4, 0.67), (2, 5, 0.25), (2, 6, 0.35), (2, 7, 0.23), (2, 8, 0.41), (2, 9, 0.54),\n" +
                "(2, 10, 0.25), (2, 11, 0.52), (2, 12, 0.07), (2, 13, 0.77), (2, 14, 0.93), (2, 15, 0.11), (2, 16, 0.42), (3, 10, 0.47), (3, 11, 0.84), (3, 12, 0.15), (3, 13, 0.07), (3, 14, 0.97),\n" +
                "(3, 15, 0.39), (3, 16, 0.91), (3, 17, 0.85)";

        try {
            System.out.println("Seeding database");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Database seeded");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Helper methods to convert ResultSet to JSON - change if desired, but should not be required
    private static JSONArray convertResultSetToJson(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<String> colNames = IntStream.range(0, columns)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            jsonArray.add(convertRowToJson(rs, colNames));
        }
        return jsonArray;
    }

    private static JSONObject convertRowToJson(ResultSet rs, List<String> colNames) throws SQLException {
        JSONObject obj = new JSONObject();
        for (String colName : colNames) {
            obj.put(colName, rs.getObject(colName));
        }
        return obj;
    }

    // Controller functions - add your routes here. getItems is provided as an example
    // getItems was changed to include the fields specified in README.
    public static JSONArray getItems() {
        String sql = "SELECT " +
             "ITEMS.NAME, " +
             "ITEMS.ID, " +
             "INVENTORY.STOCK, " +
             "INVENTORY.CAPACITY " +
             "FROM ITEMS " +
             "JOIN INVENTORY ON ITEMS.ID = INVENTORY.ITEM;";

        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Returns a JSON array of items where stock is 0.
    public static JSONArray getOutOfStockItems() {
        try {
            ResultSet set = conn.createStatement().executeQuery(
                "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items JOIN inventory ON items.id = inventory.item " +
                "WHERE inventory.stock = 0"
            );
            return convertResultSetToJson(set);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Returns a JSON array with overstocked items.
    public static JSONArray getOverstockedItems() {
        try {
            ResultSet set = conn.createStatement().executeQuery(
                "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items JOIN inventory ON items.id = inventory.item " +
                "WHERE inventory.stock > inventory.capacity"
            );
            return convertResultSetToJson(set);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Returns a JSON array with items in the inventory that are currently low on stock (< 35%).
    public static JSONArray getLowStockItems() {
        try {
            ResultSet set = conn.createStatement().executeQuery(
                "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items JOIN inventory ON items.id = inventory.item " +
                "WHERE inventory.stock < 0.35 * inventory.capacity"
            );
            return convertResultSetToJson(set);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    // Dynamic route that returns a JSON Array with the item details for a given ID.
    public static JSONArray getItemById(int id) {
        String query = "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                    "FROM items JOIN inventory ON items.id = inventory.item " +
                    "WHERE items.id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet set = stmt.executeQuery();
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // returns all distributors, including the id and name
    public static JSONArray getAllDistributors() {
        try {
            ResultSet set = conn.createStatement().executeQuery(
                "SELECT id, name FROM distributors"
            );
            return convertResultSetToJson(set);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Given a distributors ID, returns the items distributed by a given distributor, including the item name, ID, and cost
    public static JSONArray getItemsByDistributorId(int distributorId) {
        String sql = "SELECT items.name, items.id, distributor_prices.cost " +
                    "FROM items JOIN distributor_prices ON items.id = distributor_prices.item " +
                    "WHERE distributor_prices.distributor = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, distributorId);
            ResultSet set = stmt.executeQuery();
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Given an item ID, returns all offerings from all distributors for that item, including the distributor name, ID, and cost
    public static JSONArray getItemOfferingsById(int itemId) {
            String sql = "SELECT distributors.name, distributors.id, distributor_prices.cost " +
                        "FROM distributors JOIN distributor_prices ON distributors.id = distributor_prices.distributor " +
                        "WHERE distributor_prices.item = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, itemId);
                    ResultSet set = stmt.executeQuery();
                    return convertResultSetToJson(set);
            } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return null;
            }
    }


    // Helper function to add a new item to the database. The mapping is id:items.id, other_id:inventory.id.
    // The default stock and capacity are set to 0.
    public static boolean insertItem(int id, String name, int other_id) {
        String item_sql = "INSERT INTO items (id, name) VALUES (?, ?)";
        String inventory_sql = "INSERT INTO inventory (id, item, stock, capacity) VALUES (?, ?, 0, 0)";

        try (
            PreparedStatement stmt = conn.prepareStatement(item_sql);
            PreparedStatement other_stmt = conn.prepareStatement(inventory_sql)
        ) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.executeUpdate();

            other_stmt.setInt(1, other_id);  // inventory.id
            other_stmt.setInt(2, id);        // inventory.item (FK to items.id)
            other_stmt.executeUpdate();

            //conn.commit(); 
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // prints in the Main app what happened in sql

            System.out.println("Insert failed");

            return false;
        }
    }
    // Helper to add a new item to your inventory
    public static boolean insertInventory() {
        return false;
    }
    // next
}






