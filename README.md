[toc]

# Elastic Search 开始



倒排索引

将存放的数据，以-定的方式进行分词，并且将分词的内容存放到一个单独的分词库中。
当用户去查询数据时，会将用户的查询关键字进行分词。
然后去分词库中匹配内容，最终得到数据的id标识。
根据id标识去存放数据的位置拉取到指定的数据。

![image-20201204103016235](D:\java宝藏\node\Elastic Search\image-20201204103016235.png)





# Elastic Search 安装

## 安装ES&Kibana

```yml
version: "3.1"
services:
    elasticsearch:
    # http://hub.daocloud.io/  (国内docker镜像仓库)
        image: daocloud.io/library/elasticsearch:6.5.4
        restart: always
        container_name: elasticsearch
        ports:
            - 9200:9200
        environment:
      		- "ES_JAVA_OPTS=-Xms64m -Xmx1024m"
      		- "discovery.type=single-node"
      		- "COMPOSE_PROJECT_NAME=elasticsearch-server"
    kibana:
    # elasticsearch 和 kibana 都是elastic下的产品，他们的版本需要统一
        image: daocloud.io/library/kibana:6.5.4   
        restart: always
        container_name: kibana
        ports:
            - 5601:5601
        environment:
            - elasticsearch_url=http://192.168.222.129:9200
        depends_on:
            - elsaticsearch
```



在linux虚拟机下创建名称为 `docker-compose.yml` 文件，将以上内容粘贴到文件中。

在文件夹所在目录下输入命令 docker-compose up -d 执行ES 和 Kibana的安装部署。

（该模块是使用docker安装的，在安装之前确保机器上docker环境已安装好）





## 安装 IK 分词器

> 下载 IK 分词器的地址：~~https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.5.4/elasticsearch-analysis-ik-6.5.4.zip~~
>
> （IK 分词器的版本要和 ES 的版本一致，该分词器也需要在docker中安装。IK 分词器作为插件安装在ES容器中，使用过程中重启容器即可）
>
> 由于网络问题，采用国内路径下载： ./elasticsearch-plugin install http://tomcat01.qfjava.cn:81/elasticsearch-analysis-ik-6.5.4.zip     
>
> （进入ES 文件夹 bin 目录下，执行以上脚本命令，下载安装 ik 分词器）
>
> 重启 ES 容器，让 ik 分词器生效



> Docker 相关命令的使用
>
> 1.  查看 docker 容器列表：  docker ps
> 2. 进入容器： docker exec -it "容器id" bash
> 3. 退出容器：exit
> 4. 重启容器： docker restart "容器id"





# ElsticSearch 基本操作

## ES 的结构



### 索引 Index,分片备份

> ES服务中，可以创建多个索引
>
> 每一个索引默认被分成5片存储。
>
> 每一个分片都会存在至少一个备份分片。
>
> 备份分片默认不会帮助检索数据，当ES检索压力特别大时，备份分片才会帮助检索数据。
>
> 备份的分片必须放在不同的服务器中。

![image-20201207170305728](D:\java宝藏\node\Elastic Search\image-20201207170305728.png)



### 类型Type

> 一个索引下，可以创建多个类型（Type）。
>
> PS：根据版本不同，类型的创建也不同。

![image-20201207171053477](D:\java宝藏\node\Elastic Search\image-20201207171053477.png)



### 文档Doc

> 一个类型下可以有多个文档。这个文档类型于mysql表中的多行数据。

![image-20201207171541331](D:\java宝藏\node\Elastic Search\image-20201207171541331.png)



### 列 field

> 一个文档中，可以包含多个属性。类似于mysql表中一行数据存在多个列。

![image-20201207171702591](D:\java宝藏\node\Elastic Search\image-20201207171702591.png)





## 操作ES的RESTful语法

