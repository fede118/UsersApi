import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystemNotFoundException;

public class UserServiceSitesApiImpl implements IUserService {
    private static final String SITES_URL = "http://localhost:8083/sites";
    private static final String CATEGORIES_URL = "http://localhost:8083/sites/";


    public TokenResponse logUser(String username, String password) {
        return null;
    }

    public Site[] getSites() {
        BufferedReader in = getBufferedReader(SITES_URL);

        Gson gson = new Gson();
        Site[] sites = gson.fromJson(in, Site[].class);

        return sites;
    }

    public Category[] getCategories(String id) {
        try {
            BufferedReader in = getBufferedReader(CATEGORIES_URL + id + "/categories");

            Gson gson = new Gson();
            if (in != null) {
                Category[] categories = gson.fromJson(in, Category[].class);
                System.out.println("[userService] getCategories = " + categories[0].toString());
                return categories;
            } else {
                return new Category[0];
            }

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return new Category[0];
        }
    }

    private static BufferedReader getBufferedReader(String url) {
        URL urlSites = null;
        try {
            urlSites = new URL(url);

            try {

                URLConnection urlConnection = urlSites.openConnection();

                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (urlConnection instanceof HttpURLConnection) {

                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    return in;
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
