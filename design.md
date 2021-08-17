# User Stories

## P0 Stories
1. Product Definition
2. WareHouse Definition
3. Procurement
4. On Shelf
5. User Purchase
6. Delivery

## P1+ Stories
1. Supplier Definition
2. Procurement：virtual
3. Procurement：proxy
4. Off Shelf
5. Auto Shelf (P2)
6. Procurement: real
7. Delivery:return ticket

# Story Detail
## 1. Procurement
purchasing clerk procure products from suppier，and put them into warehouse

#### commands
Procurement(timestamp,warehouse, List[ProcurementItem], origin_ticket_photo, supplier)
  
ProcurementItem: sku, price, amount

#### actions
increase the stock

#### related entities
* Procurement (ActionTaken) [optional]
* Stock (Status) product,sku,count,warehouse

## 2. On Shelf
mall operator make the product on shelf so that the product cound be sold during the specifeid time segment.

#### commands:
OnShelf(product,List[OnShelfItem],start_time,end_time)

OnShelfItem: sku,count

#### constraints: 
* count must less than total stock of all warehouse
* if time is null, onshelf immediately

#### actions:
* update OnShelf strategy (take effect at queries for onshelf products)
* update OnShelfHistory [optional]

#### related entities
* OnShelf (Status,strategy) : product(pk),state,start_time,end_time,List[OnShelfItem]
* OnShelfItem: sku,on_shelf_count,stock
* OnShelfHistory (ActionTaken) [optional]

## 3. User view products on shelf
#### queries
products on shelf at this moment

#### related entities
* OnShelf

## 4. User purchase 
user purchase product by placing an order,
#### commands:
CreateOrder(List[PurchaseItem],...)

PurchaseItem: product,sku,count

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

#### related entities
* Order: ... List[PurchaseItem]
* PurchaseItem: product,sku,count,warehouse,
* Stock: product,sku,count,warehouse

## 5. Delivery
mall operator export shipping orders for one warehouse.

#### commands
ExportShippingOrders(warehouse,[carrier])

#### actions
* find paid orders in specfied time segment for one warehouse
* generate shipping orders for orders, combine when nessesary (same address)

# Bounded Context







