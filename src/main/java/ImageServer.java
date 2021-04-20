import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @ClassName ImageServer
 * @Description 图片服务器，用于接收上传的图片并存储到指定位置。
 * @Author don
 * @Date 2021-04-19 11:35
 * @Version 1.0
 */
public class ImageServer {

    /**
     * 服务器的域名，用于外链使用，默认是本地ip
     */
    public static final String HOST;

    /**
     * 服务器监听的端口，默认是8080
     */
    public static final int PORT;

    /**
     * 图片的存储位置，默认是/usr/local/images目录
     */
    public static final String LOCATION;

    static {

        Properties properties = new Properties();
        InputStream resource = ImageServer.class.getClassLoader().getResourceAsStream("serverconf.properties");
        try {
            properties.load(resource);
        } catch (IOException e) {
            throw new RuntimeException("配置文件没有找到，请将配置文件放到本程序所在目录下");
        }
        PORT = Integer.parseInt(properties.getProperty("port","8080"));
        LOCATION = properties.getProperty("location","/usr/local/images");
        File imagesDirectory = new File(LOCATION);
        if (!imagesDirectory.exists() ){
            if (imagesDirectory.mkdirs()){
                System.out.println(LOCATION + "创建成功！！");
            }else{
                throw new RuntimeException(LOCATION + "创建失败，请重新启动服务器！！");
            }
        }

        try {
            HOST = properties.getProperty("host", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException("默认ip地址失败，请自定义ip地址或域名");
        }
        System.out.println( new Date().toString() + "：自定义配置加载成功...");

    }

    /**
     * 一个固定线程数量的线程池。多线程操作，提高服务器的高可用。
     */
    public static final ExecutorService threadPool;

    static {
        threadPool = Executors.newScheduledThreadPool(10);
        System.out.println( new Date().toString() + "：线程池创建成功...");
    }

    /**
     * 服务器程序入口
     * @param args
     */
    public static void main(String[] args)  {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(new Date().toString() + "：服务器创建成功，开始监听 " + PORT + "端口.......");

        while (true){

            //监听到客户端连接，创建连接
            Socket client = null;
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(new Date().toString() + "：客户端ip：" +client.getInetAddress().getHostAddress() + "连接中....");
            
            threadPool.submit(new ImageUploadCal(client,LOCATION,HOST));

        }

    }
}

/**
 * 执行图片接收和存储的子线程
 */
class ImageUploadCal implements Callable<String>{

    // 客户端连接
    private Socket connetction;

    // 图片存储位置
    private String localtion;

    //外链使用
    private String host;

    public ImageUploadCal(Socket socket,String localtion,String ip){
        connetction = socket;
        this.localtion = localtion;
        host = ip;
    }

    @Override
    public String call()  {

        System.out.println(new Date().toString() + "：子线程 " + Thread.currentThread().getName() + "已建立...");

        String url = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            File file = new File(localtion,Thread.currentThread().getName() + ".png");

            //接收图片
            InputStream inputStream = connetction.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            int length = 0;
            byte[] bytes = new byte[2048];
            System.out.println(new Date().toString() + "：子线程 " + Thread.currentThread().getName() + "接收图片中...");
            while ((length = inputStream.read(bytes)) != -1){

                fileOutputStream.write(bytes,0,length);
                md5.update(bytes,0,length);

            }

            fileOutputStream.close();
            connetction.shutdownInput();

            //计算md5
            byte[] digest = md5.digest();
            BigInteger bigInteger = new BigInteger(1, digest);
            String imageName = bigInteger.toString(16);

            //图片存储
            if (file.renameTo(new File(localtion, imageName + ".png"))){
                System.out.println(new Date().toString() + "：子线程" + Thread.currentThread().getName() + "图片已存储到" + localtion + imageName +".png");
            }else{
                System.out.println(new Date().toString() + "：子线程" + Thread.currentThread().getName() + "图片存储失败");
                connetction.close();
            }

            //发送外链
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connetction.getOutputStream());
            url = "http://" + host + "/images/" + imageName + ".png";
            outputStreamWriter.write(url);
            outputStreamWriter.flush();
            connetction.shutdownOutput();
            connetction.close();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new Date().toString() + "：子线程 " + Thread.currentThread().getName() + "结束任务...");
        return url;
    }
}