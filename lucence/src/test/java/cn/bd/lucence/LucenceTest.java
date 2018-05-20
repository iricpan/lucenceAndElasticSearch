package cn.bd.lucence;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LucenceTest {
    //创建索引
    @Test
    public void testCreateDocumnent() throws Exception{
        //创建文档对象（1条记录)
        Document document = new Document();
        // StringField：给一个字段的名称和一个字段的值，Store.YES：是否存储，表示存放的数据是否存放到数据区域
        // StringField：表示不分词建立索引（id字段可以不分词）
        document.add(new StringField("id","3",Field.Store.YES));
        // TextField：表示一定分词建立索引（title、content字段需要分词）
        document.add(new TextField("title","Dubbo是什么",Field.Store.YES));
        document.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        //创建目录对象,指定一个索引库的位置,FSDirectory文件系统;RAMDirectory内存
        FSDirectory directory = FSDirectory.open(new File("D:\\indexDir"));
        //luncence提供的分词器,创建分词器对象
        IKAnalyzer analyzer = new IKAnalyzer();
        //指定一个写入器的配置对象,第一个参数版本Version.LATEST,第一个参数分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        //indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //创建文档写入器
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        //将文档写入到索引库
        indexWriter.addDocument(document);
        //提交
        indexWriter.commit();
        //关闭
        indexWriter.close();
    }

    //搜索索引
    @Test
    public void testSearcher() throws Exception{
        //初始化索引库对象
        FSDirectory directory = FSDirectory.open(new File("D:\\indexDir"));
        //索引读取工具
        DirectoryReader indexReader = DirectoryReader.open(directory);
        //索引搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询解析器对象
        QueryParser parser = new QueryParser("content", new IKAnalyzer());
        //创建查询对象
        Query query = parser.parse("框架");
//        Query query = parser.parse("阿里巴巴框架");
        //执行搜索操作,返回值topDocs包含命中数,得分文档
        TopDocs topDocs = indexSearcher.search(query, 10);
        //打印命中数
        System.out.println("一共命中:"+topDocs.totalHits+"条记录");
        //获得的分文档数组对象,得分文档对象包含得分和文档编号
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("得分:"+scoreDoc.score);
            //文档的编号
            int doc = scoreDoc.doc;
            System.out.println("编号:"+doc);
            //获取文档对象,通过索引读取工具
            Document document = indexReader.document(doc);
            System.out.println("id:"+document.get("id"));
            System.out.println("title:"+document.get("title"));
            System.out.println("content:"+document.get("content"));
            System.out.println("****************************************");
        }
    }

    /**
     * 抽取公用的搜索方法
     */
    public void search(Query query) throws Exception {
        // 创建目录对象
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 索引的读取对象
        IndexReader indexReader = DirectoryReader.open(directory);
        // 索引的搜索工具
        IndexSearcher searcher = new IndexSearcher(indexReader);
        // 尝试查询，1-查询对象，2-查询的条数
        // 返回的是前n条文档的对象，topDocs：包含文档的总条数，文档的得分数组
        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("搜索的命中总条数：" + topDocs.totalHits);
        // 获取得分文档的数组，得分文档包含文档编号以及得分
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("得分："+scoreDoc.score);
            // 文档的编号
            int doc = scoreDoc.doc;
            System.out.println("编号："+doc);
            // 获取文档对象，通过索引读取工具
            Document document = indexReader.document(doc);
            System.out.println("id:"+document.get("id"));
            System.out.println("title:"+document.get("title"));
            System.out.println("content:"+document.get("content"));
            System.out.println("*******************************************");
        }
    }

    /**
     * 词条查询
     * 查询条件必须是最小粒度不可再分割的内容
     * 场景：不可分割的字段可以采用，比如id
     * 缺点：只能查询一个词，例如可以查询"谷歌"，不能查询"谷歌地图"
     * @throws Exception
     */
    @Test
    public void testTermSearch() throws Exception{
        //创建查询对象
        TermQuery query = new TermQuery(new Term("title", "什么"));
        //执行搜索操作
        search(query);
    }

    /**
     * 通配符查询
     * *表示多个字符
     * ?表示1个字符
     * @throws Exception
     */
    @Test
    public void testWildCardQuery() throws Exception{
        // 查询条件对象（通配符
        // ?：通配一个字符
        // *：通配多个字符
        WildcardQuery query = new WildcardQuery(new Term("title", "什么"));
        search(query);
    }

    /**
     * 模糊查询（相似度查询）
     * 查询条件对象（模糊查询
     参数：1-词条，查询字段及关键词，关键词允许写错；
     2-允许写错的最大编辑距离，并且不能大于2（0~2）
     最大编辑距离：dubbo-->dxbbx需要编辑的次数，包括大小写
     * @throws Exception
     */
    @Test
    public void testFuzzyQuery()throws Exception{
        FuzzyQuery query = new FuzzyQuery(new Term("title", "dxbbx"));
        search(query);
    }

    /**
     * 范围查询
     * @throws Exception
    查询条件对象（数值范围查询
    查询非String类型的数据或者说是一些继承Numeric类的对象的查询
    1-字段；2-最小值；3-最大值；4-是否包含最小值；5-是否包含最大值
     */
    @Test
    public void testNumericRangeQuery() throws Exception{
        Query query = NumericRangeQuery.newLongRange("id", 2l, 4l, true, true);
        search(query);
    }

    /**
     * 新建数据
     */
    @Test
    public void testCreaterLong() throws IOException {

        // 创建文档对象集合
        List<Document> docs = new ArrayList<Document>();

        // 创建文档对象
        Document document1 = new Document();
        document1.add(new LongField("id", 1, Field.Store.YES));
        document1.add(new TextField("title", "谷歌地图之父跳槽FaceBook", Field.Store.YES));
        document1.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document1);
        // 创建文档对象
        Document document2 = new Document();
        document2.add(new LongField("id", 2, Field.Store.YES));
        document2.add(new TextField("title", "谷歌地图之父加盟FaceBook", Field.Store.YES));
        document2.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document2);
        // 创建文档对象
        Document document3 = new Document();
        document3.add(new LongField("id", 3, Field.Store.YES));
        document3.add(new TextField("title", "谷歌地图创始人拉斯离开谷歌加盟Facebook", Field.Store.YES));
        document3.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document3);
        // 创建文档对象
        Document document4 = new Document();
        document4.add(new LongField("id", 4, Field.Store.YES));
        document4.add(new TextField("title", "谷歌地图之父跳槽Facebook与Wave项目取消有关", Field.Store.YES));
        document4.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document4);
        // 创建文档对象
        Document document5 = new Document();
        document5.add(new LongField("id", 5, Field.Store.YES));
        document5.add(new TextField("title", "谷歌地图之父拉斯加盟社交网站Facebook", Field.Store.YES));
        document5.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document5);

        // 创建文档对象
        Document document6 = new Document();
        document6.add(new LongField("id", 6, Field.Store.YES));
        document6.add(new TextField("title", "谷歌地图之父拉斯加盟Facebook", Field.Store.YES));
