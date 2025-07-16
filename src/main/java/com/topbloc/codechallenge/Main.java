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

        // An item by ID, including the item name, ID, amount in stock, total capacity, and distributor information
        get("/item/:id", (req, res) -> {
                JSONArray result = DatabaseManager.getItemById(req.params(":id"));
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Item not found or database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // All distributors, including the distributor name, ID, and contact information
        get("/distributors", (req, res) -> {
                JSONArray result = DatabaseManager.getAllDistributors();
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // A distributor by ID, including the distributor name, ID, contact information, and all items they distribute
        get("/distributor/:id", (req, res) -> {
                JSONArray result = DatabaseManager.getItemsByDistributorId(req.params(":id"));
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // All item offerings, including the item name, ID, amount in stock, total capacity, and distributor information
        get("/itemOfferings/:id", (req, res) -> {
                JSONArray result = DatabaseManager.getItemOfferingsById(req.params(":id"));
                if (result == null) {
                        res.status(400);
                        return new JSONObject().put("error", "Database error").toString();
                }
                res.type("application/json");
                return result.toString();
        });

        // POST/PUT/DELETE routes
        

    }
}