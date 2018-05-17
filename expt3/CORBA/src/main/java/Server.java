import UserCreator.Creator;
import UserCreator.CreatorHelper;
import UserCreator.CreatorImpl;
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
public class Server {
    public static void main(String[] args){
        try {
            //String[] args = {};
            Properties properties = new Properties();

            properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");  //指定ORB的ip地址
            properties.put("org.omg.CORBA.ORBInitialPort", "8080");       //指定ORB的端口

            //创建一个ORB实例
            ORB orb = ORB.init(args, properties);

            //拿到根POA的引用,并激活POAManager,相当于启动了server
            org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
            POA rootPOA = POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();

            //TODO: 创建一个CreatorImpl实例
            CreatorImpl creatorImpl = new CreatorImpl();
//            creatorImpl.setToDoListServer(this);

            //从服务中得到对象的引用,并注册到服务中
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(creatorImpl);
            Creator creatorhref = CreatorHelper.narrow(ref);

            //得到一个根命名的上下文
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            //在命名上下文中绑定这个对象
            String name = "Creator";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, creatorhref);

            System.out.println("server.ToDoListServer is ready and waiting....");

            //启动线程服务,等待客户端调用
            orb.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