//        document6.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        TextField textField = new TextField("content", "dubbo是阿里巴巴的分布式框架,很有用", Field.Store.YES);
        textField.setBoost(2f);//之前得分的2倍
        document6.add(textField);
        docs.add(document6);

        // 索引库对象
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        Analyzer analyzer = new IKAnalyzer();
        // 创建索引写入器配置对象，1-版本，2-分词器：标准分词器
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, analyzer);

        // 创建索引写入器对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);

        // 执行写入操作
        indexWriter.addDocuments(docs);
        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
    }

    /**
     * BooleanQuery（组合查询）
     该查询可以存放多个查询条件，类似于sql语句的  and 或者 or
     Occur.MUST：必须满足，相当于sql语句 and（交集）
     Occur.SHOULD：应该满足，相当于sql语句 or（并集）
     Occur.MUST_NOT：不必须满足，相当于sql语句 not
     */
    @Test
    public void testBooleanQuery() throws Exception {

        Query query1 = NumericRangeQuery.newLongRange("id", 2l, 4l, true, true);
        Query query2 = NumericRangeQuery.newLongRange("id", 0l, 3l, true, true);

        // boolean查询本身没有查询条件，它可以组合其他查询
        BooleanQuery query = new BooleanQuery();
        // 交集： Occur.MUST + Occur.MUST
        // 并集：Occur.SHOULD + Occur.SHOULD
        // 非：Occur.MUST_NOT
        query.add(query1, BooleanClause.Occur.SHOULD);
        query.add(query2, BooleanClause.Occur.SHOULD);

        search(query);
    }
}
