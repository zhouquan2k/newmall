## Goals
* one user story: create order in order service, and Stock service confirm the stock

## Deploy: 
* web server with vertx
* msg queue
* two microservice: Order and Stock

## Detail
### webserver
* convert http request to a command/query message, send to message queue.
* subsribe response message from message queue，convert to http response and send back 
