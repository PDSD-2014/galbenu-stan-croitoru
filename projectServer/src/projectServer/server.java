package projectServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.bind.ParseConversionEvent;


public class server {
 
 public static void main(String[] args){
  ServerSocket serverSocket = null;
  Socket socket = null;
  DataInputStream dataInputStream = null;
  DataOutputStream dataOutputStream = null;
  
  
  String []names = new String[100];
  int []scores = new int[100];
  int []moves = new int[100];
  int size = 0;
  
  try {
   serverSocket = new ServerSocket(8889);
   System.out.println("Listening :8888");
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
  while(true){
   try {
    socket = serverSocket.accept();
    dataInputStream = new DataInputStream(socket.getInputStream());
    dataOutputStream = new DataOutputStream(socket.getOutputStream());
    //System.out.println("ip: " + socket.getInetAddress());
    String s = dataInputStream.readUTF();
    System.out.println("message: " + s);
    
    if(s.equals("get_highscores")){
    	String highscore="";
        for(int i = 0 ; i < size; i++)
        	highscore = highscore.concat(i+1+". "+names[i]+" "+scores[i]+" "+moves[i]+"\n");
        System.out.println(highscore);
        dataOutputStream.writeUTF(highscore);
    	
    }else{
	    String parts[] = s.split(" ");
	    
	    if(size == 0){
	    	names[0] = parts[0];
	    	scores[0] = Integer.parseInt(parts[1]);
	    	moves[0] = Integer.parseInt(parts[2]);
	    	size++;
	    }else{
	    	int k=size;
	    	for(int i = 0 ; i < size; i++)
	    		if( scores[i] < Integer.parseInt(parts[1]) ){
	    			k=i;
	    			break;
	    		}
	    	// deplasare la dreapta
	    	if(size < 100 && k != size ){
	    		size++;
	    		for(int i = size ; i >= k; i--){
	    			names[i+1] = names[i];
	    			scores[i+1] = scores[i];
	    			moves[i+1] = moves[i];
	    		}
	    	}
	    	names[k] = parts[0];
	    	scores[k] = Integer.parseInt(parts[1]);
	    	moves[k] = Integer.parseInt(parts[2]);
	    }
    }

   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   finally{
    if( socket!= null){
     try {
      socket.close();
     } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     }
    }
    
    if( dataInputStream!= null){
     try {
      dataInputStream.close();
     } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     }
    }
    
    if( dataOutputStream!= null){
     try {
      dataOutputStream.close();
     } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     }
    }
   }
  }
 }
}