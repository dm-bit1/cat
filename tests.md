This file shows the ad-hoc methods I used to test code in the project.

I used Curl as in the examples below to do some ad-hoc tests after each route was completed.

And, I ran ad-hoc SQL queries using the terminal to check out the database.

This helped me with selecting primary key values for CRUD operations and so on.

All my Post routes require the user to select an appropriate id.

Useful shell commands in SQLite:

'sqlite3 challenge.db' followed by '.schema' shows the schema. 

To enforce foreign keys in sqlite use: 'pragma foreign_keys = ON;'

To turn on JSON mode use '.mode json'. Use '.headers on' for column names to appear.

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

curl -X post http://localhost:4567/distributor_prices/29/100/1/0.81 correctly returned false because of a foreign

key constraint exception. The value 100 does not exist in distributors.id.

curl -X post http://localhost:4567/distributor_prices/29/1/22/0.81 correctly return false because of a primary key 

constraint with item.id of 22.

curl -i http://localhost:4567/cheapest_price/1/25 returned [{"cheapest_price":20.25}]
 
curl -i http://localhost:4567/cheapest_price/2/50 returned [{"cheapest_price":9.0}]

curl -X DELETE http://localhost:4567/inventory/80 returned {"deleted":true}

curl -X DELETE http://localhost:4567/distributors/5 returned {"deleted":true}

Fixed the bug with the add an item to the database piece. The ad-hoc tests used are below.

curl -X post http://localhost:4567/item/18/Sweet%20Apple returned {"inserted": true}

curl -X post http://localhost:4567/item/18/Sweet%20Cherry correctly return {"inserted": false}. 

And Main showed the SQL error due to a duplicate key.

curl -X post http://localhost:4567/item/500/Marzepan returned {"inserted":true}

And, a query of 'select * from items;' showed {"id":500,"name":"Marzepan"} in the database.

curl -i -X post http://localhost:4567/item/x/Marzepan This should give an http error because x is not an integer.

Response:
HTTP/1.1 500 Server Error
Date: Fri, 18 Jul 2025 20:07:14 GMT
Content-Type: text/html;charset=utf-8
Transfer-Encoding: chunked
Server: Jetty(9.3.6.v20151106)

$ curl -i -X post http://localhost:4567/item/539/Ginger%20Chocolate

Response below:
HTTP/1.1 200 OK
Date: Fri, 18 Jul 2025 20:13:53 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.3.6.v20151106)

{"inserted":true}

And {"id":539,"name":"Ginger Chocolate"} existed in the database after a query.

curl -i -X post http://localhost:4567/item/808/11%20Vanilla

Response below:
HTTP/1.1 200 OK
Date: Fri, 18 Jul 2025 20:16:27 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.3.6.v20151106)

{"inserted":true}

{"id":808,"name":"11 Vanilla"} existed in the database after a query.

curl -i -X post http://localhost:4567/item/400/$$%20Dollar%20Candy

Response:
HTTP/1.1 200 OK
Date: Fri, 18 Jul 2025 20:18:38 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.3.6.v20151106)

{"inserted":true}

{"id":400,"name":"145907 Dollar Candy"} existed in the database after a query. The $ was converted to some ASCII.

