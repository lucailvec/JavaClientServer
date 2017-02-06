import java.io.*;
import java.net.*;

public class MultiTrisThreadClient{

	public static void main(String [] args){

		
		int port = 55000;
		Socket s = new Socket();

		BufferedReader keyboard = new BufferedReader( new InputStreamReader (System.in) );
		String mex;

		byte [] buffer = new byte [1000];
		String fromSrv;
		int length;

		try{

			s.connect(new InetSocketAddress("localhost", port));
			Sender sender = new Sender(s);	

			
			System.out.println("Send something to server or write 'exit'");
			while(true){
				System.out.println(sender.receive());
				mex=keyboard.readLine();
				/*while(keyboard.read()!=-1){
					keyboard.readLine();
				}*/
				if(mex.equals("exit"))
					break;
				if(mex.length()>0)
					sender.send(mex);
				


			}

		}catch(Exception e){
			System.out.println("Exception handled");
			e.printStackTrace();

		}finally{
			try{
				s.close();
			}catch(Exception e){
				System.out.println("Exception handled");
				e.printStackTrace();
			}
		}


	}	



}
