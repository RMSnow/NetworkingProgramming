import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by snow on 2018/4/21.
 */
public class Server  extends Thread {
    private static final int DEFAULT_PORT = 8080;

    private static final int N_THREADS = 3;

    public static void main(String args[]) {
        try {
            new Server().start(DEFAULT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Start Error");
        }
    }

    public void start(int port) throws IOException {
        ServerSocket s = new ServerSocket(port);
        System.out.println("Web server listening on port " + port + " (press CTRL-C to quit)");
        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
        while (true) {
            executor.submit(new ServerHandler(s.accept()));
        }
    }

}
