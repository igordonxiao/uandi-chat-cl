import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Gordon Xiao
 *
 */
public class Server {  
    public static void main(String[] args) {  
        ServerSocket s = null;  
        Socket socket  = null;  
        try {  
            s = new ServerSocket(9999);  
            while(true){  
                socket = s.accept();  
                System.out.println("socket:"+socket);  
                new Client(socket);  
                  
            }  
        } catch (Exception e) {  
            try {  
                socket.close();  
            } catch (IOException e1) {  
                e1.printStackTrace();  
            }  
        }finally{  
            try {  
                s.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
          
  
    }  
  
}  
