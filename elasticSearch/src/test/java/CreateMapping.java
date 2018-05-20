import cn.bd.elasticSearch.Article;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import javax.transaction.TransactionRolledbackException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateMapping {
    /**
     * 创建映射
     */
    @Test
    public void testCreateMapping() throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient.builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(
                                InetAddress.getByName("127.0.0.1"), 9300
                        )
                );
        //创建索引
        client.admin().indices().prepareCreate("blog2").get();
        //添加映射
        /**
         * 格式：
         * "mappings" : {
             "article" : {
                 "dynamic" : "false",
                 "properties" : {
                     "id" : { "type" : "string" },
                     "content" : { "type" : "string" },
                     "author" : { "type" : "string" }
                 }
             }
         }
         */
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("article")
                        .startObject("properties")
                            .startObject("id")
                            .field("type", "integer").field("store", "yes")
                            .endObject()
                            .startObject("title")
                            .field("type", "string").field("store", "yes").field("analyzer", "ik")
                            .endObject()
                            .startObject("content")
                            .field("type", "string").field("store", "yes").field("analyzer", "ik")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
        //创建映射
        PutMappingRequest mapping = Requests.putMappingRequest("blog2")
                .type("article").source(builder);
        client.admin().indices().putMapping(mapping).get();

        //关闭连接
        client.close();
    }

    /**
     * 通过对象的方式创建文档
     */
    @Test
    public void createMappingByObject() throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(
                                InetAddress.getByName("127.0.0.1"), 9300
                        )
                );
        //描述json数据
        /*
         * {id:xxx, title:xxx, content:xxx}
         */
        Article article = new Article();
        article.setId(2);
        article.setTitle("搜索工作其实很快乐");
        article.setContent("我们希望我们的搜索解决方案要快，我们希望有一个零配置和一个完全免费的搜索模式，" +
                "我们希望能够简单地使用JSON通过HTTP的索引数据，我们希望我们的搜索服务器始终可用，" +
                "我们希望能够一台开始并扩展到数百，我们要实时搜索，我们要简单的多租户，" +
                "我们希望建立一个云的解决方案。Elasticsearch旨在解决所有这些问题和更多的问题。");
        ObjectMapper mapper = new ObjectMapper();
        //创建文档
        client.prepareIndex("blog2","article",article.getId().toString())
                .setSource(mapper.writeValueAsString(article)).get();
        //关闭
        client.close();
    }

    /**
     * 搜索文档
     * @throws Exception
     */
    @Test
    public void searchDocument() throws Exception{
        TransportClient client = TransportClient.builder()
                .build()
                .addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300));
        // 定义Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        SearchResponse searchResponse = client.prepareSearch("blog2").setTypes("article")
                // .setQuery(new MatchAllQueryBuilder())// 查询所有（词条查询、通配符查询、模糊查询）
                //  .setQuery(QueryBuilders.queryStringQuery("搜寻").field("title").field("content")) // 条件查询，默认是搜索所有字段；如果是某个字段
                // .setQuery(QueryBuilders.wildcardQuery("title", "搜?")) // 通配符查询，*表示任何字符，?表示任意单个字符
                // .setQuery(QueryBuilders.fuzzyQuery("title", "lucenx")) // 相似度查询
                .setQuery(QueryBuilders.termQuery("title", "搜索")) // 词条查询
                .get();
        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        System.out.println("总记录数:"+hits.getTotalHits());
        // 返回的结果
        SearchHit[] searchHits = hits.getHits();
        for(SearchHit searchHit:searchHits){
            System.out.println("得分:"+searchHit.getScore());
            // json的字符串
            System.out.println(searchHit.getSourceAsString());
            // 将json的字符串转换成Article对象
            Article article = objectMapper.readValue(searchHit.getSourceAsString(), Article.class);
            System.out.println("id:"+article.getId());
            System.out.println("title:"+article.getTitle());
            System.out.println("content:"+article.getContent());
            System.out.println("----------------------------------------------");
        }
        client.close();
    }

    /**
     * 文档更新
     * 方式一： 使用prepareUpdate、prepareIndex都可以
     * 方式二： 直接使用update
     * 更新时,如果原文档不存,会新建
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(
                                InetAddress.getByName("127.0.0.1"), 9300
                        )
                );
        //描述json数据
        /*
         * {id:xxx, title:xxx, content:xxx}
         */
        Article article = new Article();
        article.setId(2);
        article.setTitle("搜索工作其实很快乐_更新33333");
        article.setContent("我们希望我们的搜索解决方案要快，我们希望有一个零配置和一个完全免费的搜索模式，" +
                "我们希望能够简单地使用JSON通过HTTP的索引数据，我们希望我们的搜索服务器始终可用，" +
                "我们希望能够一台开始并扩展到数百，我们要实时搜索，我们要简单的多租户，" +
                "我们希望建立一个云的解决方案。Elasticsearch旨在解决所有这些问题和更多的问题。");
        ObjectMapper mapper = new ObjectMapper();
        //更新文档
      /*  client.prepareIndex("blog2","article",article.getId().toString())
                .setSource(mapper.writeValueAsString(article)).get();*/
