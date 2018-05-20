package cn.bd.lucence;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateIndex {
    @Test
    public void indexCreate() throws IOException {
        // 创建文档对象
        Document document = new Document();
        // 添加字段，参数Field是一个接口，要new实现类的对象(StringField, TextField)
        // StringField的实例化需要3个参数：1-字段名，2-字段值，3-是否保存到文档，Store.YES存储，NO不存储
        document.add(new StringField("id", "1", Field.Store.YES));
        // TextField：创建索引并提供分词，StringField创建索引但不分词
      /*  document.add(new StringField("title", "Dubbo是什么", Field.Store.YES));
        document.add(new StringField("content", "Dubbo是阿里巴巴分布式框架", Field.Store.YES));*/
        document.add(new TextField("title", "Dubbo是什么", Field.Store.YES));
        document.add(new TextField("content", "Dubbo是阿里巴巴分布式框架", Field.Store.YES));
        document.add(new StoredField("url","www.dubbo.com"));

        // 创建目录对象，指定索引库的存放位置；FSDirectory文件系统；RAMDirectory内存
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 创建分词器对象
        Analyzer analyzer = new StandardAnalyzer();
        // 创建索引写入器配置对象，第一个参数版本VerSion.LATEST,第二个参数分词器
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, analyzer);
        //设置打开模式,参数OpenMode为打开模式,枚举类,默认是APPEND(表示追加索引),CREATE(表示覆盖索引)
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // 创建索引写入器
        IndexWriter indexWriter = new IndexWriter(directory , conf);
        // 向索引库写入文档对象
        indexWriter.addDocument(document);
        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
    }

    /**
     * 批量插入
     */
    @Test
    public void createDocument_list() throws Exception{
        List<Document> docs = new ArrayList<Document>();
        // 创建文档对象
        Document document1 = new Document();
        document1.add(new StringField("id", "1", Field.Store.YES));
        document1.add(new TextField("title", "谷歌地图之父跳槽FaceBook", Field.Store.YES));
        document1.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document1);
        // 创建文档对象
        Document document2 = new Document();
        document2.add(new StringField("id", "2", Field.Store.YES));
        document2.add(new TextField("title", "谷歌地图之父加盟FaceBook", Field.Store.YES));
        document2.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document2);
        // 创建文档对象
        Document document3 = new Document();
        document3.add(new StringField("id", "3", Field.Store.YES));
        document3.add(new TextField("title", "谷歌地图创始人拉斯离开谷歌加盟Facebook", Field.Store.YES));
        document3.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document3);
        // 创建文档对象
        Document document4 = new Document();
        document4.add(new StringField("id", "4", Field.Store.YES));
        document4.add(new TextField("title", "谷歌地图之父跳槽Facebook与Wave项目取消有关", Field.Store.YES));
        document4.add(new TextField("content", "dubbo是阿里巴巴的分布式框架", Field.Store.YES));
        docs.add(document4);

        // 定义索引库的目录类（D:\indexDir）
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 定义分词器（单字分词）
        Analyzer analyzer = new StandardAnalyzer();
        // 定义索引写入器的配置对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);//表示覆盖索引，即重新创建数据
        // 创建一个文档写入器
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        // 新增文档数据
        indexWriter.addDocuments(docs);
        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
    }
}
