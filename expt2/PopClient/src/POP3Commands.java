import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by snow on 2018/4/25.
 */
public interface POP3Commands {
    String getReturn(BufferedReader in);

    String getResult(String line);

    String sendServer(String str, BufferedReader in, BufferedWriter out) throws IOException;

    String user(String user, BufferedReader in, BufferedWriter out) throws IOException;

    String pass(String password, BufferedReader in, BufferedWriter out) throws IOException;

    String stat(BufferedReader in, BufferedWriter out) throws IOException;

    String list(BufferedReader in, BufferedWriter out) throws IOException;

    String list(int mailNum, BufferedReader in, BufferedWriter out) throws IOException;

    void dele(int mailNum, BufferedReader in, BufferedWriter out) throws IOException;

    void retr(int mailNum, BufferedReader in, BufferedWriter out) throws IOException;

    void quit(BufferedReader in, BufferedWriter out) throws IOException;

}
