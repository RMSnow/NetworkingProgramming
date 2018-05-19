import UserCreator.Creator;
import UserCreator.CreatorHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.util.Properties;
import java.util.Scanner;

/**
 * Created by snow on 2018/5/8.
 */
public class Client {
    private static Creator creatorOfServant;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        connectToServant(args);
        if (creatorOfServant == null) {
            System.err.println("Something wrong in connect to servant.");
            return;
        }

        try {
            while (true) {
                System.out.println();
                System.out.println("-----------请选择下列操作-----------");
                System.out.println("1. 注册账户");
                System.out.println("2. 登录账户");
                System.out.println("----------------------------------");
                String choice = scanner.next();

                if (Integer.parseInt(choice) == 1)
                    register();
                else if (Integer.parseInt(choice) == 2)
                    login();
                else
                    System.out.println("请输入1-2中的选项");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connectToServant(String[] args) {
        System.out.println("Client init config starts....");
        Properties properties = new Properties();
        properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");  //指定ORB的ip地址
        properties.put("org.omg.CORBA.ORBInitialPort", "8080");       //指定ORB的端口

        //创建一个ORB实例
        ORB orb = ORB.init(args, properties);

        //获取根名称上下文
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            try {
                //通过ORB拿到server实例化好的Creator类
                creatorOfServant = CreatorHelper.narrow(ncRef.resolve_str(Servant.NAME_SERVICE_CREATOR));
            } catch (NotFound e) {
                e.printStackTrace();
            } catch (CannotProceed e) {
                e.printStackTrace();
            } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
                e.printStackTrace();
            }
        } catch (InvalidName e) {
            e.printStackTrace();
        }

        System.out.println("Client init config ends...");
    }

    public static void register() {
        System.out.printf("请输入用户名：");
        String username = scanner.next();
        System.out.printf("请输入密码：");
        String password = scanner.next();

        /* servant */
        creatorOfServant.register(username, password);
    }

    public static void login() {
        System.out.printf("请输入用户名：");
        String username = scanner.next();
        System.out.printf("请输入密码：");
        String password = scanner.next();

        /* servant */
        creatorOfServant.login(username, password);


    }
}
