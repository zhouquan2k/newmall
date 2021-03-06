package com.atusoft.webserver;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atusoft.framwork.ApiMessage;
import com.atusoft.framwork.ApiResponseMessage;
import com.atusoft.infrastructure.RestApi;
import com.atusoft.infrastructure.RestApi.ApiEntry;
import com.atusoft.messaging.MessageContext;
import com.atusoft.util.JsonUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


class DefaultEntry implements Handler<RoutingContext> {
	ApiEntry entry;
	MessageContext messageContext;
	JsonUtil jsonUtil;
	DefaultEntry(ApiEntry entry,MessageContext messageContext,JsonUtil jsonUtil) {
		this.entry=entry;
		this.messageContext=messageContext;
		this.jsonUtil=jsonUtil;
	}
	
	@Override
	public void handle(RoutingContext routingContext) {
		// TODO Auto-generated method stub
		 final String commandName=this.entry.getCommandName();
		 ApiMessage command=new ApiMessage(commandName);
		 String auth=routingContext.request().getHeader("Authorization");
		 if (auth!=null&&auth.startsWith("Bearer "))
			 command.setParam("_token",auth.substring(7));
		 
		 routingContext.pathParams().entrySet().stream().forEach(e->{
			 command.setParam(e.getKey(), e.getValue());
		 });
		 
		 command.setBody(routingContext.getBodyAsString());
		 messageContext.request("Command."+commandName, command, (message)-> {
			 
			 //TODO to json string
			 HttpServerResponse response = routingContext.response();
			 ApiResponseMessage r=(ApiResponseMessage)message.getContent();
			 response
			       .putHeader("Content-Type", "application/json")
			       .setStatusCode(r.getCode())
			       .end(""+((r.getBody()!=null)?r.getBody():""));
			   
		 });
	}
}

@Component
public class WebServerVertical extends AbstractVerticle  {

	@Autowired
	Vertx vertx;
	
	@PostConstruct
	public void init() {
		
        vertx.deployVerticle(this);
	}
	
	@Autowired
	MessageContext messageContext;
	
	@Autowired 
	RestApi restApi;
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Override
	public void start(Promise<Void> fut) {
	 // ????????????router?????????
	 Router router = Router.router(vertx);

	 // ???"/"??????????????????hello?????? - ?????????????????????
	 
	 
	 router.route("/").handler(routingContext -> {
	   HttpServerResponse response = routingContext.response();
	   response
	       .putHeader("Content-Type", "text/html")
	       .end("<h1>Hello from my first Vert.x 3 application</h1>");
	 });
	 
	 
	 for (ApiEntry entry: restApi.apiEntries()) {
		 switch (entry.getMethod()) {
		 case POST:
			 router.post(entry.getPath()).handler(BodyHandler.create())
			 	.handler(new DefaultEntry(entry,this.messageContext,jsonUtil));
			 break;
		 case PUT:
		 case DELETE:
			 //TODO method
			 break;
		 case GET:
			 router.get(entry.getPath())
			 	.handler(new DefaultEntry(entry,this.messageContext,jsonUtil));
			 break;
		 }	 
	 }
	  

	 // ??????HTTP???????????????"accept"?????????????????????????????????
	 vertx
	     .createHttpServer()
	     .requestHandler(router)
	     .listen(
	         // ????????????????????????????????????8080?????????
	         config().getInteger("http.port", 8080),
	         result -> {
	           if (result.succeeded()) {
	             fut.complete();
	           } else {
	             fut.fail(result.cause());
	           }
	         }
	     );
	}
}
