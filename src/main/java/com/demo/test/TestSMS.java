package com.demo.test;

import com.demo.entity.MsgInfo;
import com.demo.utils.ESClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * @author WYX
 * @date 2020/12/8
 */
public class TestSMS {

    ObjectMapper objectMapper = new ObjectMapper();
    RestHighLevelClient client = ESClient.getClient();
    String index = "sms_logs_index";
    String type = "sms_logs_type";

    /**
     * 创建sms 的index
     */
    @Test
    public void createSMSIndex() throws IOException {

        Settings.Builder settings = Settings.builder();
        settings.put("number_of_shards", 3);
        settings.put("number_of_replicas", 1);

        XContentBuilder mapping = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("createDate")
                .field("type", "date")
                .field("format", "yyyy-MM-dd HH:mm:ss")
                .endObject()
                .startObject("sendDate")
                .field("type", "date")
                .field("format", "yyyy-MM-dd HH:mm:ss")
                .endObject()
                .startObject("longCode")
                .field("type", "keyword")
                .endObject()
                .startObject("mobile")
                .field("type", "keyword")
                .endObject()
                .startObject("corpName")
                .field("type", "keyword")
                .endObject()
                .startObject("smsContent")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("state")
                .field("type", "integer")
                .endObject()
                .startObject("operatorId")
                .field("type", "integer")
                .endObject()
                .startObject("province")
                .field("type", "keyword")
                .endObject()
                .startObject("ipAddr")
                .field("type", "keyword")
                .endObject()
                .startObject("replyTotal")
                .field("type", "integer")
                .endObject()
                .startObject("fee")
                .field("type", "integer")
                .endObject()
                .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest(index).settings(settings).mapping(type, mapping);


        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }

    /**
     * 批量创建原始数据
     */
    @Test
    public void creatDoc() throws IOException {


        MsgInfo info1 = new MsgInfo("1", new Date(), new Date(), "100086", "15512345601", "迈异信息科技有限公司", "今天代码写完了，我要早点下班", 1, 1, "湖北", "127.0.0.1", 500, 12);
        MsgInfo info2 = new MsgInfo("2", new Date(), new Date(), "10003215", "15512345601", "武汉新烽光电股份有限公司", "服务差异化！（图二为迈异的部署）为运维部同学点赞！", 1, 1, "湖北", "127.0.0.1", 500, 14);
        MsgInfo info3 = new MsgInfo("3", new Date(), new Date(), "10000128", "15512345601", "湖北省楚天云有限公司", "陪同世纪互联华南区高总考察未来城机房、武钢机房。", 1, 1, "湖北", "127.0.0.1", 500, 53);
        MsgInfo info4 = new MsgInfo("4", new Date(), new Date(), "10008216", "15512345601", "IBM（国际商业机器公司）", "主要做安全产品及解决方案，国内商用密码产品市场占有率最高。", 1, 1, "湖北", "127.0.0.1", 500, 23);
        MsgInfo info5 = new MsgInfo("5", new Date(), new Date(), "10011223", "15512345601", "柔电(武汉)科技有限公司", "在海东的协助下，已查明当前设备无心跳的原因是服务器地址没备案", 1, 1, "湖北", "127.0.0.1", 500, 52);
        MsgInfo info6 = new MsgInfo("6", new Date(), new Date(), "10033416", "15512345601", "深圳市中电电力技术股份有限公司", " 后续两个思路，一个是追加服务器地址到白名单，二是走非定向流量", 1, 1, "湖北", "127.0.0.1", 500, 22);
        MsgInfo info7 = new MsgInfo("7", new Date(), new Date(), "10001312", "15512345601", "武汉开目信息技术股份有限公司", "拜访华为，沟通已合作CDN节点业务，了解近期新需求。", 1, 1, "湖北", "127.0.0.1", 500, 25);
        MsgInfo info8 = new MsgInfo("8", new Date(), new Date(), "10031131", "15512345601", "武汉友芝友医疗科技股份有限公司", "拜访部里曲总，协调的云通讯问题：", 1, 1, "湖北", "127.0.0.1", 500, 64);
        MsgInfo info9 = new MsgInfo("9", new Date(), new Date(), "10012341", "15512345601", "武汉康昕瑞基因健康科技有限公司", "“夯实‘根’基，数‘聚’未来 ——第二届中国互联网基础资源大会（CNIRC）", 1, 1, "湖北", "127.0.0.1", 500, 74);
        MsgInfo info10 = new MsgInfo("10", new Date(), new Date(), "10012341", "15512345601", "深圳华大智造科技有限公司", "陪同阳新县教育局柯局长参观考察张家口“教育云”建设和运营。", 1, 1, "湖北", "127.0.0.1", 500, 5);
        MsgInfo info11 = new MsgInfo("11", new Date(), new Date(), "10012331", "15512345601", "湖北锐世数字医学影像科技有限公司", "想了无数答案，包括：奋发向上，行善积德，努力刻苦等等，但我都觉得是虚无缥缈，直到有一天，我悟到了这样一句话：主动去做那些让自己痛苦的事。", 1, 1, "湖北", "127.0.0.1", 500, 96);

        String s1 = objectMapper.writeValueAsString(info1);
        String s2 = objectMapper.writeValueAsString(info2);
        String s3 = objectMapper.writeValueAsString(info3);
        String s4 = objectMapper.writeValueAsString(info4);
        String s5 = objectMapper.writeValueAsString(info5);
        String s6 = objectMapper.writeValueAsString(info6);
        String s7 = objectMapper.writeValueAsString(info7);
        String s8 = objectMapper.writeValueAsString(info8);
        String s9 = objectMapper.writeValueAsString(info9);
        String s10 = objectMapper.writeValueAsString(info10);
        String s11 = objectMapper.writeValueAsString(info11);

        List<String> list = new ArrayList<String>();
        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        list.add(s5);
        list.add(s6);
        list.add(s7);
        list.add(s8);
        list.add(s9);
        list.add(s10);
        list.add(s11);
        for (int i = 1; i < 11; i++) {
            IndexRequest request = new IndexRequest(index, type, Integer.toString(i));
            request.source(list.get(i), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        }
    }


    /**
     * term 查询
     */
    @Test
    public void termQuery() throws IOException {
        SearchRequest request = new SearchRequest().indices(index).types(type);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().from(0).size(5).query(QueryBuilders.termQuery("province", "湖北"));
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }

    /**
     * terms 查询
     */
    @Test
    public void termsQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);

