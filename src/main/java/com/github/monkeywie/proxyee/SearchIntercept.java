package com.github.monkeywie.proxyee;


import java.nio.charset.Charset;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchIntercept extends FullResponseIntercept {

	protected static final Logger log = LoggerFactory.getLogger(SearchIntercept.class);

//	private static String UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";
//
//	@Override
//	public void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
//							  HttpProxyInterceptPipeline pipeline) throws Exception {
//		//替换UA，伪装成手机浏览器
//		httpRequest.headers().set(HttpHeaderNames.USER_AGENT, UA);
//		//转到下一个拦截器处理
//		pipeline.beforeRequest(clientChannel, httpRequest);
//	}

	 @Override
     public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {

		 log.info("uri===="+pipeline.getHttpRequest().uri());
         return pipeline.getHttpRequest().uri().contains("/aweme/v1/general/search/single")||pipeline.getHttpRequest().uri().contains("/aweme/v1/user/profile/other");

     }

     @Override
     public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
         //打印原始响应信息
    	 if(pipeline.getHttpRequest().uri().contains("/aweme/v1/general/search/single")) {
	         String content=httpResponse.content().toString(Charset.forName("UTF-8"));
			 log.info("content====="+content);
	         Vector<String> ret=HttpProxyServerApp.parseAndAdd4Search(content);
    	 } else if(pipeline.getHttpRequest().uri().contains("/aweme/v1/user/profile/other")) {
    		 String content=httpResponse.content().toString(Charset.forName("UTF-8"));
			 log.info("user content====="+content);
	         Object object = JSON.parse(content);
	         if (object instanceof JSONObject||object instanceof JSONArray) {
	        	 HttpProxyServerApp.parseAndAdd4User(content);
	         }

    	 }

     }

}
