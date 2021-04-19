import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @ClassName Server
 * @Description 图片服务器，用于接收上传的图片并存储到指定位置。参数一：监听的端口，参数二：指定的存储位置，
 * @Author don
 * @Date 2021-04-19 11:35
 * @Version 1.0
 */
public class ImageServer {

    public static ThreadPoolExecutor threadPool;

    static {
        threadPool = (ThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        System.out.println( new Date().toString() + "：线程池创建成功");
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        System.out.println(new Date().toString() + "：服务器创建成功，开始监听 " + args[0] + "端口");

        while (true){

            Socket accept = serverSocket.accept();
            System.out.println("Socket 已建立");
            Future<String> submit = scheduledExecutorService.submit(new ImageUploadCal(accept));

        }

    }

}

class ImageUploadCal implements Callable<String>{

    private Socket accept;

    public ImageUploadCal(Socket socket){
        accept = socket;
        System.out.println("线程已建立");
    }

    @Override
    public String call() throws Exception {

        System.out.println("开始处理");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String rootPath = "/usr/local/images/";
        File file = new File(Thread.currentThread().getName() + ".png");

        InputStream inputStream = accept.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        int length = 0;
        byte[] bytes = new byte[2048];

        while ((length = inputStream.read(bytes)) != -1){

            fileOutputStream.write(bytes,0,length);
            md5.update(bytes,0,length);

        }

        byte[] digest = md5.digest();
        BigInteger bigInteger = new BigInteger(1, digest);
        String result = bigInteger.toString(16);
        System.out.println(result);

//        file.renameTo()
        accept.shutdownInput();

        OutputStream outputStream = accept.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        String url = "http://don.anan.com/images/" + result + ".png";
        outputStreamWriter.write(url);
        outputStreamWriter.flush();
        accept.shutdownOutput();
        accept.close();

        System.out.println("处理完成");
        return result;
    }


}