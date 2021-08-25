# newmall
一个用reactive架构做的电子商场

## 目标
1. 电子商城
2. gain new architecture experience, not only code, but also the whole process of the project 


## Architecture 
* reactive architecture: using vert.x
* micro service，event driven among micro services
* domain driven design
* CQRS style

<img width="1624" alt="WeChatae6293ae54a998364aab322d3541d916" src="https://user-images.githubusercontent.com/7393184/130106567-d27864a7-dbc6-42e1-ae26-cc96555d7274.png">


### Event Driven Architect:
#### Pros:
* TODO
* event can be persisted and replayed later when exception encounted
#### Cons:
TODO
* eventual transaction consistency 


* service discovery using pub/sub in message queue  

remove the necessity for the following:
* webserver/webclient in each microservice, instead of message client
* api gateway， instead of a message handler to do stuffs about security


### Plan
1. architecture prototype
2. ddd: event storming and [domain story](design.md)  
3. [Bounded contexts](design.md#bounded-contexts) 
