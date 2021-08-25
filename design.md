# User Stories

## P0 Stories
1. Product Definition (B) (Product)
3. WareHouse Definition (B) (Stock)
4. Procurement (B) (Stock)
5. On Shelf (B) (Shelf)
6. User Purchase (F) (Order)
7. Delivery: export shipping orders (B) (Delivery)

## P1+ Stories
1. Supplier Definition (B) (Stock)
2. Procurement：virtual (B) (Stock)
3. Procurement：proxy (B) (Stock)
4. Off Shelf (B) (Shelf)
5. Auto Shelf (P2) (B) (Shelf)
6. Procurement: real (B) (Shelf)
7. Delivery:return ticket (B) (Delivery)

## Other Stories
1. Campaigns (BF) (Shelf?)
2. Order: refund (BF) (Order)
3. Order: cancel (F) (Order)
4. Order: change address (F) (Order)
5. add product to cart (F) (Order)
6. pay for order -> brokerage calculation , user bill (F) (Order)
8. User: set promoter level and relations (B) (User)
9. User: owner change info (F) (User)
10. User: withdraw brokerage (User)
11. User: apply to be promoter (User) 


# Story Detail
## 0. Product Definition
mall operator create a new product

#### output entities
* Product
  * sku

## 1. Procurement
purchasing clerk procure products from suppier，and put them into warehouse

#### commands
Procurement(timestamp,warehouse, List[ProcurementItem], origin_ticket_photo, supplier)
  
ProcurementItem: sku, price, amount

#### actions
increase the stock

#### output entities
* Procurement (ActionTaken) [optional]
* Stock (Status) product,sku,count,warehouse

## 2. On Shelf
mall operator make the product on shelf so that the product cound be sold during the specifeid time segment.
mall operator also need to set the price of the product/sku. the price including all the price for different level of promoters, shipping fees(freight),

#### commands:
OnShelf(product,List[OnShelfItem],start_time,end_time)

OnShelfItem: sku,count,*Price*
Price: price for all levels, origin price to show,freight

#### constraints: 
* count must less than total stock of all warehouse
* if time is null, onshelf immediately

#### actions:
* update OnShelf strategy (take effect at queries for onshelf products)
* update OnShelfHistory [optional]

#### input entities
* Stock

#### output entities
* OnShelf (Status,strategy) : product(pk),state,start_time,end_time,*location*(normal|hot list|activity...),List[OnShelfItem]
  * OnShelfItem: sku,on_shelf_count,stock
* OnShelfHistory (ActionTaken) [optional]

## 3. User view products on shelf
#### queries
products on shelf at this moment

#### input entities
* OnShelf
* Product


## 4. User click buy to review order
may recalculated by adjust the pay options

#### commands:
Order ReviewOrder(List[PurchaseItem],...)

PurchaseItem: product,sku,count

#### constraints
* product is on shelf 
* onshelf stock is greater than 0
* sku stock is greater than 0

#### actions 
persist order(per user) to remeber what showed to user,especially prices
calculate prices,including: total price,deductions,shipping price,pay price

#### input entities
* Stock: product,sku,count,warehouse
* OnShelf: price info
* User: brokerage/integral/coupon for deduction

#### output entities
* Order: user,address,pay method(wechat/balance),deduction method(brokerage/integral),deliver method,List[PurchaseItem]
  - PurchaseItem: product,sku,count,warehouse,shelf 

#### published events
* OrderCreated
  * check stock & onshelf (optional)

#### returns
Calculated order

## 5. User confirm order 
user confirm the order
#### commands:
ConfirmOrder(orderId)

#### actions 
* found an warehouse which have enough stock,
* place an order, link the order and the warehouse
* decease the onshelf stock (OnShelfItem.stock)
* decrease the stock of the warehouse
* note: should do reverse operations when order has been canceled
* wait user 30 min to pay


#### published events
* OrderConfirmed
  * stock & onshelf decrease
  * user conpon,brokerage,integral dedeuction
  * reset cart

## 6. Delivery
mall operator export shipping orders for one warehouse.

#### Commands
ExportShippingOrders(warehouse,[carrier],isReExport*)
* can support: regenerate deliveries when isReExport is true [optional]


#### actions
* find paid orders in specfied time segment for one warehouse (and no Delivery already generated for these orders) 
* generate shipping orders(entity:Delivery) for orders, combine when nessesary (same address)

#### input entities
* Orders

#### output entities
* Delivery: warehouse,List[DeliveryItem],carrier,price,Destination
  * DeliveryItem: product/sku,count,order
  * Destination: address,name,phone


# Bounded Contexts
totally using Object Oriented Methodolodge
<img width="1306" alt="WeChatae29fbaa41b3968c5c736019fcceab8b" src="https://user-images.githubusercontent.com/7393184/129801677-e01e8fe1-9fc9-4445-a2a2-6488202252d9.png">


