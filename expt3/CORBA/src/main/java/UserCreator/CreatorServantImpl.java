package UserCreator;

/**
 * Created by snow on 2018/5/17.
 */
public class CreatorServantImpl extends CreatorPOA {
    public boolean login(String username, String password) {
        System.out.println("Login Successfully!");
        System.out.println(username + " " + password);
        return false;
    }

    public boolean register(String username, String password) {
        System.out.println("Register Successfully!");
        System.out.println(username + " " + password);
        return false;
    }
}
