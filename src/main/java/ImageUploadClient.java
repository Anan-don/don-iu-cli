import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
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
    public static void main(String[] args) throws IOException {

        int i = 0;
        while (i < args.length){

            //创建Socket，连接服务器
            InetAddress ip = InetAddress.getByName(HOST);
            Socket socket = new Socket(ip, PORT);
            System.out.println(new Date().toString() + "：成功连接服务器...");

            //获取第 i 张图片
            FileInputStream fileInputStream = new FileInputStream(args[i]);
            OutputStream outputStream = socket.getOutputStream();
            int length = 0;
            byte[] bytes = new byte[2048];

            //上传图片
            while ((length =fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,length);
            }
            socket.shutdownOutput();
            fileInputStream.close();
            System.out.println(new Date().toString() + "：图片上传成功...");

            //输出成功相关信息
            if (i == 0){
                System.out.println("Upload Success:");
            }

            //获取URL外链
            InputStream inputStream = socket.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while ((length = inputStream.read(bytes)) != -1){
                byteArrayOutputStream.write(bytes,0,length);
            }

            String string = byteArrayOutputStream.toString();
            System.out.println(string);

            socket.shutdownInput();
            socket.close();
            byteArrayOutputStream.close();

            //获取下一张图片
            i++;
        }
    }
}