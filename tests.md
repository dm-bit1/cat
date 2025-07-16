This file shows a few methods I used to test code in the project.

I used Curl as in the example below to do some ad-hoc tests after each route was completed.
 
The -i flag also shows the http code of 200 for success or 400 for database errors after each Curl query. 

curl -i http://localhost:4567/items

curl -i http://localhost:4567/item/100 returned empty. No message is given.

curl -i http://localhost:4567/itemOfferings/1

Test case: curl -i http://localhost:4567/itemOfferings/xxx

For the put route:

curl -X put http://localhost:4567/item/id/name/other_id 

To add a space in the name, use %20. The UTRL to database mappings are id:items.id, name:items.name,other_id:inventory.id.

Returns back:
HTTP/1.1 500 Server Error
Date: Wed, 16 Jul 2025 19:44:26 GMT
Content-Type: text/html;charset=utf-8
Transfer-Encoding: chunked
Server: Jetty(9.3.6.v20151106)


I also ran ad-hoc SQL queries in a terminal to check the database out.

For example, 'sqlite3 challenge.db' followed by '.schema' shows the schema.