public interface IServiceElasticSearch {

//    public boolean saveUser(String username, String password);
    public String getUserToken(String username, String password);
    public boolean checkIfValidToken(String username, String token);

}
