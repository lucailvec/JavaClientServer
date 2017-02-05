import java.io.*;
import java.net.*;

public class MultiTrisThreadServer{

	public static void main(String [] args){

		
		int port = 55000;
		ServerSocket s =null;
		int player=1;
		
		Socket toCln=null;
		Tris t = new Tris();

		try{

		s = new ServerSocket(port);
		

			while(true){

				toCln = s.accept();	
					player++;
					ServiceThread sThread = new ServiceThread(toCln, player, t);
					new Thread(sThread).start();
				
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
