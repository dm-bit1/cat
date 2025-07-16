package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;

import static spark.Spark.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.connect();
        // Don't change this - required for GET and POST requests with the header 'content-type'
        options("/*",
                (req, res) -> {
                    res.header("Access-Control-Allow-Headers", "content-type");
                    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    return "OK";
                });

        // Don't change - if required you can reset your database by hitting this endpoint at localhost:4567/reset
        get("/reset", (req, res) -> {
            DatabaseManager.resetDatabase();
            return "OK";
        });

        //TODO: Add your routes here. a couple of examples are below
        // All items in your inventory, including the item name, ID, amount in stock, and total capacity.
        get("/items", (req, res) -> DatabaseManager.getItems());


        get("/version", (req, res) -> "TopBloc Code Challenge v1.0");

        // The next routes in the requirements are below.
        // All items in your inventory that are currently out of stock, including the item name, ID, amount in stock, and total capacity
        get("/outOfStockItems", (req, res) -> {
                JSONArray result = DatabaseManager.getOutOfStockItems();
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // All items in your inventory that are overstocked, including the item name, ID, amount in stock, and total capacity
        get("/overstockedItems", (req, res) -> {
                JSONArray result = DatabaseManager.getOverstockedItems();
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // All items in your inventory that are low on stock, including the item name, ID, amount in stock, and total capacity
        get("/lowStockItems", (req, res) -> {
                JSONArray result = DatabaseManager.getLowStockItems();
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // This route gets an  item by ID, including the item name, ID, amount in stock, total capacity, and distributor information
        get("/item/:id", (req, res) -> {
                int itemId = Integer.parseInt(req.params(":id")); // throws NumberFormatException for invalid IDs
                JSONArray result = DatabaseManager.getItemById(itemId);
                res.type("application/json");
                if (result == null) {
                        throw new RuntimeException("Item not found or database error");
                }
                return result.toString();
        });




        // All distributors, including the distributor name, ID, contact information, and all items they distribute
        get("/distributors", (req, res) -> {
                JSONArray result = DatabaseManager.getAllDistributors();
                res.type("application/json");
                return (result == null) ? null : result.toString();
        });

        // A dynamic route that, given a distributors ID, returns the items distributed by a given distributor, including the item name, ID, and cost
        get("/distributor/:id", (req, res) -> {
                int distributorId = Integer.parseInt(req.params(":id"));
                JSONArray result = DatabaseManager.getItemsByDistributorId(distributorId);
                res.type("application/json");
                return (result == null) ? null : result.toString();
        });

        //A dynamic route that, given an item ID, returns all offerings from all distributors for that item, including the distributor name, ID, and cost
        get("/itemOfferings/:id", (req, res) -> {
		int itemId = Integer.parseInt(req.params(":id"));
		JSONArray result = DatabaseManager.getItemOfferingsById(itemId);
		res.type("application/json");
		return (result == null) ? null : result.toString();
        });

        // POST/PUT/DELETE routes are below.
        // Add a new item to the database. It works directly in the url with item/id/name without JSON parsing.
      	put("/item/:id/:name/:other_id", (req, res) -> {
                int id = Integer.parseInt(req.params(":id"));
		String name = req.params(":name");
		int other_id = Integer.parseInt(req.params(":other_id"));
		boolean success = DatabaseManager.insertItem(id, name, other_id);

		res.type("application/json");
		return "{\"inserted\":" + success + "}";
        	});
        } //main
    // next route

} // class