import com.google.gson.Gson;
import spark.Spark;

import static spark.Spark.*;

public class UsersApi {

    final IUserService service = new UserServiceSitesApiImpl();

    public static void main(String[] args) {

        final IUserService service = new UserServiceSitesApiImpl();

        final IServiceElasticSearch elasticSearchService = new UserServiceElasticSearchImpl();

        Spark.port(8080);

        post("/login", (req, res) -> {
            res.type("application/json");

            if (req.body().isEmpty() || req.body() == null) {
                return "please provide username and password";

            } else {
                User user = new Gson().fromJson(req.body(), User.class);

                String token = elasticSearchService.getUserToken(user.getUsername(), user.getPassword());
                TokenResponse tokenResponse = new TokenResponse(user.getUsername(), token);

                System.out.println("TOKKEEEN BACK FROM ELASTIC: " + tokenResponse.getToken());

                res.status(200);
                return new Gson().toJsonTree(tokenResponse);
            }
        });

        get("/sites", (req, res) -> {
            res.type("application/json");

            if (req.queryParams("username").isEmpty() || req.queryParams("username") == null) {
                return new Gson().toJson("please provide username");
            } else if (req.queryParams("password").isEmpty() || req.queryParams("password") == null) {
                return new Gson().toJson("please provide password");
            }

            String username = req.queryParams("username");
            String token = req.queryParams("token");

            System.out.println("username from params: " + username);
            System.out.println("token from params: " + token);

            Site[] sites = service.getSites();
            res.status(200);
            return new Gson().toJsonTree(sites);
        });

        get("/sites/:id/categories", (req, res) -> {
            res.type("application/json");

            String id = req.params("id");

            String token = req.queryParams("token");
            System.out.println("TOKEN: " + token);

            Category[] categories = service.getCategories(id);
            res.status(200);
            return new Gson().toJsonTree(categories);
        });

    }
}
