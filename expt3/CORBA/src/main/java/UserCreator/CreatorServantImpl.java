package UserCreator;

import app.AppServant;

import java.util.Hashtable;

/**
 * Created by snow on 2018/5/17.
 */
public class CreatorServantImpl extends CreatorPOA {
    private Hashtable<String, String> users = new Hashtable<String, String>();

    public boolean login(String username, String password) {
       if (!users.containsKey(username)){
           System.err.println("该用户名不存在！");
           return false;
       }else if (!users.get(username).equals(password)){
           System.err.println("密码错误！");
           return false;
       }
       return true;
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            System.err.println("该用户名已注册！");
            return false;
        }

        //create a CORBA object
        users.put(username, password);
        AppServant.createNameServceOfUser(username);

        System.out.println("注册成功，已成功登入该账户！");
        return true;
    }
}
