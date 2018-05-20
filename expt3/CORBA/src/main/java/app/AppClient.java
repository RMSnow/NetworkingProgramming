package app;

import TodoList.UserOperation;
import TodoList.UserOperationHelper;
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
public class AppClient {
    private static Creator creatorOfServant;
    private static UserOperation user;
    private static Scanner scanner = new Scanner(System.in);

    static NamingContextExt ncRef;

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
                System.out.println("3. 退出");
                System.out.println("----------------------------------");
                String choice = scanner.next();

                if (Integer.parseInt(choice) == 1)
                    register();
                else if (Integer.parseInt(choice) == 2)
                    login();
                else if (Integer.parseInt(choice) == 3)
                    return;
                else
                    System.out.println("请输入1-3中的选项");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端与服务器连接
     *
     * @param args
     */
    public static void connectToServant(String[] args) {

        /* 配置NameService服务器地址 */

        Properties properties = new Properties();
        properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
        properties.put("org.omg.CORBA.ORBInitialPort", "8080");
        ORB orb = ORB.init(args, properties);

        /* 获取Servant的引用 */

        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            ncRef = NamingContextExtHelper.narrow(objRef);

            try {
                creatorOfServant = CreatorHelper.narrow(ncRef.resolve_str(AppServant.NAME_SERVICE_CREATOR));
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
    }

    private static void register() {
        System.out.printf("请输入用户名：");
        String username = scanner.next();
        System.out.printf("请输入密码：");
        String password = scanner.next();

        /* servant */
        if (creatorOfServant.register(username, password)) {
            userOperation(username);
            return;
        }
        System.err.println("该用户名已注册！");
    }

    private static void login() {
        System.out.printf("请输入用户名：");
        String username = scanner.next();
        System.out.printf("请输入密码：");
        String password = scanner.next();

        /* servant */
        if (creatorOfServant.login(username, password)) {
            userOperation(username);
            return;
        }
        System.err.println("该用户名不存在，或密码错误！");

    }

    private static void userOperation(String username) {
        try {
            /* 获取Servant的引用 */
            user = UserOperationHelper.narrow(ncRef.resolve_str(username));
        } catch (NotFound e) {
            e.printStackTrace();
        } catch (CannotProceed e) {
            e.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("欢迎您，" + username);
        System.out.println();

        while (true) {
            System.out.println();
            System.out.println("-----------请选择下列操作-----------");
            System.out.println("1. 添加事项");
            System.out.println("2. 查询事项");
            System.out.println("3. 删除事项");
            System.out.println("4. 展示清单");
            System.out.println("5. 清除清单");
            System.out.println("6. 返回登录主页");
            System.out.println("----------------------------------");

            try {
                int choice = scanner.nextInt();
                String startTime, endTime, description;
                switch (choice) {
                    case 1:
                        System.out.printf("起始时间：");
                        startTime = scanner.next();
                        System.out.printf("截止时间：");
                        endTime = scanner.next();
                        System.out.printf("任务说明：");
                        description = scanner.next();
                        user.add(startTime, endTime, description);
                        break;
                    case 2:
                        System.out.println("请输入查询的时间范围：");
                        System.out.printf("\t起始时间：");
                        startTime = scanner.next();
                        System.out.printf("\t截止时间：");
                        endTime = scanner.next();
                        user.query(startTime, endTime);
                        break;
                    case 3:
                        System.out.println("当前的任务清单如下：");
                        user.show();
                        System.out.printf("请输入所要删除的任务序号：");
                        String key = scanner.next();
                        user.delete(key);
                        break;
                    case 4:
                        System.out.println("当前的任务清单如下：");
                        System.out.println(user.show());
                        break;
                    case 5:
                        System.out.println("您确定要清空所有任务吗？（请输入Y/N）");
                        String s = scanner.next();
                        if (s.equals("Y")) user.clear();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("请输入1-6中的选项");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