> GET请求：
>
> http://ip:port/index  ：查询索引信息
>
> http://ip:port/index/type/doc_id  ： 查询指定的文档信息
>
> POST请求：
>
> http://ip:port/index/type/_search ：查询，可以在请求体中添加一个json字符串，代表查询条件
>
> http://ip:port/index/type/doc_id/_update ：修改文档，在请求体中，指定修改的具体信息
>
> PUT请求：
>
> http://ip:port/index ：创建一个索引，在请求体中指定索引的信息，类型，结构
>
> http://ip:port/index/type_mappings ：创建索引时，指定索引的文档存储的属性的信息
>
> DELETE请求：
>
> http://ip:port/index  
>
> http://ip:port/index/type/doc_id：删除指定的文档





## ES中Field可以指定的类型

> string （字符串类型）：
>
> - text：一般被用于全文检索。将当前Filed进行分词
>
> - keyword：当前Field不会被分词
>
> Numeric datatypes（数值类型）：
>
> -  long：-2^63 ~ 2^63 -1
> -  integer：-2^31 ~ 2^31 -1
> - short：
> - byte：
> - double：
> - float：
> - half_float：精度比float小一半
> - scaled_float：根据一个long和scaled来表示一个浮点型。long-345，scaled-100->3.45
>
> Date datatype（时间类型）：
>
> - date：可以针对时间类型指定具体的格式
>
> Boolean datatype（布尔类型）：
>
> - boolean：
>
> Binary datatype（二进制类型）：
>
> - binary：暂时支持Base64编码的字符串
>
> Range datatypes（范围类型）：
>
> - long range：赋值时，无序指定具体的内容，只需要存储一个范围即可，指定gt，lt， gte, lte
> - integer_ range：同上。
> - float_range：同上。
> - double_range：同上。
> - date_range：同上。
> - ip_range：同上。
>
> Geo datatypes（经纬度类型）：
>
> - geo_point：用来存储经纬度
>
> IP datatypes（IP类型）：
>
> - ip：可以存储IPV4或者IPV6
>
> 
>
> 其他数据类型可以参考官网： https://www.elastic.co/guide/en/elasticsearch/reference/6.5/mapping-types.html



## 创建索引并指定数据结构

```json
# 创建索引，指定数据结构
PUT /book
{
  "settings":{
      # 备份数
    "number_of_replicas": 1, 
      # 分片数
    "number_of_shards": 5
  }
    # 指定数据结构
  , "mappings": {
    # 类型type
    "novel":{
      # 文档存储的 Field
      "properties":{
    	# Field属性名
        "name":{
    	  # 类型
          "type":"text",
    	  # 指定分词器
          "analyzer":"ik_max_word",
    	  # 指定当前的Filed可以被作为查询条件，默认为true
          "index":true,
    	  # 是否需要额外存储，默认是false
          "store":false
        },
        "author":{
          "type":"keyword"
        },
        "count":{
          "type":"long"
        },
        "onSale":{
          "type":"date",
            # 可以指定date的数据格式
          "format":"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis" 
        },
        "desc":{
          "type":"text",
          "analyzer":"ik_max_word"
        }
      }
    }
  }
}
```



## 文档操作

> 文档在ES服务中的唯一标识，`_index`， `_type` ,`_ id` 三个内容为组合，锁定一 个文档，操作时添加还是修
> 改。



### 新建文档

```json
# 添加文档，自动生成id
POST /book/novel
{
  "name":"JAVA从入门到精通",
  "author":"老鸟",
  "count":25627,
  "onSale":"2010-10-24",
  "desc":"是人民邮电出版社于 2010年出版的图书，由国家863中部软件孵化器主编。"
}
```

```json
# 添加文档，手动指定 _id
PUT /book/novel/111
{
  "name":"西游记",
  "author":"吴承恩",
  "count":205000,
  "onSale":"1700-10-24",
  "desc":"《西游记》是中国古代第一部浪漫主义章回体长篇神魔小说。现存明刊百回本《西游记》均无作者署名。清代学者吴玉搢等首先提出《西游记》作者是明代吴承恩"
}
```



### 修改文档

覆盖式修改：