//        client.prepareUpdate("blog2","article",article.getId().toString())
//                .setDoc(mapper.writeValueAsString(article)).get();
        client.update(new UpdateRequest("blog2","article",article.getId().toString())
                .doc(mapper.writeValueAsString(article))).get();
        //关闭
        client.close();
    }

    /**
     * 删除
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception{
        //创建连接搜索服务器对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(
                                InetAddress.getByName("127.0.0.1"), 9300
                        )
                );
        Article article = new Article();
        article.setId(2);
//        client.prepareDelete("blog2","article",article.getId().toString()).get();
        client.delete(new DeleteRequest("blog2","article",article.getId().toString())).get();
        //关闭
        client.close();
    }

    /**
     * 批量查询100条记录
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testAddMultiplies() throws IOException, InterruptedException,
            ExecutionException {
        // 创建连接搜索服务器对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress
                                .getByName("127.0.0.1"), 9300));

        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 1; i <= 100; i++) {
            // 描述json 数据
            Article article = new Article();
            article.setId(i);
            article.setTitle(i + "搜索工作其实很快乐");
            article.setContent(i
                    + "我们希望我们的搜索解决方案要快，我们希望有一个零配置和一个完全免费的搜索模式，我们希望能够简单地使用JSON通过HTTP的索引数据，我们希望我们的搜索服务器始终可用，我们希望能够一台开始并扩展到数百，我们要实时搜索，我们要简单的多租户，我们希望建立一个云的解决方案。Elasticsearch旨在解决所有这些问题和更多的问题。");

            // 建立文档
            client.prepareIndex("blog2", "article", article.getId().toString())
                    .setSource(objectMapper.writeValueAsString(article)).get();
        }
        // 关闭连接
        client.close();
    }

    /**
     * 组合查询
     */

    @Test
    public void combinationQuery() throws Exception{
        TransportClient client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        SearchResponse searchResponse = client.prepareSearch("blog2").setTypes("article")
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("title", "搜索"))// 词条查询
                        //.must(QueryBuilders.rangeQuery("id").from(1).to(5))  // 范围查询
                        .must(QueryBuilders.wildcardQuery("content", "Elastics*ch".toLowerCase())) // 模糊查询
                )// 大家回去改成should试试看
                .get();
        SearchHits hits = searchResponse.getHits();
        System.out.println("总记录数："+hits.getTotalHits());
        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            Map<String, Object> source = searchHit.getSource();
            System.out.println(source.get("id")+"    "+source.get("title")+"    "+source.get("content"));
        }
        client.close();

    }

    /**
     *  分页搜索
     * @throws IOException
     */
    @Test
    public void testPagination() throws IOException {
        // 创建连接搜索服务器对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress
                                .getByName("127.0.0.1"), 9300));
        // 搜索数据
        // get() === execute().actionGet()
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("blog2").setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery());//默认每页10条记录

        // 查询第2页数据，每页20条
        //setFrom()：从第几条开始检索，默认是0。
        //setSize():每页最多显示的记录数。
        searchRequestBuilder.setFrom(20).setSize(20);
