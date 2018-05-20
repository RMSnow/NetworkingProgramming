package app;

import TodoList.UserOperation;
import TodoList.UserOperationHelper;
import TodoList.UserOperationServantImpl;
import UserCreator.Creator;
import UserCreator.CreatorHelper;
import UserCreator.CreatorServantImpl;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.Properties;

/**
 * Created by snow on 2018/5/8.
 */
public class AppServant {
    public static final String NAME_SERVICE_CREATOR = "Creator";

    static ORB orb;
    static org.omg.CORBA.Object obj;
    static org.omg.CORBA.Object ref;
    static POA rootPOA;
    static org.omg.CORBA.Object objRef;
    static NamingContextExt ncRef;

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();

            /* 配置NameService服务器地址 */

            properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
            properties.put("org.omg.CORBA.ORBInitialPort", "8080");
            orb = ORB.init(args, properties);

            /* 启动Servant */

            obj = orb.resolve_initial_references("RootPOA");
            rootPOA = POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();

            /* 服务注册与绑定 */

            CreatorServantImpl creatorImpl = new CreatorServantImpl();
            ref = rootPOA.servant_to_reference(creatorImpl);
            Creator creatorRef = CreatorHelper.narrow(ref);

            objRef = orb.resolve_initial_references("NameService");
            ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name(NAME_SERVICE_CREATOR);
            ncRef.rebind(path, creatorRef);

            System.out.println("server.ToDoListServer is ready and waiting....");

            /* 服务启动 */

            orb.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户名新建一个服务对象
     *
     * @param username
     */
    public static void createNameServceOfUser(String username) {
        try {
            /* 服务注册与绑定 */

            UserOperationServantImpl userImpl = new UserOperationServantImpl();
            ref = rootPOA.servant_to_reference(userImpl);
            UserOperation userRef = UserOperationHelper.narrow(ref);

            NameComponent path[] = ncRef.to_name(username);
            ncRef.rebind(path, userRef);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//orbd -ORBInitialPort 8080 -ORBInitialHost 127.0.0.1
