package com.atusoft.newmall;

import com.atusoft.infrastructure.RestApi;
import com.atusoft.infrastructure.RestApi.ApiEntry;
import com.atusoft.infrastructure.RestApi.Method;


//TODO refactor to @Annotation on service's command?
public class MallApi {

	static public RestApi apiFactory() {
		return new RestApi(new ApiEntry[] {
			new ApiEntry(Method.POST,"/order/preview","Order.previewOrder"),
			new ApiEntry(Method.POST,"/order/:orderId/submit","Order.submitOrder"),
			new ApiEntry(Method.POST,"/order/cancel","Order.cancelOrder"),
			
			new ApiEntry(Method.POST,"/security","Security.Login"),
			
			new ApiEntry(Method.POST,"/shelf","Shelf.SaveShelf"),
			
			new ApiEntry(Method.POST,"/user","User.SaveUser"),
			new ApiEntry(Method.POST,"/user/account","User.SaveAccount"),
			new ApiEntry(Method.POST,"/security/login","User.Login")
			
			
		});
	}
}