```json
PUT /book/novel/111
{
  "name":"西游记",
  "author":"吴承恩",
  "count":205000,
  "onSale":"1700-10-24",
  "desc":"《西游记》是中国古代第一部浪漫主义章回体长篇神魔小说。现存明刊百回本《西游记》均无作者署名。清代学者吴玉搢等首先提出《西游记》作者是明代吴承恩"
}
```

给予doc方式，更新文档：

```json
POST /book/novel/111/_update
{
  "doc": {
    # 指定需要修改的 k:v 即可
    "name":"西行纪"
  }
  
}
```





# JAVA操作ES

>  新建maven工程，导入 ES 的依赖和 ES 高级 api （注意：引入依赖的版本要和安装的 ES 的版本对应）

```xml
  		<!--ES-->
        <!-- https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch -->
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>6.5.4</version>
        </dependency>
        <!--ES高级api-->
        <!-- https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>6.5.4</version>
        </dependency>
```

> 连接ES，通过 `RestHighLevelClient` 操作ES

```java
 public static RestHighLevelClient getClient() {
        HttpHost host = new HttpHost("192.168.222.130", 9200);
        RestClientBuilder builder = RestClient.builder(host);
        // 创建RestHighLevelClient对象
        return new RestHighLevelClient(builder);
    }
```

> 创建索引

```java
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
        client.indices().create(mapping, RequestOptions.DEFAULT);
    }


```

> 判断index是否存在

```java
public void indexExists() throws IOException {
        // 准备request对象
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        // 使用client 操作ES
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
    }
```

> 删除index

```java
 public void indexDelete() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest();
        request.indices(index);
        client.indices().delete(request, RequestOptions.DEFAULT);
    }
```

> 添加文档doc

```java
public void creatDoc() throws IOException {
        // 准备一个json数据
        Person person = new Person(1, "张三", 20, new Date());
        // 准备一个request对象，手动指定id
        String json = mapper.writeValueAsString(person);
        System.out.println(json);
        IndexRequest request = new IndexRequest(index, type, person.getId().toString());
        request.source(json, XContentType.JSON);
        // 通过client对象添加
        client.index(request, RequestOptions.DEFAULT);
    }
```

> 更新文档

```java
 public void updateDoc() throws IOException {
        String docId = "1";
        // 创建一个map 指定需要修改的内容
        Map<String, String> map = new HashMap<>();
        map.put("name", "小张");
        // 创建一个request对象
        UpdateRequest request = new UpdateRequest(index, type, docId);
        request.doc(map);
        // 通过client对象执行
        client.update(request, RequestOptions.DEFAULT);
    }
```

> 删除文档

```java
public void deleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest(index, type, "3");
        client.delete(request, RequestOptions.DEFAULT);
    }
```

> 批量添加文档

```java
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

        client.bulk(request, RequestOptions.DEFAULT);
    }
```

> 批量删除文档

```java
public void bulkDeleteDoc() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest(index, type, "1"));
        request.add(new DeleteRequest(index, type, "2"));
        request.add(new DeleteRequest(index, type, "3"));

        client.bulk(request, RequestOptions.DEFAULT);
    }
```











# ElasticSearch的各种查询





准备：

![no-shadow](D:\java宝藏\node\Elastic Search\image-20201209171557515.png)

创建sms_logs_index表，设置mapping 为以上结构；

添加初始数据，本章中要用到的查询提供记录。

## term&terms查询

### term查询

> term的查询是代表完全匹配，搜索之前不会对你搜索的关键字进行分词，对你的关键字去文档分词库中去匹
> 配内容。

```json
# term查询
POST /sms_logs_index/sms_logs_type/_search
{
  "from": 0,
  "size": 5,
  "query": {
    "term": {
      "province": {
        "value": "湖北"
      }
    }
  }
}
```

```java
public void termQuery() throws IOException {
        SearchRequest request = new SearchRequest().indices(index). types(type);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().from(0).size(5).query(QueryBuilders.termQuery("province", "湖北"));
        request.source(sourceBuilder);
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }
```



### terms查询

