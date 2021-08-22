## On Shelf
mall operator make the product on shelf so that the product cound be sold during the specifeid time segment.
mall operator also need to set the price of the product/sku. the price including all the price for different level of promoters, shipping fees(freight),

#### commands:
OnShelf(product,List[OnShelfItem],start_time,end_time)

OnShelfItem: sku,count,*Price*
Price: price for all levels, origin price to show,freight

#### constraints: 
* ~~count must less than total stock of all warehouse,~~  needn't check, display stock=min(shelf stock,real stock)
* if time is null, onshelf immediately

#### actions:
* update OnShelf strategy (take effect at queries for onshelf products)
* update OnShelfHistory [optional]

#### input entities
* Stock

#### output entities
* OnShelf (Status,strategy) : product(pk),state,start_time,end_time,*location*(normal|host list|activity...),List[OnShelfItem]
  * OnShelfItem: sku,on_shelf_count,stock
* OnShelfHistory (ActionTaken) [optional]


## 4. User purchase 
user purchase product by placing an order,
#### commands:
CreateOrder(List[PurchaseItem],...)

PurchaseItem: product,sku,count,shelf

#### constraints
product is on shelf 
onshelf stock is greater than 0
sku stock is greater than 0

#### actions 
* found an warehouse which have enough stock,
* place an order, link the order and the warehouse
* decease the onshelf stock (OnShelfItem.stock)
* decrease the stock of the warehouse
* note: should do reverse operations when order has been canceled

#### input entities
* Stock: product,sku,count,warehouse
* OnShelf:
#### output entities
* Order: ... List[PurchaseItem]
  - PurchaseItem: product,sku,count,warehouse,shelf,

#### Detail
* create order,which state is created 
* publish event Order.OrderCreated(Order)

event handler:
* Shelf.onStockConfirmed(orderId) & Stock.onStockConfirmed(orderId,List[PurchaseItem]): update order,set state to Confirmed,update purchase items with warehouse
* Shelf.onStockNegative(orderId): cancel order due to lack of shelf stock 
* Stock.onStockNegative(orderId): cancel order due to lack of stock

<img width="480" alt="WeChatcb5ce32f3b542c58e66b03335c81ac47" src="https://user-images.githubusercontent.com/7393184/129824221-3019334e-885b-47ca-b21d-89a0a308e612.png">


Order.cancel(reason): set state to canceled, publish Order.OrderCanceledEvent

may cause many canceled order when out of stock, frontend should disable purchase button when displayed in client ui.


