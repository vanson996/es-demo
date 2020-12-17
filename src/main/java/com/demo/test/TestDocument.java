package com.demo.test;

import com.demo.entity.Person;
import com.demo.utils.ESClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WYX
 * @date 2020/12/8
 */
public class TestDocument {

    ObjectMapper mapper = new ObjectMapper();
    RestHighLevelClient client = ESClient.getClient();
    String index = "person";
    String type = "man";


    @Test
    public void creatDoc() throws IOException {
        // 准备一个json数据
        Person person = new Person(1, "张三", 20, new Date());
        // 准备一个request对象，手动指定id
        String json = mapper.writeValueAsString(person);
        System.out.println(json);
        IndexRequest request = new IndexRequest(index, type, person.getId().toString());
        request.source(json, XContentType.JSON);
        // 通过client对象添加
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }

    /**
     * 采用doc的方式更新文档
     */
    @Test
    public void updateDoc() throws IOException {
        String docId = "1";
        // 创建一个map 指定需要修改的内容
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "小张");
        // 创建一个request对象
        UpdateRequest request = new UpdateRequest(index, type, docId);
        request.doc(map);
        // 通过client对象执行
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }

    @Test
    public void deleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest(index, type, "3");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    /**
     * 批量添加文档
     */
    @Test
    public void bulkCreateDoc() throws IOException {
        Person person1 = new Person(1, "张三", 28, new Date());
        Person person2 = new Person(2, "张四", 19, new Date());
        Person person3 = new Person(3, "张五", 23, new Date());

        String p1 = mapper.writeValueAsString(person1);
        String p2 = mapper.writeValueAsString(person2);
        String p3 = mapper.writeValueAsString(person3);
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest(index, type, person1.getId().toString()).source(p1, XContentType.JSON));
        request.add(new IndexRequest(index, type, person2.getId().toString()).source(p2, XContentType.JSON));
        request.add(new IndexRequest(index, type, person3.getId().toString()).source(p3, XContentType.JSON));

        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(responses);
    }

    /**
     * 批删除文档
     */
    @Test
    public void bulkDeleteDoc() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest(index, type, "1"));
        request.add(new DeleteRequest(index, type, "2"));
        request.add(new DeleteRequest(index, type, "3"));

        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(responses.hasFailures());
    }
}
