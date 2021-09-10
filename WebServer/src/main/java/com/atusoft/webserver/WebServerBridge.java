package com.atusoft.webserver;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atusoft.framwork.ApiMessage;
import com.atusoft.framwork.ApiResponseMessage;
import com.atusoft.infrastructure.RestApi;
import com.atusoft.messaging.MessageContext;
import com.atusoft.newmall.dto.order.DeductionOptions;
import com.atusoft.newmall.dto.order.DeductionOptions.Deduction;
import com.atusoft.newmall.dto.order.OrderDTO.PayMethod;
import com.atusoft.newmall.dto.order.PurchaseItem;
import com.atusoft.util.JsonUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.httpproxy.HttpProxy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import io.vertx.ext.web.handler.BodyHandler;


/*
@Component
class MyHandler implements Handler<HttpServerRequest> {

	String host="localhost";
	int port=8001;
	
	@Autowired
	Vertx vertx;
	
	HttpProxy proxy;
	
	Router router = Router.router(vertx);
	
	@PostConstruct
	public void init() {
		// TODO Auto-generated method stub
		HttpClient proxyClient = vertx.createHttpClient();
		proxy = HttpProxy.reverseProxy(proxyClient);
		proxy.origin(port, host);
	}
	
	@Override
	public void handle(HttpServerRequest event) {
		
		//TODO
		this.router.handle(event);
		proxy.handle(event);
	}
	
}
*/

class Context {
	RoutingContext rctx;
	Map<String,Object> data;
	Context(RoutingContext rctx){
		this.rctx=rctx;
		//String str=rctx.getBodyAsString();
		data=rctx.getBodyAsJson()!=null?rctx.getBodyAsJson().getMap():null;//jsonUtil.fromJson(str, Map.class);
		if (data==null) data=new HashMap<String,Object>();
	}
	String getString(String name) {
		return (String)this.data.get(name);
	}
	Integer getInt(String name) {
		return (Integer)this.data.get(name);
	}
	/*
	Boolean getBoolean(String name) {
		return (Boolean)this.data.get(name);
	}
	*/
	RoutingContext getRoutingContext() {
		return this.rctx;
	}
}

@Data
class EmptyObject {
	String _userless="";
}

@FunctionalInterface
interface WebHandler {
	Future<Response> handle(Context ctx);
}


@Slf4j
class MyHandler implements Handler<RoutingContext>{

	WebHandler handler;
	JsonUtil jsonUtil;
	MyHandler(WebHandler handler,JsonUtil jsonUtil){
		this.handler=handler;
		this.jsonUtil=jsonUtil;
	}
	
	@Override
	public void handle(RoutingContext event) {
		//Map<String,Object> data=event.getBodyAsJson().getMap();
		log.debug("// handling http request:"+event.normalizedPath());
		Context ctx=new Context(event);
		Future<Response> f=this.handler.handle(ctx);
		f.onSuccess(ret->{
			 HttpServerResponse response = event.response();
			 response
			       .putHeader("Content-Type", "application/json")
			       .setStatusCode(ret.status)
			       .end(this.jsonUtil.toJson(ret));
		}).onFailure(e->{
			e.printStackTrace();
		}).onComplete(r->{
			log.debug("\\\\ handled http request:"+event.normalizedPath());
		});
		
	}
	
	
}

class Response {
	int status=200;
	boolean success=true;
	LocalTime time=LocalTime.now();
	Map<String,Object> data;
	static Response create(ApiResponseMessage msg,JsonUtil jsonUtil) {
		Response ret=new Response();
		@SuppressWarnings("unchecked")
		Map<String,Object> data=jsonUtil.fromJson(msg.getBody(), Map.class);
		if (msg.getCode()==200) {
			ret.data=data;
		}
		else {
			ret.status=msg.getCode();
			ret.success=false;
			ret.data=data;
		}
		return ret;
	}
}

@Component
public class WebServerBridge  extends AbstractVerticle  {

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
	
	//@Autowired 
	//MyHandler myHandler;
	
	String host="www.progartisan.com";
	int port=8070;
	
	@Override
	public void start(Promise<Void> fut) {
		 // 创建一个router对象。
		Router router = Router.router(vertx);
		 //client = WebClient.create(vertx);
		
		HttpClient proxyClient = vertx.createHttpClient();
		HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
		proxy.origin(port, host);
		
		BodyHandler bodyHandler=BodyHandler.create();
	
		 // 将"/"绑定到我们的hello消息 - 从而保持兼容性
		 
		 
		 router.route("/").handler(routingContext -> {
		   HttpServerResponse response = routingContext.response();
		   response
		       .putHeader("Content-Type", "text/html")
		       .end("<h1>Hello from my first Vert.x 3 application</h1>");
		 });
	
		 
		 router.post("/api/cart/add").handler(bodyHandler).handler(new MyHandler(this::addCart,jsonUtil));
		 router.post("/api/order/confirm").handler(bodyHandler).handler(new MyHandler(this::confirmOrder,jsonUtil));
		 router.post("/api/order/computed/:cartId").handler(bodyHandler).handler(new MyHandler(this::computeOrder,jsonUtil));
		 router.post("/api/order/create/:orderId").handler(bodyHandler).handler(new MyHandler(this::createOrder,jsonUtil));
		 /*
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
		 */
		 
		 router.route().handler(rctx->{
			 proxy.handle(rctx.request());
		 });
		  
		 
		 
	
		 // 创建HTTP服务器并将"accept"方法传递给请求处理器。
		 vertx
		     .createHttpServer()
		     .requestHandler(router)
		     .listen(
		         // 从配置中获取端口，默认是8080端口。
		         config().getInteger("http.port", 8888),
		         result -> {
		           if (result.succeeded()) {
		             fut.complete();
		           } else {
		             fut.fail(result.cause());
		           }
		         }
		     );
	}
	
