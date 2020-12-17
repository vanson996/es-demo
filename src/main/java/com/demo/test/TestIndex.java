package com.demo.test;

import com.demo.utils.ESClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;

import java.io.IOException;

/**
 * @author WYX
 * @date 2020/12/8
 */
public class TestIndex {
    String index = "person";
    String type = "man";

    RestHighLevelClient client = ESClient.getClient();

    @Test
    public void testConnect() {
        RestHighLevelClient client = ESClient.getClient();
    }


    @Test
    public void indexDelete() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest();
        request.indices(index);
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void indexExists() throws IOException {
        // 准备request对象
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        // 使用client 操作ES
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }


    /*
    # 创建索引，指定数据结构
        PUT /book
        {
            "settings":{
            "number_of_replicas": 1 ,
            "number_of_shards": 5
            },
            "mappings": {
                "novel":{
                    "properties":{
                        "name":{ "type":"text", "analyzer":"ik_max_word",   "index":true, "store":false  },
                    "author":{  "type":"keyword" },
                    "count":{ "type":"long"  },
                    "onSale":{ "type":"date",  "format":"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"  },
                    "desc":{  "type":"text", "analyzer":"ik_max_word" }
                    }
            }
        }
    */
    @Test
    public void createIndex() throws IOException {
        // 准备关于索引的settings
        Settings.Builder builder = Settings.builder().put("number_of_shards", 3).put("number_of_replicas", 1);
        // 准备关于索引的结构，mappings 参数
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("name")
                .field("type", "text")
                .endObject()
                .startObject("age")
                .field("type", "integer")
                .endObject()
                .startObject("birthday")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .endObject()
                .endObject();
        // 将setting和mapping封装到Request对象中
        CreateIndexRequest mapping = new CreateIndexRequest().index(index).settings(builder).mapping(type, mappings);
        // 通过Client对象连接ES，创建索引
        CreateIndexResponse response = client.indices().create(mapping, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }


}
