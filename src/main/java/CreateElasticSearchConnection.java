import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class CreateElasticSearchConnection {

    //private static ObjectMapper objectMapper = new ObjectMapper();

    public static RestHighLevelClient makeConnection() {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        return client;
    }

    public static void closeConnection(RestHighLevelClient client) throws IOException {
        client.close();
    }
}

