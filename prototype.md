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

### Rest API
POST /order
general data from header such as token,

### Order Service
* Fundamental layer: 
  * ServiceFramework: convert message to service method invocation
  * ServiceFramework: other aspect support: security:current user & permission (Spring security?),log,transaction?
  * Infrustructure: 
* service layer: OrderService, 
* domain layer: Order

### Security Service
* Authentication & Authroization
* 

//TODO a architect diagram
Fundamental -> Service -> Domain 

