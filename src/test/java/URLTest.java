import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @ClassName URLTest
 * @Description TODO
 * @Author don
 * @Date 2021-04-22 10:44
 * @Version 1.0
 */


public class URLTest {

    @Test
    public void urlConnection() throws Exception{

        URL url = new URL("https://pic4.zhimg.com/50/v2-c261f23e668cdd364994a5f198d026b1_hd.jpg?source=1940ef5c");
        InputStream inputStream = url.openStream();

        FileOutputStream fileOutputStream = new FileOutputStream("test.png");
        int length = 0;
        byte[] bytes = new byte[2048];
        while ((length = inputStream.read(bytes)) != -1){
            fileOutputStream.write(bytes,0,length);
        }

        inputStream.close();
        fileOutputStream.close();

    }
}
