This file shows the ad-hoc methods I used to test code in the project.

I used Curl as in the examples below to do some ad-hoc tests after each route was completed.

And, I ran ad-hoc SQL queries using the terminal to check out the database.

For example, 'sqlite3 challenge.db' followed by '.schema' shows the schema. 

This helped me with selecting primary key values for CRUD operations and so on.

It is entirely possible to write unit tests that validate any function and how it returns data.

All of my Put and Post routes use an exception handler that returns true or false dependent upon success and the 

stack trace prints in Main which is useful to show the SQL error.
 
The -i flag in Curl shows the http codes for success or failure.

Use %27 to encode ' and %20 for space inside the URL.

Some ad-hoc tests I used are below:

curl -i http://localhost:4567/items

curl -i http://localhost:4567/item/100 returned empty. 

curl -i http://localhost:4567/itemOfferings/1

curl -X put http://localhost:4567/inventory/80/91/10/20 returned {"updated":true}

curl -X post http://localhost:4567/distributors/4/Good%20Candy returned {"inserted":true}

curl -X post http://localhost:4567/distributors/5/Charleston%20Chew returned {"inserted":true}

curl -X post http://localhost:4567/distributors/5/Good%20Bar correctly returned {"inserted":false} 

Because id 5 already exists in distributors. Main terminal shows A PRIMARY KEY constraint failed (UNIQUE constraint 

failed: distributors.id).

curl -X post http://localhost:4567/distributor_prices/28/1/1/0.81 returned {"inserted":true}

Tn enforce foreign keys in sqlite use: pragma foreign_keys = ON;

curl -X post http://localhost:4567/distributor_prices/29/100/1/0.81 correctly returned false because of a foreign

key constraint exception. The value 100 does not exist in distributors.id.

curl -X post http://localhost:4567/distributor_prices/29/1/22/0.81 correctly return false because of a primary key 

constraint with item.id of 22.

curl -i http://localhost:4567/cheapest_price/1/25 returned [{"cheapest_price":20.25}]
 
curl -i http://localhost:4567/cheapest_price/2/50 returned [{"cheapest_price":9.0}]

curl -X DELETE http://localhost:4567/inventory/80 returned {"deleted":true}

curl -X DELETE http://localhost:4567/distributors/5 returned {"deleted":true}
