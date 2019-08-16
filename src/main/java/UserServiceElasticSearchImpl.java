import com.google.gson.Gson;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.PostDataRequest;
import org.elasticsearch.client.ml.PostDataResponse;
import org.elasticsearch.client.ml.job.process.DataCounts;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserServiceElasticSearchImpl implements IServiceElasticSearch {

//    crear usuario String uniqueID = UUID.randomUUID().toString();

    @Override
    public boolean saveUser(User user) {
        RestHighLevelClient client = CreateElasticSearchConnection.makeConnection();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username",user.getUsername());
        dataMap.put("password",user.getPassword());

        IndexRequest indexRequest = new IndexRequest("users").source(dataMap);
        try {
            client.index(indexRequest,RequestOptions.DEFAULT);
            return true;
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        try {
            CreateElasticSearchConnection.closeConnection(client);
        } catch (IOException e) {
            System.out.println(e.getMessage());;

        }
        return false;






//        PostDataRequest.JsonBuilder jsonBuilder = new PostDataRequest.JsonBuilder();
//        jsonBuilder.addDoc("username : " + user.getUsername() + ", password :" + user.getPassword());
//        jsonBuilder.addDoc(("{users:1000}"));
//        PostDataRequest postDataRequest = new PostDataRequest("test-post-data", jsonBuilder);
//
//        try {
//            PostDataResponse postDataResponse = client.machineLearning().postData(postDataRequest, RequestOptions.DEFAULT);
//
//            DataCounts dataCounts = postDataResponse.getDataCounts();
//
//            System.out.println(dataCounts.toString());
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            CreateElasticSearchConnection.closeConnection(client);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());;
//            e.printStackTrace();
//
//        }
//
//        return false;
    }

    @Override
    public String getUserToken(String username, String password) {

        User user = getUserFromElasticSearch(username);

        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            System.out.println("Authentification: SUCCEDED");
            return user.getToken();
        } else {
            System.out.println("wrong password or user not found");
            // throw exception 404 or forbidden
            return "wrong password or user not found";
        }

    }

    @Override
    public boolean checkIfValidToken(String username, String token) {
        User user = getUserFromElasticSearch(username);

        if (user != null && user.getUsername().equals(username) && user.getToken().equals(token)) {
            return true;
        } else {
            return false;
        }
    }

    private User getUserFromElasticSearch(String username) {
        RestHighLevelClient client = CreateElasticSearchConnection.makeConnection();

        SearchRequest searchRequest = new SearchRequest("users");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("username", username));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();

            if (searchHits.length < 1) {
                return null;
            } else {
                return new Gson().fromJson(searchHits[0].getSourceAsString(), User.class);
            }

        } catch (ElasticsearchException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
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
