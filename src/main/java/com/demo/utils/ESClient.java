package com.demo.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author WYX
 * @date 2020/12/8
 */
public class ESClient {

    public static RestHighLevelClient getClient() {
        HttpHost host = new HttpHost("192.168.222.130", 9200);
        RestClientBuilder builder = RestClient.builder(host);
        // 创建RestHighLevelClient对象
        return new RestHighLevelClient(builder);
    }
}
