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

            User user = new Gson().fromJson( searchHits[0].getSourceAsString(), User.class);

//            List<Site> sites = new ArrayList<>() ;
//            for (SearchHit hit : searchHits) {
//                sites.add(new Gson().fromJson( hit.getSourceAsString(), Site.class));
//            }

            System.out.println(user.getPassword());

            return user.getToken();


        } catch(ElasticsearchException e) {
            System.out.println(e.getMessage());
        } catch (java.io.IOException ex){
            System.out.println(ex.getMessage());
        }

        try {
            CreateElasticSearchConnection.closeConnection(client);
        } catch (IOException e) {
            System.out.println(e.getMessage());;
        }
        return null;
    }
}
