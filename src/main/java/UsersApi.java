import com.google.gson.Gson;
import spark.Spark;

import static spark.Spark.*;

public class UsersApi {

    static final IUserService service = new UserServiceSitesApiImpl();
    static final IServiceElasticSearch elasticSearchService = new UserServiceElasticSearchImpl();;

    public static void main(String[] args) {

        Spark.port(8084);

        post("/users", (req, res) -> {
            res.type("application/json");

            if (req.body().isEmpty() || req.body() == null) {
                res.status(403);
                return "please provide username and password";
            } else {
                User user = new Gson().fromJson(req.body(), User.class);
                if (elasticSearchService.saveUser(user)) {
                    res.status(201);
                    return true;
                }

                res.status(500);
                return false;

            }
        });

        post("/users/login", (req, res) -> {
            res.type("application/json");

            if (req.body().isEmpty() || req.body() == null) {
                res.status(403);
                return "please provide username and password";

            } else {
                User user = new Gson().fromJson(req.body(), User.class);

                String token = elasticSearchService.getUserToken(user.getUsername(), user.getPassword());

                TokenResponse tokenResponse = new TokenResponse(user.getUsername(), token);

                System.out.println("[POST] /login: " + tokenResponse.getToken());

                res.status(200);
                return new Gson().toJsonTree(tokenResponse);
            }
        });

        get("/users/sites", (req, res) -> {
            res.type("application/json");

            if (validateToken(req.queryParams("username"), req.queryParams("token"))) {
                Site[] sites = service.getSites();
                res.status(200);
                return new Gson().toJsonTree(sites);
            } else {
                res.status(403);
                return new Gson().toJson("invalid username or token");
            }
        });

        get("/users/sites/:id/categories", (req, res) -> {
            res.type("application/json");

            if (validateToken(req.queryParams("username"), req.queryParams("token"))) {
                String id = req.params("id");
//                System.out.println("[GET] /sites/:id/categories =>" + id);

                Category[] categories = service.getCategories(id);
                if (categories.length > 0) {
                    res.status(200);
                    return new Gson().toJsonTree(categories);
                } else {
                    res.status(404);
                    return new Gson().toJson("Couldn't find categories for that site/Id");
                }

            } else {
                res.status(403);
                return new Gson().toJson("invalid username or token");
            }

        });
    }

    private static boolean validateToken(String username, String token) {
        if (username.isEmpty() || username == null) {
            return false;
        } else if (token.isEmpty() || token == null) {
            return false;
        } else {
            return elasticSearchService.checkIfValidToken(username, token);
        }
    }
}
