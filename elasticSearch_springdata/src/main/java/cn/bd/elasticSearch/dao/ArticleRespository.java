package cn.bd.elasticSearch.dao;

import cn.bd.elasticSearch.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ArticleRespository extends ElasticsearchRepository<Article,Integer> {
    /**
     * 条件查询
     * @param condition
     * @return
     */
    List<Article> findByTitle(String condition);

    /**
     * 条件查询,并实现分页
     * @param conditon
     * @param pageable
     * @return
     */
    Page<Article> findByTitle(String conditon,Pageable pageable);
}
