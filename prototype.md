## Goals
* one user story:
  * user click buy button in product detail page,
  * show the price of the order: including deduction,shipping fee,user can adjust deduction and pay method
  * user submit the order: deduction, decrease stock in shelf and warehouse
  * user pay the order: generating brokerage 
  
## Deploy: 
* web server: Vert.X, SpringBoot 
* msg queue: Kafka or RabbitMQ
* microservices
  * order
  * shelf: price related
  * stock: 
  * user: brokerage/coupon deduction,brokerage generation   

## Detail
### webserver
* convert http request to a command/query message, send to message queue.
* subsribe response message from message queue，convert to http response and send back 

### Rest API
POST /order
general data from header such as token,


### Fundamental layer: 
  * ServiceFramework: convert message to service method invocation
  * ServiceFramework: other aspect support: security:current user & permission (Spring security?),log,transaction?
  * Infrustructure: 


//TODO a architect diagram
Fundamental -> Service -> Domain 

### Event Storming
<img width="1536" alt="WeChat50900ac91619bd26a41162508596ab51" src="https://user-images.githubusercontent.com/7393184/130709982-c333ba3f-a068-45c6-af70-ada427810801.png">

### Security Service
* Authentication & Authroization,fire UserLoginEvent

### OrderService
* fire OrderCreatedEvent

### ShelfService
* onOrderCreatedEvent: fill shelf price to order,and fire OrderPricedEvent  

### UserService
* onOrderCreatedEvent: fill balance and brokerage that can be deducted to ther order,and fire OrderBalanceDeductedEvent