	Future<Response> confirmOrder(Context ctx) {
		String cartId=ctx.getString("cartId");
		/*
		ApiMessage command=new ApiMessage("Order.previewOrder");
		String cartId=ctx.getString("cartId");
		command.setParam("cartId",cartId );
		return messageContext.request("Command."+command.getCommandName(), command).map((message)-> {
			 ApiResponseMessage r=(ApiResponseMessage)message.getContent();
			 Response resp=Response.create(r,jsonUtil);
			 if (resp.success) {
				 Map<String,Object> origin=resp.data;
				 Map<String,Object> newData=new HashMap<String,Object>();
				 newData.put("orderKey",origin.get("orderId"));
				 newData.put("deduction",false);
				 newData.put("priceGroup",new EmptyObject());
				 newData.put("result",Map.of("payPrice",origin.getOrDefault("payPrice",0),"deductionPrice",origin.getOrDefault("deductionPrice",0)));
				 resp.data=newData;
			 }
			 return resp;   
		 });
		 */
		// do nothing
		Response resp=new Response();
		resp.data=Map.of("orderKey",cartId,"deduction",false,"priceGroup",new EmptyObject());
		return Future.succeededFuture(resp);
		
	}
	
	Future<Response> computeOrder(Context ctx){
		// preview order, return orderId
		ApiMessage command=new ApiMessage("Order.previewOrder");
		command.setParam("cartId", ctx.rctx.pathParam("cartId"));
		DeductionOptions options=new DeductionOptions();
		options.setBrokerageDeduction(new Deduction(ctx.getInt("useBrokerage")==1));
		command.setBody(jsonUtil.toJson(options));
		return messageContext.request("Command."+command.getCommandName(), command).map((message)-> {
			 ApiResponseMessage r=(ApiResponseMessage)message.getContent();
			 Response resp=Response.create(r,jsonUtil);
			 if (resp.success) {
				 Map<String,Object> origin=resp.data;
				 Map<String,Object> newData=new HashMap<String,Object>();
				 newData.putAll(Map.of("orderId",origin.get("orderId"),"result",Map.of(
						 "totalPrice",origin.getOrDefault("totalPrice", 0),
						 "payPrice",origin.getOrDefault("payPrice",0),
						 "deductionPrice",origin.getOrDefault("deductionPrice",0))));
				 resp.data=newData;
			 }
			 return resp;   
		 });
	}
	
	Future<Response> createOrder(Context ctx){
		// preview order, return orderId
		ApiMessage command=new ApiMessage("Order.submitOrder");
		PayMethod payMethod=PayMethod.WeChatPay;
		switch (ctx.getString("payType")) {
		case "yue": payMethod=PayMethod.Balance;break;
		case "weixin": payMethod=PayMethod.WeChatPay;break;
		}
		command.setParam("orderId", ctx.rctx.pathParam("orderId"));
		command.setParam("payMethod",payMethod.toString());
		return messageContext.request("Command."+command.getCommandName(), command).map((message)-> {
			 ApiResponseMessage r=(ApiResponseMessage)message.getContent();
			 Response resp=Response.create(r,jsonUtil);
			 if (resp.success) {
				 Map<String,Object> origin=resp.data;
				 Map<String,Object> newData=new HashMap<String,Object>();
				 if (origin.get("status").equals("Paid"))
					 newData.put("status","SUCCESS");
				 else if (origin.get("payMethod").equals("WeChatPay"))
					 newData.put("status","WECHAT_PAY");
				 resp.data=newData;
			 }
			 return resp;   
		 });
	}
	
	/*
	Future<Response> getOrder(Context ctx) {
		ApiMessage command=new ApiMessage("Order.getOrder");
		command.setParam("orderId", ctx.rctx.pathParam("orderId"));
		return messageContext.request("Command."+command.getCommandName(), command).map((message)-> {
			 ApiResponseMessage r=(ApiResponseMessage)message.getContent();
			 Response resp=Response.create(r,jsonUtil);
			 if (resp.success) {
				 Map<String,Object> origin=resp.data;
				 Map<String,Object> newData=new HashMap<String,Object>();
				 newData.put("result",Map.of("payPrice",origin.getOrDefault("payPrice",0),"deductionPrice",origin.getOrDefault("deductionPrice",0)));
				 resp.data=newData;
			 }
			 return resp;   
		 });
	}
	*/
	
	Future<Response> addCart(Context ctx) {
		
		ApiMessage command=new ApiMessage("Cart.singlePurchase");
		PurchaseItem item=PurchaseItem.builder().count(ctx.getInt("cartNum"))
				.skuId(ctx.getString("uniqueId"))
				.productId(ctx.getString("productId"))
				.shelfId("shelf_1") //TODO
				.build();
		 
		 command.setBody(this.jsonUtil.toJson(item));
		 return messageContext.request("Command."+command.getCommandName(), command).map((message)-> {
			 ApiResponseMessage r=(ApiResponseMessage)message.getContent();
			 Response resp=Response.create(r,jsonUtil);
			 return resp;   
		 });
		
	}
	
}
