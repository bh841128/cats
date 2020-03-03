package com.github.monkeywie.proxyee;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.config.RequestConfig.Builder;

import java.io.IOException;
import java.util.Set;

public class Task4User implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Task4User.class);

	private RestHighLevelClient restHighClient;

	private static RestClientBuilder builder;


	public Task4User() {
		
		builder = RestClient.builder(new HttpHost("47.254.46.184", 9200, "http"));
		//builder=RestClient.builder(new HttpHost("112.74.105.107", 9200, "http"));
		builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

			@Override
			public Builder customizeRequestConfig(Builder requestConfigBuilder) {

			    requestConfigBuilder.setConnectTimeout(5000);
                requestConfigBuilder.setSocketTimeout(15000);
                requestConfigBuilder.setConnectionRequestTimeout(15000);
                return requestConfigBuilder;
				
			}
		});

		this.restHighClient = new RestHighLevelClient(builder);
		
	}

   	public void run(){

	    String content = HttpProxyServerApp.queue4User.poll();
        if(content == null || content.isEmpty()){
            return ;
        }
       
        JSONObject outJson = JSONObject.parseObject(content);
        Set<String> jsonSet = outJson.keySet();
        if (!jsonSet.contains("user") ) {
            log.error("=====Task4User err! no user!! content:"+content);
       		return ;
        }

        if(!outJson.getJSONObject("user").containsKey("sec_uid")) {
            log.error("=====Task4User err! no user.sec_uid!! content:"+content);
       		return ;
        }
        String id = outJson.getJSONObject("user").getString("sec_uid");
       
        IndexRequest request = new IndexRequest("tiktok");
        request.timeout(TimeValue.timeValueSeconds(15));
        request.id(id);	//ID也可使用内部自动生成的 不过希望和数据库统一唯一业务ID
        request.source(content, XContentType.JSON);
        try {
            IndexResponse response = restHighClient.index(request, RequestOptions.DEFAULT);
            log.info(response.getResult().toString());
        } catch (IOException e) {
		    e.printStackTrace();
        }
   }
}
