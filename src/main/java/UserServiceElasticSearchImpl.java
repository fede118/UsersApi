import com.google.gson.Gson;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserServiceElasticSearchImpl implements IServiceElasticSearch {

//    crear usuario String uniqueID = UUID.randomUUID().toString();

    @Override
    public String getUserToken(String username, String password) {
        RestHighLevelClient client = CreateElasticSearchConnection.makeConnection();

        SearchRequest searchRequest = new SearchRequest("users");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("username", username));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();

            if (searchHits.length < 1) {
                return "not found 404";
            }

            User user = new Gson().fromJson(searchHits[0].getSourceAsString(), User.class);

            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("Authentification: SUCCEDED");
                return user.getToken();
            } else {
                System.out.println("wrong password or user not found");
                // throw exception 404 or forbidden
                return "wrong password or user not found";
            }


        } catch(ElasticsearchException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (java.io.IOException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            CreateElasticSearchConnection.closeConnection(client);
        } catch (IOException e) {
            System.out.println(e.getMessage());;
            e.printStackTrace();
        }
        return null;
    }

}
