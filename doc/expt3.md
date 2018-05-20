---
layout:     post
title:      "使用Java实现Web服务器编程"
subtitle:   "A Demo Web Server"
date:       2018-4-29 19:50:00
author:     "Snow"
header-img: "img/post-bg-2015.jpg"
catalog: true
tags:
- Networking
---

## EXPT 1：使用JAVA实现WEB服务器编程

### 实验要求

完成简单的web服务器程序，需实现`GET`、`POST`、`PUT`方法。

### 业务逻辑

#### 需求描述

实现一个简单的Web服务器，它能够：

1. 当一个客户（浏览器）与服务器联系时，创建一个连接套接字；
2. 从这个连接套接字接收`HTTP`请求，请求包括`GET`、`POST`、`PUT`方法；
3. 解释该请求以确定所请求的特定文件；
4. 从服务器的文件系统获得请求的文件；
5. 创建一个由请求的文件组成的`HTTP`响应报文，报文前面有首部行；
6. 经TCP连接向请求浏览器发送响应。如果浏览器请求一个在该服务器种不存在的文件，服务器应当返回一个`404 Not Found`差错报文。

#### 时序图

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/web/WebServer.png)

### 具体实现

#### 项目架构

项目分为`Server`主类，`ServerHandler`、`Request`、`Response`类与`entity`包。

<img src="https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/web/cd-1.png" style="zoom:50%">

`entity`包结构如下：

<img src="https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/web/cd-2.png" style="zoom:50%">

#### `Server`

```java
package web;

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
```

#### `ServerHandle`

```java
package web;

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

```

#### `Request`

```java
package web;

import entity.Method;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by snow on 2018/4/21.
 */
public class Request {
    String method;
    String url;
    String version;
    ArrayList<String> headerLine = new ArrayList<>();

    Hashtable<String, String> postEntityBody = new Hashtable<>();
    String putEntityBody = "";

    public Request(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            //Request Line
            String str = reader.readLine();
            parseRequestLine(str);

            //Header Line
            while (!str.equals("")) {
                str = reader.readLine();
                parseRequestHeaderLine(str);
            }

            //Entity Body: POST
            if (method.equals(Method.POST)) {
                while (str != null) {
                    str = reader.readLine();
                    System.out.println(str);

                    //boundary code
                    if (str.contains("-----"))
                        continue;

                    //Content-Disposition
                    if (str.contains("Content-Disposition")) {
                        try {
                            String[] contents = str.split("\\s+");
                            String[] names = contents[2].split("\"");
                            String key = names[1];

                            //cr lf
                            str = reader.readLine();
                            System.out.println(str);

                            //value
                            str = reader.readLine();
                            System.out.println(str);
                            String value = str;

                            postEntityBody.put(key, value);
                            continue;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //Entity Body: PUT
            if (method.equals(Method.PUT)) {
                while (str != null) {
                    str = reader.readLine();
                    System.out.println(str);

                    //boundary code
                    if (str.contains("-----"))
                        continue;

                    //Content-Disposition
                    if (str.contains("Content-Disposition")) {
                        try {
                            //cr lf
                            str = reader.readLine();
                            System.out.println(str);

                            //entity
                            while (str != null) {
                                str = reader.readLine();
                                System.out.println(str);
                                putEntityBody += str;
                            }
                            continue;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Parse Request Error");
        }
    }

    /**
     * 解析Request Line
     *
     * @param str
     */
    private void parseRequestLine(String str) {
        System.out.println(str);
        String[] split = str.split("\\s+");
        try {
            method = split[0];
            if (!Method.methods.contains(method)) {
                method = Method.UNRECOGNIZED;
            }
            url = split[1];
            version = split[2];
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RequestLine Error");
        }
    }

    /**
     * 解析Header Line
     *
     * @param str
     */
    private void parseRequestHeaderLine(String str) {
        System.out.println(str);
        headerLine.add(str);
    }

}
```

#### `Response`

