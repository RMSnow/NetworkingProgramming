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
