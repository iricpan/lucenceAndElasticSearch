package cn.bd.lucence;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class LucenceUpdate {
    /**
     * 更新索引库
     * @throws Exception
     * 更新文档
     * 本质先删除再添加
     * 先删除所有满足条件的文档，再创建文档
     * 文档编号重新进行编排，后续的数据的文档编号前移，修改的数据的文档编号放置到最后
     * 因此，更新索引通常要根据唯一字段
     */
    @Test
    public void testUpdate() throws Exception{
        // 创建文档对象
        Document document = new Document();
        document.add(new StringField("id", "1", Field.Store.YES));
        // TextField：创建索引并提供分词，StringField创建索引但不分词
        document.add(new TextField("title", "Dubbo是什么", Field.Store.YES));
        document.add(new TextField("content", "Dubbo是阿里巴巴分布式框架，发誓学好它", Field.Store.YES));

        // 索引库对象
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 索引写入器配置对象
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        // 索引写入器对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);

        // 执行更新操作
        indexWriter.updateDocument(new Term("id", "1"), document);
        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
    }
}
