import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName ImageUploadClient
 * @Description 基于 TCP 协议的图片上传客户端。写这个工具主要是为了在 typora 中自动上传图片并获得外链，因此不需要图形化界面。
 * @Author don
 * @Date 2021-04-19 11:07
 * @Version 1.0
 */
public class ImageUploadClient {

    /**
     * 图片服务器域名或ip地址，默认值是localhost
     */
    public static final String HOST;

    /**
     * 图片服务器的端口号，默认值是8080
     */
    public static final int PORT;

    /**
     *加载配置文件，读取自定义配置
     */
    static {

        Properties properties = new Properties();
        InputStream resource = ImageUploadClient.class.getClassLoader().getResourceAsStream("clientconf.properties");
        try {
            properties.load(resource);
        } catch (IOException e) {
            throw new RuntimeException("配置文件没有找到，请将配置文件放到本程序所在目录下");
        }

        HOST = properties.getProperty("host", "localhost");
        PORT = Integer.parseInt(properties.getProperty("port","8080"));
        System.out.println(new Date().toString() + "：客户端创建成功....");

    }

    /**
     * @param args 上传图片的路径，支持批量上传。
     * @throws IOException
     */
    public static void main(String[] args) {

        int i = 0;
        while (i < args.length){

            //创建Socket，连接服务器
            Socket socket = null;
            try {
                InetAddress ip = InetAddress.getByName(HOST);
                socket = new Socket(ip, PORT);
            } catch (IOException e) {
                System.out.println("服务器 " + HOST + " 连接失败，请核对服务器ip和验证服务器是否开启");
                e.printStackTrace();
                System.exit(-1);
            }
            if (i == 0){
                System.out.println(new Date().toString() + "：成功连接服务器...");
            }

            //获取第 i 张图片
            InputStream fileInputStream = null;
            try {

                if ( args[i].startsWith("http") ){
                    InputStream urlInputStream = new URL(args[i]).openStream();
                    fileInputStream = urlInputStream;
                }else {
                    fileInputStream = new FileInputStream(args[i]);
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(args[i] + "图片不存在,上传失败！");
                continue;
            }

            int length = 0;
            byte[] bytes = new byte[0];

            try {
                OutputStream outputStream = socket.getOutputStream();
                length = 0;
                bytes = new byte[2048];

                //上传图片
                while ((length =fileInputStream.read(bytes)) != -1){
                    outputStream.write(bytes,0,length);
                }
                socket.shutdownOutput();
                fileInputStream.close();
                if (i==0){
                    System.out.println(new Date().toString() + "：图片上传成功...");
                }

            } catch (IOException e) {
                System.out.println(new Date().toString() + "：图片上传失败...");
                e.printStackTrace();
                continue;
            }

            //获取URL外链
            ByteArrayOutputStream byteArrayOutputStream = null;
            try {
                InputStream inputStream = socket.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();

                while ((length = inputStream.read(bytes)) != -1){
                    byteArrayOutputStream.write(bytes,0,length);
                }

                String string = byteArrayOutputStream.toString();

                //输出成功相关信息和外链
                if (i == 0){
                    System.out.println("Upload Success:");
                }
                System.out.println(string);

            } catch (IOException e) {
                System.out.println(new Date().toString() + "：外链获取失败...");
                e.printStackTrace();
            }

            try {
                socket.shutdownInput();
                socket.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                System.out.println(new Date().toString() + "：资源未正常关闭...");
                e.printStackTrace();
            }

            //获取下一张图片
            i++;
        }
    }
    
}