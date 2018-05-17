import UserCreator.Creator;
import UserCreator.CreatorHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.util.Properties;

/**
 * Created by snow on 2018/5/8.
 */
public class Client {
    public static void main(String[] args){
        System.out.println("Client init config starts....");
//        String[] args = {};
        Properties properties = new Properties();
        properties.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1");  //指定ORB的ip地址
        properties.put("org.omg.CORBA.ORBInitialPort", "8080");       //指定ORB的端口

        //创建一个ORB实例
        ORB orb = ORB.init(args, properties);

        //获取根名称上下文
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "Creator";
            try {
                //通过ORB拿到server实例化好的Creator类
                Creator creator = CreatorHelper.narrow(ncRef.resolve_str(name));
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

        //TODO: 业务代码
    }
}
