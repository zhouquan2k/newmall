package com.atusoft.newmall;

import com.atusoft.infrastructure.RestApi;
import com.atusoft.infrastructure.RestApi.ApiEntry;
import com.atusoft.infrastructure.RestApi.Method;


//TODO refactor to @Annotation on service's command?
public class MallApi {

	static public RestApi apiFactory() {
		return new RestApi(new ApiEntry[] {
			new ApiEntry(Method.POST,"/order/preview","Order.PreviewOrder"),
			new ApiEntry(Method.POST,"/order/:orderId/submit","Order.SubmitOrder"),
			new ApiEntry(Method.POST,"/order/cancel","Order.CancelOrder"),
			
			new ApiEntry(Method.POST,"/security","Security.Login"),
			
		});
	}
}
