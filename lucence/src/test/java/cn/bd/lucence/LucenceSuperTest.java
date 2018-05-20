package cn.bd.lucence;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class LucenceSuperTest {
    /**
     * 文字高亮
     * @throws Exception
     */
    @Test
    public void testHighLighter() throws Exception{
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 索引读取工具类
        IndexReader indexReader = DirectoryReader.open(directory);
        // 索引查询对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 查询解析器（只搜索一个字段）
//		QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
        // 查询解析器（多字段检索）
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title","content"}, new IKAnalyzer());
        Query query = queryParser.parse("框架");
        TopDocs topDocs = indexSearcher.search(query, 10);// 只返回前10条记录

        // 设置文字高亮
        // html标签格式化
        Formatter formatter = new SimpleHTMLFormatter("<em>","</em>");
        // 封装查询条件（高亮显示根据查询条件显示）
        Scorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);

        // 查询的结果数据
        System.out.println("总记录数是："+topDocs.totalHits);
        // 查询的结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // 遍历结果
        if(scoreDocs!=null && scoreDocs.length>0){
            for(ScoreDoc scoreDoc:scoreDocs){
                // 得分
                System.out.println("得分:"+scoreDoc.score);
                // 文档编号
                int docId = scoreDoc.doc;
                System.out.println("文档的文档编号（默认从0开始）："+docId);
                // 根据文档编号获取文档
                Document document = indexReader.document(docId);

                // 要在title字段和content字段上添加高亮
                String title = document.get("title");
                String content = document.get("content");

                String highlightTitle = highlighter.getBestFragment(new IKAnalyzer(), "title", title);// 参数一：分词器；参数二：表示从哪个字段上获取高亮（一次只能从一个字段上获取），参数三：需要设置高亮的文本内容
                // 如果highlightTitle为null，表示该字段没有高亮的结果；如果highlightTitle不为null，表示该字段存在高亮的结果，并返回
                if(highlightTitle!=null){
                    title = highlightTitle;
                }
                String highlightContent = highlighter.getBestFragment(new IKAnalyzer(), "content", content);
                if(highlightContent!=null){
                    content = highlightContent;
                }

                System.out.println("id:"+document.get("id"));
                System.out.println("title:"+title);
                System.out.println("content:"+content);
                System.out.println("---------------------------------------------------");
            }
        }
    }

    /**
     * 排序
     * @throws Exception
     */
    @Test
    public void testSort() throws Exception{
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 索引读取工具类
        IndexReader indexReader = DirectoryReader.open(directory);
        // 索引查询对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 查询解析器（只搜索一个字段）
//		QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
        // 查询解析器（多字段检索）
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title","content"}, new IKAnalyzer());
        Query query = queryParser.parse("谷歌");
        // 排序
        // 默认按照升序排列（默认值false），true表示降序，此时如果添加排序的操作，得分将失效
        Sort sort = new Sort(new SortField("id", SortField.Type.LONG,true));
        TopDocs topDocs = indexSearcher.search(query, 10,sort);// 只返回前10条记录

        // 设置文字高亮
        // html标签格式化
        Formatter formatter = new SimpleHTMLFormatter("<em>","</em>");
        // 封装查询条件（高亮显示根据查询条件显示）
        Scorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);

        // 查询的结果数据
        System.out.println("总记录数是："+topDocs.totalHits);
        // 查询的结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // 遍历结果
        if(scoreDocs!=null && scoreDocs.length>0){
            for(ScoreDoc scoreDoc:scoreDocs){
                // 得分
                System.out.println("得分:"+scoreDoc.score);
                // 文档编号
                int docId = scoreDoc.doc;
                System.out.println("文档的文档编号（默认从0开始）："+docId);
                // 根据文档编号获取文档
                Document document = indexReader.document(docId);

                // 要在title字段和content字段上添加高亮
                String title = document.get("title");
                String content = document.get("content");

                String highlightTitle = highlighter.getBestFragment(new IKAnalyzer(), "title", title);// 参数一：分词器；参数二：表示从哪个字段上获取高亮（一次只能从一个字段上获取），参数三：需要设置高亮的文本内容
                // 如果highlightTitle为null，表示该字段没有高亮的结果；如果highlightTitle不为null，表示该字段存在高亮的结果，并返回
                if(highlightTitle!=null){
                    title = highlightTitle;
                }
                String highlightContent = highlighter.getBestFragment(new IKAnalyzer(), "content", content);
                if(highlightContent!=null){
                    content = highlightContent;
                }

                System.out.println("id:"+document.get("id"));
                System.out.println("title:"+title);
                System.out.println("content:"+content);
                System.out.println("---------------------------------------------------");
            }
        }
    }

    /**
     * 分页
     * @throws Exception
     */
    @Test
    public void testPage() throws Exception{
        // 分页参数（用于逻辑分页）
        int pageNo = 1; // 当前页
        int pageSize = 2; // 每页最多显示的记录数
        int start = (pageNo-1)*pageSize;  // 当前页从第几条开始
        int end = start+pageSize;  // 当前页到第几条结束

        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 索引读取工具类
        IndexReader indexReader = DirectoryReader.open(directory);
        // 索引查询对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 查询解析器（只搜索一个字段）
//		QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
        // 查询解析器（多字段检索）
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title","content"}, new IKAnalyzer());
        Query query = queryParser.parse("谷歌");

        TopDocs topDocs = indexSearcher.search(query, 10);// 只返回前10条记录

        // 设置文字高亮
        // html标签格式化
        Formatter formatter = new SimpleHTMLFormatter("<em>","</em>");
        // 封装查询条件（高亮显示根据查询条件显示）
        Scorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);

        // 查询的结果数据
        System.out.println("总记录数是："+topDocs.totalHits);
        // 查询的结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        // 逻辑分页
        // 遍历结果
        if(scoreDocs!=null && scoreDocs.length>0){
            for(int i=start;i<end;i++){
                // 如果循环的i大于等于总记录数，退出循环，防止数组下标越界
                if(i>=topDocs.totalHits){
                    break;
                }
                ScoreDoc scoreDoc = scoreDocs[i];
                // 得分
                System.out.println("得分:"+scoreDoc.score);
                // 文档编号
                int docId = scoreDoc.doc;
                System.out.println("文档的文档编号（默认从0开始）："+docId);
                // 根据文档编号获取文档
                Document document = indexReader.document(docId);

                // 要在title字段和content字段上添加高亮
                String title = document.get("title");
                String content = document.get("content");

                String highlightTitle = highlighter.getBestFragment(new IKAnalyzer(), "title", title);// 参数一：分词器；参数二：表示从哪个字段上获取高亮（一次只能从一个字段上获取），参数三：需要设置高亮的文本内容
                // 如果highlightTitle为null，表示该字段没有高亮的结果；如果highlightTitle不为null，表示该字段存在高亮的结果，并返回
                if(highlightTitle!=null){
                    title = highlightTitle;
                }
                String highlightContent = highlighter.getBestFragment(new IKAnalyzer(), "content", content);
                if(highlightContent!=null){
                    content = highlightContent;
                }

                System.out.println("id:"+document.get("id"));
                System.out.println("title:"+title);
                System.out.println("content:"+content);
                System.out.println("---------------------------------------------------");
            }
        }
    }
}
