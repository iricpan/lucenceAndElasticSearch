import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;

/**
 * 测试程序
 */
public class ElasticSearchTest {
   //直接在ElasticSearch中加你文档,自动创建索引,自动创建映射
   @Test
   public void test1() throws Exception{
       //创建连接搜索服务器对象
       Client client = TransportClient
               .builder()
               .build()
               .addTransportAddress(
                       new InetSocketTransportAddress(InetAddress
                               .getByName("127.0.0.1"), 9300)
               );//服务器对应的端口9300
       //描述json数据
       /**
        * {id:xxx,title:xxx, content:xxx}
        */
       XContentBuilder builder = XContentFactory.jsonBuilder()
               .startObject()
               .field("id", 1)
               .field("title", "ElasticSearch是一个基于Lucene的搜索服务器")
               .field("content", "它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。" +
                       "设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。")
               .endObject();
       /**
        * 拼装json的字符串
        */
      /* String json = "{"+
               "\"id\":\"2\","+
               "\"title\":\"基于Lucene的搜索服务器\","+
               "\"content\":\"它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口\""+
       "}";*/
       //创建文档对象
       /**
        * 参数一blog1:表示索引对象
        * 参数二article:类型
        * 参数三1:建立id
        */
       IndexResponse indexResponse = client.prepareIndex("blog1", "article", "1").setSource(builder).get();
//       IndexResponse indexResponse = client.prepareIndex("blog1", "article", "2").setSource(json).execute().actionGet();
       //获取结果
       String index = indexResponse.getIndex();
       String type = indexResponse.getType();
       String id = indexResponse.getId();
       long version = indexResponse.getVersion();
       boolean created = indexResponse.isCreated();
       System.out.println(index+" : "+type+" : "+id+" : "+version+" : "+created);

       //关闭连接
       client.close();
   }

    /**
     * 获取数据
     */
    @Test
    public void getIndexNoMapping() throws Exception{
        //创建client连接对象
        Client client = TransportClient.builder()
                .build()
                .addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300
                ));
        GetResponse actionGet = client.prepareGet("blog1", "article", "1").execute().actionGet();
        System.out.println(actionGet.getSourceAsString());
        client.close();
    }

    /**
     * 搜索在elasticSearch中创建文档对象
     */
    @Test
    public void searchDocument()throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient.builder()
                .build().addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300
                ));
        //搜索数据
        //get()==execute().actionGet()
        SearchResponse searchResponse = client.prepareSearch("blog1")
                .setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery())
                .get();
        SearchHits hits = searchResponse.getHits();//获取命中次数,查询结果有多少对象
        System.out.println("查询结果有:"+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();//每个查询对象
            System.out.println(searchHit.getSourceAsString());//获取字符串格式打印
            System.out.println("title:"+searchHit.getSource().get("title"));
        }
        //关闭连接
        client.close();
    }

    /**
     * ElasticSearch提供QueryBuileders.queryStringQuery(搜索内容) 查询方法，对所有字段进行分词查询
     * @throws Exception
     */
    @Test
    public void searchStringQuery()throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient.builder()
                .build().addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300
                ));
        //搜索数据
        //get()==execute().actionGet()
        SearchResponse searchResponse = client.prepareSearch("blog1")
                .setTypes("article")
                .setQuery(QueryBuilders.queryStringQuery("全文"))
                .get();
        SearchHits hits = searchResponse.getHits();//获取命中次数,查询结果有多少对象
        System.out.println("查询结果有:"+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();//每个查询对象
            System.out.println(searchHit.getSourceAsString());//获取字符串格式打印
            System.out.println("title:"+searchHit.getSource().get("title"));
        }
        //关闭连接
        client.close();
    }
    /**
     * QueryBuilders.wildcardQuery模糊查询 *任意字符串 ?任意单个字符
     * @throws Exception
     */
    @Test
    public void searchWildCardQuery()throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient.builder()
                .build().addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300
                ));
        //搜索数据
        //get()==execute().actionGet()
        SearchResponse searchResponse = client.prepareSearch("blog1")
                .setTypes("article")
                .setQuery(QueryBuilders.wildcardQuery("content","*全文*"))
                .get();
        SearchHits hits = searchResponse.getHits();//获取命中次数,查询结果有多少对象
        System.out.println("查询结果有:"+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();//每个查询对象
            System.out.println(searchHit.getSourceAsString());//获取字符串格式打印
            System.out.println("title:"+searchHit.getSource().get("title"));
        }
        //关闭连接
        client.close();
    }
    /**
     * 查询content词条为“全文” 内容，使用TermQuery
     * @throws Exception
     */
    @Test
    public void searchTermQuery()throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient.builder()
                .build().addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300
                ));
        //搜索数据
        //get()==execute().actionGet()
        SearchResponse searchResponse = client.prepareSearch("blog1")
                .setTypes("article")
                .setQuery(QueryBuilders.termQuery("content","全文"))
                .get();
        SearchHits hits = searchResponse.getHits();//获取命中次数,查询结果有多少对象
        System.out.println("查询结果有:"+hits.getTotalHits()+"条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();//每个查询对象
            System.out.println(searchHit.getSourceAsString());//获取字符串格式打印
            System.out.println("title:"+searchHit.getSource().get("title"));
        }
        //关闭连接
        client.close();
    }
}
