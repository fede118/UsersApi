public interface IUserService {

    public TokenResponse logUser(String username, String password);
    public Site[] getSites();
    public Category[] getCategories(String id);

}
