---
layout:     post
title:      "使用Java实现POP客户端编程"
subtitle:   "A Demo POP3 Client"
date:       2018-4-29 19:51:00
author:     "Snow"
header-img: "img/post-bg-2015.jpg"
catalog: true
tags:
- Networking
---

##  EXPT 2：使用JAVA实现POP客户端编程

### 实验要求

完成`user`、`pass`、`retr`、`list`等命令。

### 方案设计

#### 业务逻辑

pop客户端需要从pop邮箱服务器中收取用户邮件，在项目实现中有四个关键点：

- 连接：客户端与邮箱服务器通过套接字连接。
- Authorization：用户通过用户名和密码取得权限。
- 事务处理：用户可采用`user`、`pass`、`stat`、`retr`、`list`、`dele`等命令，查阅邮箱服务器中的信息。
- 更新：当用户输入`quit`命令时，客户端与服务器之间释放连接，且服务器端需要根据用户的操作（如删除）来进行更新。

#### 报文获取

首先先观察telnet在pop客户端上的实现。通过wireshark抓包工具，可以获取客户端与邮箱服务器之间的交互报文：

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/wireshark.png)

### 具体实现

#### 架构设计

项目分为`POP3Commands`接口、`POP3CommandsImpl`实现类，与`MyPOP`主类。

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/cd.png)

#### `POP3Commands` 接口

```java
package pop;

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
```

#### `POP3CommandsImpl`

```java
package pop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * Created by snow on 2018/4/25.
 */
public class POP3CommandsImpl implements POP3Commands{

    @Override
    public String getReturn(BufferedReader in) {
        String line = "";
        try {
            line = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public String getResult(String line) {
        StringTokenizer st = new StringTokenizer(line, " ");
        return st.nextToken();
    }

    @Override
    public String sendServer(String str, BufferedReader in, BufferedWriter out) throws IOException {
        out.write(str);
        out.newLine();
        out.flush();
        return getReturn(in);
    }

    @Override
    public String user(String user, BufferedReader in, BufferedWriter out) throws IOException {
        return sendServer("user " + user, in, out);
    }

    @Override
    public String pass(String password, BufferedReader in, BufferedWriter out) throws IOException {
        return sendServer("pass " + password, in, out);
    }

    @Override
    public String stat(BufferedReader in, BufferedWriter out) throws IOException {
        return sendServer("stat", in, out);
    }

    @Override
    public String list(BufferedReader in, BufferedWriter out) throws IOException {
        return sendServer("list", in, out);
    }

    @Override
    public String list(int mailNum, BufferedReader in, BufferedWriter out) throws IOException {
        return sendServer("list " + mailNum, in, out);
    }

    @Override
    public void dele(int mailNum, BufferedReader in, BufferedWriter out) throws IOException {
        sendServer("dele " + mailNum, in, out);
    }

    String getMessageDetail(BufferedReader in) throws UnsupportedEncodingException {
        String message = "";
        String line = null;

        try {
            line = in.readLine().toString();
            while (!".".equalsIgnoreCase(line)) {
                message = message + line + "\n";
                line = in.readLine().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void retr(int mailNum, BufferedReader in, BufferedWriter out) throws IOException {
        String msg = sendServer("retr " + mailNum, in, out);
        String result = getResult(msg);

        if (!"+OK".equals(result)) {
            System.out.println(msg);
        }else {
            System.out.println(getMessageDetail(in));
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void quit(BufferedReader in, BufferedWriter out) throws IOException {
        sendServer("QUIT", in, out);
    }
}
```

#### `MyPOP`主类

```java
package pop;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by snow on 2018/4/25.
 */
public class MyPOP {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        POP3Commands commands = new POP3CommandsImpl();

        BufferedReader in;
        BufferedWriter out;

        /* connect */
        while (true) {
            System.out.printf("> ");
            String[] conn = scanner.nextLine().split(" ");
            if (conn.length != 3) {
                System.out.println("USAGE: MyPOP [POP Server] [Port]");
                System.out.println("eg: MyPOP pop3.163.com 110");
                continue;
            }

            String popServer = conn[1];
            int port = Integer.valueOf(conn[2]);

            Socket socket = null;
            try {
                socket = new Socket(popServer, port);
            } catch (Exception e) {
                System.out.println("CANNOT CREATE THE SOCKET");
                System.out.println("USAGE: MyPOP [POP Server] [Port]");
                System.out.println("eg: MyPOP pop3.163.com 110");
                continue;
            }

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String msg = commands.getReturn(in);
            System.out.println(msg);
            String result = commands.getResult(msg);
            if (!"+OK".equals(result)) {
                System.out.println("CANNOT CONNECT TO MAIL SERVER");
                System.out.println("USAGE: MyPOP [POP Server] [Port]");
                System.out.println("eg: MyPOP pop3.163.com 110");
                continue;
            }

            break;
        }

        /* authorization */
        while (true) {
            System.out.printf("> ");
            String[] userParam = scanner.nextLine().split(" ");

            if (userParam.length != 2 || !userParam[0].equals("user")) {
                System.out.println("Please access the authorization by username and password first.");
                continue;
            }
            System.out.println(commands.user(userParam[1], in, out));

            System.out.printf("> ");
            String[] passParam = scanner.nextLine().split(" ");
            if (passParam.length != 2 || !passParam[0].equals("pass")) {
                System.out.println("Please access the authorization by username and password first.");
                continue;
            }
            System.out.println(commands.pass(passParam[1], in, out));

            break;
        }

        /* event handle */
        while (true) {
            System.out.printf("> ");
            String[] cmd = scanner.nextLine().split(" ");

            if (cmd[0].equals("stat")) {
                System.out.println(commands.stat(in, out));
                continue;
            } else if (cmd[0].equals("list")) {
                if (cmd.length == 1) {
                    System.out.println(commands.list(in, out));
                } else {
                    System.out.println(commands.list(Integer.valueOf(cmd[1]), in, out));
                }
                continue;
            } else if (cmd[0].equals("retr")) {
                if (cmd.length == 2) {
                    commands.retr(Integer.valueOf(cmd[1]), in, out);
                } else {
                    System.out.println("USAGE: retr [Mail Num]");
                }
                continue;
            } else if (cmd[0].equals("dele")) {
                if (cmd.length == 2) {
                    commands.dele(Integer.valueOf(cmd[1]), in, out);
                } else {
                    System.out.println("USAGE: dele [Mail Num]");
                }
                continue;
            }

            /* update */
            else if (cmd[0].equals("quit")) {
                commands.quit(in, out);
                return;
            }

            System.out.println("Please use the cmd like stat, list, retr, dele or quit.");
        }

    }
}
```

### 实验结果

首先通过pop邮箱服务器的ip、端口，进行客户端与服务器之间的连接。其次，通过`user`命令与`pass`命令获取权限：

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/auth.png)

之后，可测试`stat`、`list`、`retr`等事务处理的命令：

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/event.png)

最后，通过`delete`命令，删除1号邮件，在经过`quit`命令后，邮件服务器进行事务更新：

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/dele.png)

在网页端登录163邮箱，可发现原有的第1封邮件已被删除：

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/163-1.png)

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/pop/163-2.png)

