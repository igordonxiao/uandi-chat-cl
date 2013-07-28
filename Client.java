import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gordon Shaw
 *
 */
public class Client extends Thread{  
  
    private Socket socket = null;  
    private BufferedReader br = null;
    private String clientName = null;
    private PrintWriter pw = null; 
    private PrintWriter pwAll = null;
    private static List<Socket> clients = new ArrayList<Socket>();
    private static Map<String, String> names = new HashMap<String, String>();
    public Client(Socket s){  
        socket = s;
        clients.add(socket);
        try {  
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"));  
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK")),true);  
            pw.println("***************************************************");
            pw.println(" -Welcome Y&I chatting channel- ");
            pw.println("Please input your nick name[支持中文]: ");
            String nickName = br.readLine().trim();
            if("".equals(nickName)){
            	nickName = socket.getInetAddress().getHostName();
            }
            pw.println("Hello " + nickName + " and have fun!");
            pw.println("-------------------------------------------------------------");
            StringBuilder userNames = new StringBuilder("  ");
            for(Entry<String, String> entry : names.entrySet()){
            	userNames.append(entry.getValue()).append(" | ");
            }
            pw.println(names.size() +" user(s) is(are) online: " + userNames.substring(0, userNames.length() - 2));
            pw.println("Write a message and press 'Enter' key to send it out...");
            pw.println("Tip: Input '886' to quit this channel.");
            if(names.size() > 0){
            	sendMsg("Info: " + nickName + " is online. ",true);
            }
            pw.println("***************************************************");
            names.put(socket.getInetAddress().getHostAddress(), nickName);
            start();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        
        clientName = getClientName(socket);
    }
      
    @Override  
    public void run() {  
        while(true){  
            String str;  
            try { 
                str = br.readLine().trim(); 
                System.out.println("Client Socket Message:" + str);  
                if(str.equals("886")){
                	sendMsg(clientName + " is offline!", true);
                    br.close();
                    pw.close();  
                    removeAclient(socket);
                    socket.close();
                    break;  
                }else{
                    sendMsg(str, false);
                }
            } catch (Exception e) {  
                try {  
                    if(br != null){
                    	br.close();
                    }
                    if(pw != null){
                    	pw.close();
                    }
                    if(socket != null){
                    	socket.close();
                    }
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
      
    private void sendMsg(String str, boolean isDefinedBySelf){
    	String time = " [" + new java.text.SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()) + "]";
    	StringBuilder closedClient = new StringBuilder();
    	List<Socket> removedClients = new ArrayList<Socket>();
    	 for(Socket s : Client.clients){
    		 if(s.isClosed()){
					String cName = getClientName(s);
					closedClient.append(cName).append(" ");
					removedClients.add(s);
				}
    	 }
    	 
    	 if(removedClients.size() > 0){
    		 Client.clients.removeAll(removedClients);
    	 }
    	 for(Socket so : Client.clients){
			try {
				pwAll = new PrintWriter(new BufferedWriter(new OutputStreamWriter(so.getOutputStream(), "GBK")),true);
				if(closedClient.length() > 0){
					pwAll.println("Info: [" + closedClient.toString() + "] offline." + time);
				}
	         	if(isDefinedBySelf){
	         		pwAll.println(str + time);
	         	}else{
	         		pwAll.println(clientName + ": " + str + time);
	         	}
	         	pwAll.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
         	
         }
    }
    
    private void removeAclient(Socket s){
    	names.remove(s.getInetAddress().getHostAddress());
		Client.clients.remove(s);
    }
    
    private String getClientName(Socket s){
    	String clientName = s.getInetAddress().getHostName();
      	String socketIp = s.getInetAddress().getHostAddress();
      	if(names.containsKey(socketIp)){
      		clientName = names.get(socketIp);
      	}
      	return clientName;
    }
}  