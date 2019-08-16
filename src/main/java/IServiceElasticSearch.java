public interface IServiceElasticSearch {

    public boolean saveUser(User user);
    public String getUserToken(String username, String password);
    public boolean checkIfValidToken(String username, String token);

}
