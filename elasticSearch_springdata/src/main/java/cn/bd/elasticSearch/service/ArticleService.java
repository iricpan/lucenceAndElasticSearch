package cn.bd.elasticSearch.service;

import cn.bd.elasticSearch.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {
    /**
     * 新增
     * @param article
     */
    public void save(Article article);

    /**
     * 删除
     * @param article
     */
    public void delete(Article article);

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Article findOne(Integer id);

    /**
     * 查询所有
     * @return
     */
    public Iterable<Article> findAll();

    /**
     * 查询所有，并实现分页效果
     * @param pageable
     * @return
     */
    public Page<Article> findAll(Pageable pageable);

    /**
     * 条件查询
     * 根据标题查询
     */
    public List<Article> findByTitle(String condition);

    /**
     * 条件查询,并实现分页
     */
    public Page<Article> findByTitle(String condition,Pageable pageable);
}