> terms和term的查询机制是一样，都不会将指定的查询关键字进行分词，直接去分词库中匹配，找到相应文档内容。
>
> terms是在针对一个字段包含多个值的时候使用。
> term: where province亏北京;
> terms: where province =北京or province= ? or province= ?

```json
# terms查询,可以查询多个参数
POST /sms_logs_index/sms_logs_type/_search
{
  "from": 0,
  "size": 5,
  "query": {
    "terms": {
      "province": [
        "湖北","武汉","成都"
      ]
    }
  }
}
```

```java
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
```





## match查询

> match查询属于高层查询，他会根据你查询的字段类型不一样，采用不同的查询方式。
>
> - 查询的是日期或者是数值的话，他会将你基于的字符串查询内容转换为日期或者数值对待。
> - 如果查询的内容是一 个不能被分词的内容(keyword) ，match查询不会对你指定的查询关键字进行分
>   词。
> - 如果查询的内容是一个可以被分词的内容(text) ，match会将你指定的查询内容根据一 定的方式去分
>   词，去分词库中匹配指定的内容。
>
> match查询，实际底层就是多个term查询，将多个term查询的结果给你封装到了一起。



### match_all 查询

> 查询全部内容，不指定任何查询条件。

```json
# match_all 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "match_all": {}
  }
}
```

```java
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
```



### match 查询

> 指定一个Field作为筛选条件

```json
# match 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "match": {
      "smsContent": "安装"
    }
  }
}

```

```java
 public void matchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchQuery("smsContent","拜访"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }
```



### 布尔match查询

> 基于一个Field匹配的内容，采用and或者or的方式连接

```json
# 布尔 match 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "match": {
      "smsContent": {
        "query": "拜访 云",
        "operator": "or"
      }
    }
  }
}
```

```java
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
```

### multi_match 查询

> match针对一个field做检索，multi match针对多个field进行检索，多个field对应一-个text。

```json
# multi_match 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "multi_match": {
      "query": "教育",                                 # 指定查询的text
      "fields": ["smsContent","corpName"]         #指定的多个Field
    }
  }
}
```

```java
 public void multiMatchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.multiMatchQuery("教育", "smsContent","corpName"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            System.out.println(map);
        }
    }
```



## 其他查询

### id查询

```json
#id 查询
GET /sms_logs_index/sms_logs_type/1
```

```java
@Test
    public void idQuery() throws IOException {
        GetRequest request = new GetRequest(index).type(type).id("1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsMap());
    }
```

### ids查询

> 根据多个id查询，类似MySQL中的where id in (id1, id2, id...)

```json
# ids 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "ids": {
      "values": ["1","2"]
    }
  }
}
```

```java
public void idsQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.idsQuery().addIds("1", "2"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
```

### prefix查询

> 前缀查询，可以通过一个关键字去指定一个Field的前缀， 从而查询到指定的文档。

```json
# prefix 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
   "prefix": {
     "corpName": {
       "value": "湖北"
     }
   }
  }
}

```

```java
public void findByPrefix() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.prefixQuery("corpName","武汉"));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
```

### fuzzy查询

> 模糊查询，我们输入字符的大概，ES就可以去根据输入的内容大概去匹配一下结果。这里的查询，模糊查询允许有错别字的出现。

```json
# fuzzy 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
   "fuzzy": {
     "corpName": {
       "value":  "湖北省除天云有限公司",
       "prefix_length": 3                # 指定前面几个字符是不允许出现错误的
     }
     }
   }
  }
}
```



### wildcard 查询

> 通配查询，和MySQL中的like是一个套路，可以在查询时，在字符串中指定通配符*和占位符?

```json
# wildcard 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "wildcard": {
      "corpName": {
        "value": "*科技*"
      }
    }
  }
}

```



### range 查询

> 范围查询，只针对数值类型，对摸个field进行范围指定。

```json
#range 范围查询
POST /sms_logs_index/sys_logs_type/_search
{
  "query": {
    "range": {
      "fee": {
        "gt": 1,
        "lte": 50
      }
    }
  }
}
```