```java
package web;

import entity.MIME;
import entity.Method;
import entity.Status;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snow on 2018/4/21.
 */
public class Response {
    String version = "HTTP/1.1";
    String status;
    ArrayList<String> headerLine = new ArrayList<>();
    byte[] entityBody;

    final String resPath = "src/main/resources/web";

    public Response(Request req) {
        if (req.method.equals(Method.GET))
            doGet(req);

        if (req.method.equals(Method.POST))
            doPost(req);

        if (req.method.equals(Method.PUT))
            doPut(req);

        if (req.method.equals(Method.UNRECOGNIZED)) {
            fillHeaders(Status.BAD_REQUEST);
        }

    }

    /**
     * 处理GET请求
     *
     * @param req
     */
    private void doGet(Request req) {
        try {
            status = Status.OK;
            fillHeaders(status);

            //html
            headerLine.add(MIME.HTML.toString());
            File res = new File(resPath + req.url);
            if (res.exists()) {
                setContentType(req.url, headerLine);
                fillResponse(getBytes(res));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fillHeaders(Status.BAD_REQUEST);
            fillResponse(Status.BAD_REQUEST);
        }
    }

    /**
     * 处理POST请求
     *
     * @param req
     */
    private void doPost(Request req) {
        try {
            doGet(req);

            //output entity body
            if (req.postEntityBody != null) {
                File file = new File(resPath + "/web/output.txt");
                if (file.exists()) {
                    Writer out = new FileWriter(file);
                    out.write(req.putEntityBody);
                    out.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            fillHeaders(Status.BAD_REQUEST);
            fillResponse(Status.BAD_REQUEST);
        }
    }

    /**
     * 处理PUT方法
     *
     * @param req
     */
    private void doPut(Request req) {
        try {
            //output entity body
            if (req.postEntityBody != null) {
                File file = new File(resPath + req.url);
                if (file.exists()) {
                    Writer out = new FileWriter(file);
                    out.write(req.putEntityBody);
                    out.close();
                }
            }
            doGet(req);
        } catch (Exception e) {
            e.printStackTrace();
            fillHeaders(Status.BAD_REQUEST);
            fillResponse(Status.BAD_REQUEST);
        }
    }


    /**
     * 从特定的文件中获取全部字节
     *
     * @param file
     * @return
     * @throws IOException
     */
    private byte[] getBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] array = new byte[length];
        InputStream in = new FileInputStream(file);
        int offset = 0;
        while (offset < length) {
            int count = in.read(array, offset, (length - offset));
            offset += count;
        }
        in.close();
        return array;
    }

    /**
     * Response报文：填充Headers
     *
     * @param status
     */
    private void fillHeaders(String status) {
        headerLine.add(version + " " + status);
        headerLine.add("Connection: close");
        headerLine.add("Server: SimpleWebServer");
    }

    private void fillResponse(String response) {
        entityBody = response.getBytes();
    }

    private void fillResponse(byte[] response) {
        entityBody = response;
    }

    public void write(OutputStream os) throws IOException {
        DataOutputStream output = new DataOutputStream(os);
        for (String header : headerLine) {
            output.writeBytes(header + "\r\n");
        }
        output.writeBytes("\r\n");
        if (entityBody != null) {
            output.write(entityBody);
        }
        output.writeBytes("\r\n");
        output.flush();
    }

    /**
     * 设置返回文件的类型
     *
     * @param uri
     * @param list
     */
    private void setContentType(String uri, List<String> list) {
        try {
            String ext = uri.substring(uri.indexOf(".") + 1);
            list.add(MIME.valueOf(ext.toUpperCase()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("MIME NOT FOUND");
        }
    }
}
```

#### `entity`包

`Method`类规定了HTTP方法：

```java
package entity;

import java.util.ArrayList;

/**
 * Created by snow on 2018/4/21.
 */
public class Method {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String UNRECOGNIZED = "UNRECOGNIZED";

    public static final ArrayList<String> methods = new ArrayList<String>();

    static {
        methods.add(GET);
        methods.add(POST);
        methods.add(PUT);
        methods.add(DELETE);
    }
}
```

`MIME`类规定了文件类型：

