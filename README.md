# newmall
a mall project using reactive architecture 

## Goals
1. real mall project for production
2. gain new architecture experience, not only code, but also the whole process of the project 


## Architecture 
* reactive architecture: using vert.x
* micro service，event driven among micro services
* domain driven design
* CQRS style

<img width="1364" alt="WeChat7f931e9da4d1440a2f19ba85757d02fb" src="https://user-images.githubusercontent.com/7393184/129631836-c53fb006-ec45-4d15-b864-3caf57b5fcfe.png">


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