### regexp 查询

> 正则查询，通过编写正则表达式去匹配内容。
>
> Ps：prefix，fuzzy，wildcard 和 regexp 查询效率相对比较低，要求查询效率较高时，避免使用

```json
#regexp 正则查询
POST /sms_logs_index/sys_logs_type/_search
{
  "query": {
    "regexp": {
      "mobile": "155[0-9]{8}"    # 编写正则表达式
    }
  }
}
```



## 深分页Scroll

> ES对from + size是有限制的，from和size二者之 和不能超过1W
>
> from+size在ES查询数据的方式:
>
> - 现将用户指定的关键进行分词。
> - 将词汇去分词库中进行检索，得到多个文档的id。
> - 去各个分片中去拉取指定的数据。（耗时较长）
> - 将数据根据score进行排序。（耗时较长）
> - 根据from的值，将查询到的数据舍弃一部分。
> - 返回结果。
>
> Scroll+size在ES查询数据的方式:
>
> - 现将用户指定的关键进行分词。
> - 将词汇去分词库中进行检索，得到多个文档的id。
> - 将文档的id存放在一个ES的上下文中。
> - 根据你指定的size的个数去ES中检索指定的数据，拿完数据的文档id,会从上下文中移除。
> - 如果需要下一-页数据，直接去ES的上下文中，找后续内容。
> - 循环上面两个步骤，拿到所需的全部数据完成
>
> Scroll查询方式，不适合做实时查询。

```json
#执行 scrol 查询，返回第一页数据，并且将文档id信息存放在ES上下文中，指定生存时间为1m 
POST /sms_logs_index/sms_logs_type/_search?scroll=1m
{
"query": {
  "match_all": {}
  },
  "size": 2,
  "sort": [
    {
      "createDate": {
        "order": "desc"
      }
    }
  ]
}

# 根据scroll查询下一页数据
POST /_search/scroll
{
  "scroll_id":"<根据第一步得到的scroll_id去指定>",
  "scroll":"<生存时间>"
}

# 删除 scroll 在ES上下文中的数据
DELETE /_search/scroll/“scroll_id"

```

```java
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
        // 获取完记录之后 scroll_id 还会在内存中存活指定的时间，此时scroll_id 是无用的，因此将其删除 
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        System.out.println("删除Scroll： " + clearScrollResponse.isSucceeded());
    }
```



## delete-by-query

> 根据term, match等查询方式去删除大量的文档
>
> **Ps:如果你需要删除的内容，是index 下的大部分数据，推荐创建一个全新的index, 将保留的文档内容，添加到全新的索引**

```json
# delete-by-query 查询并删除
POST /sms_logs_index/sms_logs_type/_delete_by_query
{
  "query": {
    "range": {
      "fee": {
        "gte": 10,
        "lte": 50
      }
    }
  }
}
```

```java
    public void deleteByQuery() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index).types(type);
        request.setQuery(QueryBuilders.rangeQuery("fee").gte(0).lte(10));
        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }
```



## 复合查询

### bool 查询

> 复合过滤器，将你的多个查询条件，以一定的逻辑组合在一起。
>
> - must：所有的条件，用must组合在一起，表示And的意思
> - must_ not：将must_ not中的条件，全部都不能匹配，标识Not的意思
> - should： 所有的条件，用should组合在一 起，表示0r的意思

```json
# 查询省份为武汉或者北京
# 运营商不是联通
# smsContent中包含中国和平安
 POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "terms": {
            "province": [
              "北京",
              "湖北"
            ]
          }
        }
      ],
      "must_not": [
        {
          "term": {
            "operatorId": {
              "value": "2"
            }
          }
        }
      ],
      "must": [
        {
          "match": {
            "smsContent": {
              "query": "中国 平安",
              "operator": "and"
            }
          }
        }
      ]
    }
  }
}
```