// 排序
        searchRequestBuilder.addSort("id",SortOrder.DESC);
        SearchResponse searchResponse = searchRequestBuilder.get();
        printSearchResponse(searchResponse);

        // 关闭连接
        client.close();
    }
    private void printSearchResponse(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits(); // 获取命中次数，查询结果有多少对象
        System.out.println("查询结果有：" + hits.getTotalHits() + "条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next(); // 每个查询对象
            System.out.println(searchHit.getSourceAsString()); // 获取字符串格式打印
            // System.out.println("id:" + searchHit.getSource().get("id"));
            // System.out.println("title:" +
            // searchHit.getSource().get("title"));
            // System.out.println("content:" +
            // searchHit.getSource().get("content"));
        }
    }

    /**
     *  // 高亮查询结果 处理 搜索
     * @throws IOException
     */
    @Test
    public void testHighLighting() throws IOException {
        // 创建连接搜索服务器对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(
                        new InetSocketTransportAddress(InetAddress
                                .getByName("127.0.0.1"), 9300));

        ObjectMapper objectMapper = new ObjectMapper();

        // 搜索数据
        SearchRequestBuilder searchRequestBuilder = client
                .prepareSearch("blog2").setTypes("article")
//.setQuery(QueryBuilders.termQuery("content", "搜索"));
                .setQuery(QueryBuilders.multiMatchQuery("搜索", "title","content"));
        //.setQuery(QueryBuilders.queryStringQuery("搜索").field("title").field("content"))

        /**
         *  配置应用高亮
         */

        // 高亮定义
        searchRequestBuilder.addHighlightedField("content").addHighlightedField("title"); // 设置高亮的字段; // 对content和title字段进行高亮
        searchRequestBuilder.setHighlighterPreTags("<em>"); // 前置元素
        searchRequestBuilder.setHighlighterPostTags("</em>");// 后置元素
        // 设置摘要大小（高亮出现最多的区域）
        searchRequestBuilder.setHighlighterFragmentSize(100);

        SearchResponse searchResponse = searchRequestBuilder.get();

        SearchHits hits = searchResponse.getHits(); // 获取命中次数，查询结果有多少对象
        System.out.println("查询结果有：" + hits.getTotalHits() + "条");
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next(); // 每个查询对象

            /**
             * 对结果的高亮片段做拼接处理，替换原有内容
             */

            Article article = objectMapper.readValue(searchHit.getSourceAsString(), Article.class);
            // 获取高亮的结果，并把高亮的结果封装到Article中
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            // 获取title中的文字高亮
            HighlightField titleHighlightField = highlightFields.get("title");
            // 如果存在高亮的结果，此时titleHighlightField对象不为null，组织高亮的结果，如果对象为null，说明没有高亮的效果
            if(titleHighlightField!=null){
                Text[] titleFragments = titleHighlightField.fragments();
                String title = "";
                if(titleFragments!=null && titleFragments.length>0){
                    for(Text text:titleFragments){
                        title += text;
                    }
                    article.setTitle(title);
                }
            }
            // 获取content中的文字高亮
            // 如果存在高亮的结果，此时contentHighlightField对象不为null，组织高亮的结果，如果对象为null，说明没有高亮的效果
            HighlightField contentHighlightField = highlightFields.get("content");
            if(contentHighlightField!=null){
                Text[] contentFragments = contentHighlightField.fragments();
                String content = "";
                if(contentFragments!=null && contentFragments.length>0){
                    for(Text text:contentFragments){
                        content += text;
                    }
                    article.setContent(content);
                }
            }

            System.out.println(article);
        }

        // 关闭连接
        client.close();
    }
}