        SearchSourceBuilder query = new SearchSourceBuilder().from(0).size(10).query(QueryBuilders.termsQuery("province", "湖北", "十堰"));
        request.source(query);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }

    /**
     * match_all查询
     */
    @Test
    public void matchAllQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }

    /**
     * match查询
     */
    @Test
    public void matchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchQuery("smsContent", "拜访"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }

    /**
     * 布尔match查询
     */
    @Test
    public void boolMatchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchQuery("smsContent", "拜访 云").operator(Operator.OR));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }

    /**
     * multi_match查询
     */
    @Test
    public void multiMatchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.multiMatchQuery("教育", "smsContent", "corpName"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }

    /**
     * id查询
     */
    @Test
    public void findeById() throws IOException {
        GetRequest request = new GetRequest(index).type(type).id("1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsMap());
    }

    /**
     * ids查询
     */
    @Test
    public void findByIds() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.idsQuery().addIds("1", "2"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * Prefix查询，根据给定关键字模糊查询
     */
    @Test
    public void findByPrefix() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.prefixQuery("corpName", "武汉"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }


    /**
     * Prefix查询，根据给定关键字模糊查询
     */
    @Test
    public void findByFuzzy() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.fuzzyQuery("corpName", "湖北省除天云有限公司"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 使用通配符查询，可以使用“*”和“#”占位符查询
     */
    @Test
    public void findBywildcard() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.wildcardQuery("corpName", "湖北*"));

        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 范围查询，只能针对数值类型
     */
    @Test
    public void findByRange() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.rangeQuery("fee").gt(10).lte(50));

        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 正则查询
     */
    @Test
    public void findByRegexp() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.regexpQuery("mobile", "155[0-9]{8}"));

        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * scroll 深分页查询
     */
    @Test
    public void scrollQuery() throws IOException {
        //1.创建SearchRequest
        SearchRequest request = new SearchRequest(index).types(type);
        //2.指定scro11信息
        request.scroll(TimeValue.timeValueMinutes(1L));
        //3.指定查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(2).sort("createDate", SortOrder.DESC);
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(sourceBuilder);
        //4.获取返回结果scrollId, source
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        String scrollId = response.getScrollId();
        System.out.println("************ 首页 ************");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

        //5.循环-创建SearchScro1 IRequest
        while (true) {
            //6.指定scrol1Id，  指定生存时间
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId).scroll(TimeValue.timeValueMinutes(1L));
            //7.执行查询获取返回结果
            SearchResponse scrolls = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = scrolls.getHits().getHits();
            //8. 判断是否查询到了数据，输出
            if (hits != null && hits.length > 0) {
                System.out.println("************ 下一页 ************");
                for (SearchHit hit : hits) {
                    System.out.println(hit.getSourceAsMap());
                }
            } else {
                //9.判断没有查询到数据-退出循环
                System.out.println("************ 结束 ************");
                break;
            }
        }
        //10.创建CLearScrollRequest
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        //11.指定Scrol1Id
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        System.out.println("删除Scroll： " + clearScrollResponse.isSucceeded());
    }


    /**
     * delete-by-query 查询并删除
     */
    @Test
    public void deleteByQuery() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index).types(type);
        request.setQuery(QueryBuilders.rangeQuery("fee").gte(0).lte(10));
        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }


    /**
     * bool查询
     */
    @Test
    public void boolQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //# 查询省份为武汉或者北京
        boolQuery.should(QueryBuilders.termsQuery("province", "湖北", "北京"));
        //# 运营商不是联通
        boolQuery.mustNot(QueryBuilders.termQuery("operatorId", 2));
        //# smsContent中包含中国和平安
        boolQuery.must(QueryBuilders.matchQuery("smsContent", "世纪 未来").operator(Operator.AND));

        SearchSourceBuilder query = new SearchSourceBuilder().query(boolQuery);
        request.source(query);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }


    /**
     * boosting 查询，主要用于降低negative的查询后排名
     */
    @Test
    public void bostingQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.boostingQuery(
                QueryBuilders.matchQuery("smsContent", "拜访"),
                QueryBuilders.matchQuery("smsContent", "华为")).negativeBoost(0.5f));
        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * filter 查询， filter 查询不会计算分数进行排序，
     */
    @Test
    public void filterQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.matchQuery("smsContent", "二"));
        boolQuery.filter(QueryBuilders.rangeQuery("fee").lte(100));
        boolQuery.filter(QueryBuilders.rangeQuery("fee").lte(100));

        builder.query(boolQuery);
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.toString());
        }
    }


    /**
     * 高亮查询，查询匹配到的结果以一定的样式返回
     */
    @Test
    public void highlightQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("smsContent", "拜访"));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("smsContent", 10);
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.toString());
        }
    }


    /**
     * 去重计数查询
     */
    @Test
    public void cardinalityQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        // 使用指定的聚合查询方式
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.cardinality("agg").field("province"));

        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        Cardinality agg = response.getAggregations().get("agg");
        System.out.println(agg.getValue());
    }


    /**
     * 基于数值的范围统计
     */
    @Test
    public void intRangeCount() throws IOException {
        SearchRequest searchRequest = new SearchRequest(index).types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.range("agg").field("fee").addUnboundedTo(20).addRange(20, 60).addUnboundedFrom(60));

        searchRequest.source(builder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Range range = response.getAggregations().get("agg");
        for (Range.Bucket bucket : range.getBuckets()) {
            String key = bucket.getKeyAsString();
            Object from = bucket.getFrom();
            Object to = bucket.getTo();
            long docCount = bucket.getDocCount();
            System.out.println(String.format("key：%s,from：%s,to：%s,docCount：%s", key, from, to, docCount));
        }
    }


    /**
     * 统计聚合查询
     */
    @Test
    public void extendedStatusQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest(index).types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.extendedStats("agg").field("fee"));

        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        ExtendedStats agg = response.getAggregations().get("agg");
        double max = agg.getMax();
        double min = agg.getMin();
        System.out.println("fee的最大值为: " + max + "，最小值为:" + min);

    }
}