```java
package entity;

/**
 * Created by snow on 2018/4/21.
 */
public enum MIME {
    CSS("CSS"), //
    GIF("GIF"), //
    HTM("HTM"), //
    HTML("HTML"), //
    ICO("ICO"), //
    JPG("JPG"), //
    JPEG("JPEG"), //
    PNG("PNG"), //
    TXT("TXT"), //
    XML("XML"); //

    private final String extension;

    MIME(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        switch (this) {
            case CSS:
                return "Content-Type: text/css";
            case GIF:
                return "Content-Type: image/gif";
            case HTM:
            case HTML:
                return "Content-Type: text/html";
            case ICO:
                return "Content-Type: image/gif";
            case JPG:
            case JPEG:
                return "Content-Type: image/jpeg";
            case PNG:
                return "Content-Type: image/png";
            case TXT:
                return "Content-type: text/plain";
            case XML:
                return "Content-type: text/xml";
            default:
                return null;
        }
    }
}
```

`Status`类规定了HTTP请求的响应状态：

```java
package entity;

/**
 * Created by snow on 2018/4/21.
 */
public class Status {
    public static final String OK = "200 OK";
    public static final String MOVED_PERMANENTLY = "301 Moved Permanently";
    public static final String BAD_REQUEST = "400 Bad Request";
    public static final String NOT_FOUND = "404 Not Found";
    public static final String HTTP_VERSION_NOT_SUPPORTED = "505 HTTP Version Not Supported";
}
```

### 实验结果

#### `PUT`方法

首先测试`PUT`方法，利用postman发送`PUT`请求：

![1](https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/web/postman.png)

其中，`Form.html`文件如下所示：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Form</title>
</head>
<body>
    This is form_action.html
</body>
</html>
```

#### `GET`方法

其次测试`GET`方法，在浏览器中访问：`localhost:8080/Hello.html`，得到界面如下：

<img src="https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/web/hello.png" style="zoom:50%">

`Hello.html`文件内容如下，其中表单方法为`post`：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello</title>
</head>
<body>
    <form action="Form.html" method="post">
        Username:<br>
        <input type="text" name="username">
        <br>
        Password:<br>
        <input type="password" name="password">
        <br><br>
        <input type="submit" value="Submit">
    </form>
</body>
</html>
```

#### `POST`方法

在上一步得到的浏览器界面中，输入用户名`zxy`，密码`123456`，点击`Submit`后跳转到`Form.html`：

<img src="https://raw.githubusercontent.com/RMSnow/NetworkingProgramming/master/doc/web/form.png" style="zoom:50%">

检查服务器对`POST`请求的响应报文解析情况：

`POST`请求报文如下：

```
POST / HTTP/1.1
cache-control: no-cache
Postman-Token: de372c77-974d-4712-ab37-dcb88ce1de70
User-Agent: PostmanRuntime/6.4.1
Accept: */*
Host: localhost:8080
accept-encoding: gzip, deflate
content-type: multipart/form-data; boundary=--------------------------869815759519057510504163
content-length: 279
Connection: keep-alive

----------------------------869815759519057510504163
Content-Disposition: form-data; name="username"

zxy
----------------------------869815759519057510504163
Content-Disposition: form-data; name="passward"

123456
----------------------------869815759519057510504163--
```

服务器解析后，将报文中`entity body`的信息存储在文件`output.txt`中：

```
username=zxy
password=123456
```

报文解析成功。

### 项目特点

- 整个项目的难点主要为以下两点：

  - **如何获取HTTP请求报文？**

    在项目开发时，可现在控制台输出所有报文，或者可利用wireshark等抓包工具获得Request报文。之后再对报文的请求行、首部行、实体信息分别进行解析。

  - **如何生成HTTP响应报文？**

    HTTP响应报文包括Status信息行、首部行、实体信息，因此应先定义响应状态码（`Status`类）、返回文件类型（`MIME`类）等，之后逐步生成响应报文。

- 本项目实现了一个能够同时处理多个请求的**多线程**服务器。

  首先创建一个主线程，在固定端口（`8080`）监听客户端请求。当从客户端收到TCP连接请求时，它将通过另一个端口建立TCP连接，并在另外的单独线程中为客户端请求提供服务。这样在每个请求/响应对的独立线程中将有一个独立的TCP连接。