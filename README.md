# newmall
a mall project using reactive architecture 

Event driven achitect, Command Query Responsbility Segragation, Micro service, Domain Driven Development
EDA,CQRS,DDD

## Goals
1. real mall project for production
2. gain new architecture experience, not only code, but also the whole process of the project 


## Architecture 
* reactive architecture: using vert.x
* micro service，event driven among micro services
* domain driven design
* CQRS style

<img width="1534" alt="WeChat762268aa84b3eb4239004ac388562f8f" src="https://user-images.githubusercontent.com/7393184/130392510-5e1b80d4-1310-406f-b431-2d127575289f.png">



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

