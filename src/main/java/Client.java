
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @ClassName Client
 * @Description TODO
 * @Author don
 * @Date 2021-04-19 11:07
 * @Version 1.0
 */

public class Client {

    /**
     * @param args args[0] = host,args[1] = port,args[2]... = image file
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        int i = 2;
        while (i < args.length){
            InetAddress ip = InetAddress.getByName(args[0]);
            Socket socket = new Socket(ip, Integer.parseInt(args[1]));

            OutputStream outputStream = socket.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(args[i]);

            int length = 0;
            byte[] bytes = new byte[2048];

            while ((length =fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,length);
            }
            socket.shutdownOutput();
            fileInputStream.close();

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

            i++;
        }
    }
}