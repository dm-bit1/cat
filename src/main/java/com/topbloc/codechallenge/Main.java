package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;
import static spark.Spark.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.connect();

        // Don't change this - required for GET and POST requests with the header 'content-type'
        options("/*", (req, res) -> {
            res.header("Access-Control-Allow-Headers", "content-type");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            return "OK";
        });

        // Don't change - if required you can reset your database by hitting this endpoint at localhost:4567/reset
        get("/reset", (req, res) -> {
            DatabaseManager.resetDatabase();
            return "OK";
        });

        // TODO: Add your routes here. a couple of examples are below
        // All items in your inventory, including the item name, ID, amount in stock, and total capacity.
        get("/items", (req, res) -> DatabaseManager.getItems());

        get("/version", (req, res) -> "TopBloc Code Challenge v1.0");

        // All items in your inventory that are currently out of stock
        get("/outOfStockItems", (req, res) -> {
            JSONArray result = DatabaseManager.getOutOfStockItems();
            if (result == null) {
                res.status(400);
                return new JSONObject().put("error", "Database error").toString();
            }
            res.type("application/json");
            return result.toString();
        });

        // All items that are overstocked
        get("/overstockedItems", (req, res) -> {
            JSONArray result = DatabaseManager.getOverstockedItems();
            if (result == null) {
                res.status(400);
                return new JSONObject().put("error", "Database error").toString();
            }
            res.type("application/json");
            return result.toString();
        });

        // All items low on stock
        get("/lowStockItems", (req, res) -> {
            JSONArray result = DatabaseManager.getLowStockItems();
            if (result == null) {
                res.status(400);
                return new JSONObject().put("error", "Database error").toString();
            }
            res.type("application/json");
            return result.toString();
        });

        // Get item by ID
        get("/item/:id", (req, res) -> {
            int itemId = Integer.parseInt(req.params(":id"));
            JSONArray result = DatabaseManager.getItemById(itemId);
            res.type("application/json");
            if (result == null) {
                throw new RuntimeException("Item not found or database error");
            }
            return result.toString();
        });

        // All distributors
        get("/distributors", (req, res) -> {
            JSONArray result = DatabaseManager.getAllDistributors();
            res.type("application/json");
            return (result == null) ? null : result.toString();
        });

        // Items by distributor ID
        get("/distributor/:id", (req, res) -> {
            int distributorId = Integer.parseInt(req.params(":id"));
            JSONArray result = DatabaseManager.getItemsByDistributorId(distributorId);
            res.type("application/json");
            return (result == null) ? null : result.toString();
        });

        // Offerings by item ID
        get("/itemOfferings/:id", (req, res) -> {
            int itemId = Integer.parseInt(req.params(":id"));
            JSONArray result = DatabaseManager.getItemOfferingsById(itemId);
            res.type("application/json");
            return (result == null) ? null : result.toString();
        });

        // Add a new item with items.id, items.name, inventory.id.
        post("/item/:id/:name/:other_id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            String name = req.params(":name");
            int other_id = Integer.parseInt(req.params(":other_id"));
            boolean success = DatabaseManager.insertItem(id, name, other_id);

            res.type("application/json");
            return "{\"inserted\":" + success + "}";
        });

        // Add a new item to your inventory. Bookmark. Change 4 spaces to 1 tab.
        post("/inventory/:id/:item/:stock/:capacity", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            int item = Integer.parseInt(req.params(":item"));
            int stock = Integer.parseInt(req.params(":stock"));
            int capacity = Integer.parseInt(req.params(":capacity"));

            boolean success = DatabaseManager.insertInventory(id, item, stock, capacity);

            res.type("application/json");
            return "{\"inserted\":" + success + "}";
        });

        // Modify an existing item in your inventory
        put("/inventory/:id/:item/:stock/:capacity", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            int item = Integer.parseInt(req.params(":item"));
            int stock = Integer.parseInt(req.params(":stock"));
            int capacity = Integer.parseInt(req.params(":capacity"));

            boolean success = DatabaseManager.updateInventory(id, item, stock, capacity);

            res.type("application/json");
            return "{\"updated\":" + success + "}";
        });

        // Add a distributor
        post("/distributors/:id/:name", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            String name = req.params(":name");

            boolean success = DatabaseManager.insertDistributor(id, name);

            res.type("application/json");
            return "{\"inserted\":" + success + "}";
        });


        // Add items to a distributor's catalog (including the cost)
        post("/distributor_prices/:id/:distributor/:item/:cost", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            int distributor = Integer.parseInt(req.params(":distributor"));
            int item = Integer.parseInt(req.params(":item"));
            double cost = Double.parseDouble(req.params(":cost"));

            boolean success = DatabaseManager.insertDistributorPrices(id, distributor, item, cost);

            res.type("application/json");
            return "{\"inserted\":" + success + "}";
        });

        // Modify the price of an item in a distributor's catalog
        put("/distributor_prices/:id/:cost", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            double cost = Double.parseDouble(req.params(":cost"));

            boolean success = DatabaseManager.updateDistributorPrices(id, cost);

            res.type("application/json");
            return "{\"updated\":" + success + "}";
        });


        // Get the cheapest price for restocking an item at a given quantity from all distributors
        get("/cheapest_price/:item/:quantity", (req, res) -> {
            int item = Integer.parseInt(req.params(":item"));
            int quantity = Integer.parseInt(req.params(":quantity"));

            JSONArray result = DatabaseManager.getCheapestPrice(item, quantity);

            res.type("application/json");
            return result != null ? result.toJSONString() : "{\"cheapest_price\": null}";
        });
        // Delete an existing item from your inventory
        delete("/inventory/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean success = DatabaseManager.deleteInventory(id);

            res.type("application/json");
            return "{\"deleted\":" + success + "}";
        });

        // Delete an existing distributor given their ID
        delete("/distributors/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean success = DatabaseManager.deleteDistributor(id);

            res.type("application/json");
            return "{\"deleted\":" + success + "}";
        });

    }
}
