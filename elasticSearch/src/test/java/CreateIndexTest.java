import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;

import java.net.InetAddress;

public class CreateIndexTest {
    /**
     * 创建索引
     */
    @Test
    public void createIndex() throws Exception{
        //创建client连接对象
        Client client = TransportClient
                .builder()
                .build()
                .addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName("127.0.0.1"), 9300
                ));
        client.admin().indices().prepareCreate("blog2").get();
        client.close();
    }

    /**删除索引*/
    @Test
    public void deleteIndex() throws Exception {
        // 创建Client连接对象
        TransportClient client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        client.admin().indices().prepareDelete("blog2").get();
        client.close();
    }
}