```java
    public void boolQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //# 查询省份为武汉或者北京
        boolQuery.should(QueryBuilders.termsQuery("province", "武汉", "北京"));
        //# 运营商不是联通
        boolQuery.mustNot(QueryBuilders.termQuery("operatorId", 2));
        //# smsContent中包含中国和平安
        boolQuery.must(QueryBuilders.matchQuery("smsContent", "中国 平安").operator(Operator.AND));

        SearchSourceBuilder query = new SearchSourceBuilder().query(boolQuery);
        request.source(query);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
```



### boosting 查询

> boosting查询可以帮助我们去影响查询后的score。
>
> - positive: 只有匹配上positive的查询的内容，才会被放到返回的结果集中。
>
> - negative: 如果匹配上了positive并且也匹配上了negative, 就可以降低这样的文档score。
>
> - negative_ boost: 指定系数，必须小于1.0
>
>   
>
> 关于查询时，分数是如何计算的:
>
> - 搜索的关键字在文档中出现的频次越高，分数就越高
> - 指定的文档内容越短，分数就越高
> - 我们在搜索时，指定的关键字也会被分词，这个被分词的内容，被分词库匹配的个数越多，分数越高

```json
# bosting 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "smsContent": "拜访"
        }
      },
      "negative": {
        "match": {
          "smsContent": "华为"
        }
      },
      "negative_boost": 0.5
    }
  }
}
```

```java
    public void bostingQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest(index).types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.boostingQuery(
                        QueryBuilders.matchQuery("smsContent", "拜访"),
                        QueryBuilders.matchQuery("smsContent", "华为")).negativeBoost(0.5f));
        searchRequest.source(builder);

        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
```



## filter 查询

> query,根据你的查询条件，去计算文档的匹配度得到一个分数，并且根据分数进行排序，不会做缓存的。
> filter,根据你的查询条件去查询文档，不去计算分数，而且filter会对经常被过滤的数据进行缓存。

```json
#filter 查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "match":{
            "smsContent": "二"
          }
        },
        {
          "range":{
            "fee":{
              "lte":100
            }
          }
        }
        ]
    }
  }
}
```

```java
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
```



## 高亮查询

> 高亮查询就是你用户输入的关键字，以-定的特殊样式展示给用户，让用户知道为什么这个结果被检索出来。
> 高亮展示的数据，本身就是文档中的一个Field, 单独将Field以highlight的形式返回给你。
>
> ES提供了一-个highlight属性，和query同级别的。
>
> - fragment_ size：指定高亮数据展示多少个字符回来。
> - pre _tags：指定前缀标签，举个栗子<font color="red">
> - post _tags:：指定后缀标签，举个栗子</font>
> - fields：指定那几个field以高亮形式返回

```json
# highlight 高亮查询
POST /sms_logs_index/sms_logs_type/_search
{
  "query": {
    "match": {
      "smsContent": "拜访"
    }
  },
  "highlight": {
    "fields": {
      "smsContent":{}
    },
    "pre_tags": "<font color='red'>",
    "post_tags": "</font>",
    "fragment_size": 10
  }
}
```

```java
public void highlightQuery() throws IOException {
        SearchRequest request = new SearchRequest(index).types(type);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("smsContent","拜访"));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("smsContent",10);
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.toString());
        }
    }
```



## 聚合查询

> ES的聚合查询和MySQL的聚合查询类型，ES的聚合查询相比MySQL要强大的多，ES提供的统计数据的方式多种多样。

```json
# ES聚合查询的RESTfu1语法
POST /index/type/_ search
{
	 "aggs": {
		”名字(agg)”:{
			"agg type' : {
				”属性”:”值”
        		}
     		}
	 }
}


```



### 去重计数查询

> 去重计数，即Cardinality, 第一步先将返回的文档中的一个指定的field进行去重，统计一共有多少条

```json
# 去重计数查询
POST /sms_logs_index/sms_logs_type/_search
{
  "aggs": {
    "agg": {
      "cardinality": {
        "field": "province"
      }
    }
  }
}
```

```java
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
```



### 范围统计

