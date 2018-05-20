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
import java.io.IOException;

public class LucenceDeleteTest {

    @Test
    public void testDelete() throws IOException {

        // 创建目录对象
        Directory directory = FSDirectory.open(new File("D:\\indexDir"));
        // 创建索引写入器配置对象
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        // 创建索引写入器对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);

        // 执行删除操作(根据词条)，要求id字段必须是字符串类型
        // indexWriter.deleteDocuments(new Term("id", "1"));
        // 根据查询条件删除
        // indexWriter.deleteDocuments(NumericRangeQuery.newLongRange("id", 2l, 4l, true, false));
        // 删除所有
        indexWriter.deleteAll();

        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
    }
}
