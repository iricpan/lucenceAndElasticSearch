package cn.bd.elasticSearch.service;

import cn.bd.elasticSearch.dao.ArticleRespository;
import cn.bd.elasticSearch.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService{
    @Autowired
    private ArticleRespository articleRespository;

    public void save(Article article) {
        articleRespository.save(article);
    }

    public void delete(Article article) {
        articleRespository.delete(article);
    }

    public Article findOne(Integer id) {
        return articleRespository.findOne(id);
    }

    public Iterable<Article> findAll() {
        //查询所有
//        Iterable<Article> iterable = articleRespository.findAll();
        //排序
        Iterable<Article> iter = articleRespository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        return iter;
    }

    public Page<Article> findAll(Pageable pageable) {
        return articleRespository.findAll(pageable);
    }

    public List<Article> findByTitle(String condition){
        return articleRespository.findByTitle(condition);
    }

    public Page<Article> findByTitle(String conditon,Pageable pageable){
        return articleRespository.findByTitle(conditon,pageable);
    }
}
