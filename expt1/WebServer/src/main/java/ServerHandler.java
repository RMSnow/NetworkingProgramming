import java.net.Socket;

/**
 * Created by snow on 2018/4/21.
 */
public class ServerHandler implements Runnable {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            Request req = new Request(socket.getInputStream());
            Response res = new Response(req);
            res.write(socket.getOutputStream());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Runtime Error");
        }
    }
}
