This file shows a few methods I used to test code in the project.

I used Curl as in the example below to do some ad-hoc tests after each route was completed.
 
The -i flag also shows the http code of 200 for success after each Curl query. 

Curl should show http 400 if a database error happens.

curl -i http://localhost:4567/items

curl -i http://localhost:4567/itemOfferings/1

Note on input validation for routes. Because of time concerns, I did not validate inputs to

make sure the types are correct. For example, it expects integer for item id and the user adds 

string instead to the route URL.

I also ran ad-hoc SQL queries in a terminal to check the database out.

For example, 'sqlite3 challenge.db' followed by '.schema' shows the schema.

sqlite> .schema
CREATE TABLE items (
id integer PRIMARY KEY,
name text NOT NULL UNIQUE
);
CREATE TABLE inventory (
id integer PRIMARY KEY,
item integer NOT NULL references items(id),
stock integer NOT NULL,
capacity integer NOT NULL
);
CREATE TABLE distributors (
id integer PRIMARY KEY,
name text NOT NULL UNIQUE
);
CREATE TABLE distributor_prices (
id integer PRIMARY KEY,
distributor integer NOT NULL references distributors(id),
item integer NOT NULL references items(id),
cost float NOT NULL
);
