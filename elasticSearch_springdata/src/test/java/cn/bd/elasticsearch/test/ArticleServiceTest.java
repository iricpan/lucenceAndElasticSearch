package cn.bd.elasticsearch.test;

import cn.bd.elasticSearch.entity.Article;
import cn.bd.elasticSearch.service.ArticleService;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private Client client;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建索引和映射
     */
    @Test
    public void testCreateIndex(){
        elasticsearchTemplate.createIndex(Article.class);
        elasticsearchTemplate.putMapping(Article.class);
    }
    /**
     * 测试保存
     */
    @Test
    public void  save(){
        Article article = new Article();
        article.setId(1001);
        article.setTitle("elasticSearch 3.0版本发布");
        article.setContent("ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
        articleService.save(article);
    }

    /**
     * 测试更新
     */
    @Test
    public void update(){
        Article article = new Article();
        article.setId(1001);
        article.setTitle("elasticSearch 3.0版本发布>>>>更新");
        article.setContent("ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
        articleService.save(article);
    }
    /**
     * 删除
     */
    @Test
    public void delete(){
        Article article = new Article();
        article.setId(1001);
        articleService.delete(article);
    }

    /**
     * 使用id查询
     */
    @Test
    public void findOne(){
        Integer id = 1001;
        Article article = articleService.findOne(id);
        System.out.println(article);
    }
    /**
     * 批量插入
     */
    @Test
    public void save100(){
        for(int i = 1; i<=100;i++){
            Article article = new Article();
            article.setId(i);
            article.setTitle(i+"elasticSearch 3.0版本发布..，更新");
            article.setContent(i+"ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
            articleService.save(article);
        }
    }
    /**
     * 排序查询
     */
    @Test
    public void findAllSort(){
        Iterable<Article> list = articleService.findAll();
        for (Article article : list) {
            System.out.println(article);
        }
    }

    /**
     * 分页查询
     */
    @Test
    public void findAllPage(){
        //当前页
        int page = 2;
        //当前页显示的记录数
        int size = 10;

        Pageable pageable = new PageRequest(page - 1, size, new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        Page<Article> pageData = articleService.findAll(pageable);
        for (Article article : pageData.getContent()) {
            System.out.println(article);
        }
    }

    /**
     * 条件查询
     */
    @Test
    public void findByCondition(){
        String conditon ="版本";
        List<Article> list = articleService.findByTitle(conditon);
        for (Article article : list) {
            System.out.println(article);
        }
    }

    /**
     * 条件分页查询
     */
    @Test
    public void findByTitlePage(){
        String condition = "版本";
        Pageable pageable = new PageRequest(0, 10, Sort.Direction.DESC, "id");
        Page<Article> page = articleService.findByTitle(condition, pageable);
        for (Article article : page.getContent()) {
            System.out.println(article);
        }
    }
}