> 统计-定范围内出现的文档个数，比如，针对某一个Field的值在 0~100,100~200,200~300之间文档出现的个数分别是多少。
> 范围统计可以针对普通的数值，针对时间类型，针对jp类型都可以做相应的统计。
> range, date_range, ip_range

**基于数值的范围统计**

```json
# 基于数值的范围统计
POST /sms_logs_index/sms_logs_type/_search
{
  "aggs": {
    "agg": {
      "range": {
        "field": "fee",
        "ranges": [
          {
            "to":20
          },{
            "from": 20,              # from 有包含的意思
            "to": 70
          },{
            "from": 70
          }
        ]
      }
    }
  }
}
```

```java
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
```



**基于日期类型的范围统计**

```json
# 基于时间 范围统计
POST /sms_logs_index/sms_logs_type/_search
{
  "aggs": {
    "agg": {
      "date_range": {
        "field": "createDate",
        "format": "yyyy", 
        "ranges": [
          {
            "to": "2000"
          },{
            "from": "2000"
          }
        ]
      }
    }
  }
}
```



基于ip的范围统计

```json
# 基于ip 范围统计
POST /sms_logs_index/sms_logs_type/_search
{
  "aggs": {
    "agg": {
      "ip_range": {
        "field": "ipAddr",
        "ranges": [
          {
            "from": "10.0.0.5",
            "to": "10.0.0.10"
          }
        ]
      }
    }
  }
}
```



### 统计聚合查询

> 他可以帮你查询指定Field的最大值，最小值，平均值，平方和。
> 使用: extended_ stats

```json
# 统计聚合查询
POST /sms_logs_index/sms_logs_type/_search
{
  "aggs": {
    "agg": {
      "extended_stats": {
        "field": "fee"
      }
    }
  }
}
```

```java
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
```

<font color='green'>其他的聚合查询方式查看官方文档: https://www.elastic.co/guide/en/elasticsearch/reference/6.5/index.html </font>





## 地图经纬度查询

> ES中提供了一个数据类型geo_point, 这个类型就是用来存储经纬度的。

```json
# 地图经纬度查询
# 创建一个索引，指定 name、location
PUT /map
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "map":{
      "properties":{
        "name":{
          "type":"text",
          "analyzer":"ik_max_word"
        },
        "location":{
          "type":"geo_point"
        }
      }
    }
  }
}

# 添加测试数据
PUT /map/map/1
{
  "name":"天安门",
  "location":{
    "lon":116.403263,
    "lat":39.914382
    
  }
}


PUT /map/map/2
{
  "name":"海淀公园",
  "location":{
    "lon":116.3244580255215,
    "lat":39.99101005988627
    
  }
}


PUT /map/map/3
{
  "name":"北京动物园",
  "location":{
    "lon":116.342753,
    "lat":39.947689
    
  }
}
```





### ES 的地图检索方式

> - geo_distance： 直线距离方式检索
> - geo_bounding_box：以两点确定一个矩形
> - geo_polygon：多边形检索

```json

# geo_distance 距离查询
POST /map/map/_search
{
  "query": {
    "geo_distance": {
      "location": {       # 确定一个点
        "lon":116.42396,
        "lat":39.91095
      },
      "distance": 3000,   # 确定半径
      "distance_type":"arc"   # 指定形状为圆形
    }
  }
}
```

```json
# geo_bounding_box
POST /map/map/_ search
{
    "query”: {
    	"geo_ bounding. box": {
		"location”: {
			"top_ left”: {
				"lon”: 116. 326943,
				"lat”: 39. 95499
			},
			"bottom_ right": {
				"1on”: 116. 433446, 
				"lat”: 39. 908737
			}
		}
	}
}
```

```json
# geo_ polygon
POST / map/ map/_ search
{
	"query": {
		"geo_ polygon": {
			"location": {
				"points": [
				{
                    			"1on”: 116.298916,
					"lat”: 39. 99878
				},{
					"lon": 116. 29561,
					"lat”: 39. 972576
				},{
					"lon": 116. 327661,
					"lat”: 39. 984739
               			 }]
			}
        	}
    }
}

```

