import java.io.*;
import java.net.*;

public class ServiceThread extends Thread{

	Socket toCln;
	int player=0;
	Tris t;

	public ServiceThread(Socket s, int num, Tris t){
	
		toCln = s;
		this.t = t;
		player=num;
	}

	public void run(){

		Sender sender = null;
		byte[] buffer = new byte [1000];
		int length;
		int items;
		String[] str ;
		String symbol;
		String mexToCln;

		try{
		 sender = new Sender(toCln);			
		 if(player ==1){
			sender.send("METTITI AD ASPETTARE L' ALTRO GIOCATORE");
		 	t.endRound();	
		 }
			
		
	
			
			while(true){
				if(t.isWinner()){//deve essere prima della read se no ciao
					//qualcuno ha vinto non si sa ancora chi
					sender.send("Qualcuno ha vinto bravohhhhh");
					break;
				}
				
				str=sender.read().split(" ");


				switch (str[0]){

				/*case "play" :
				    	 out.write("Game table created. Use 'help' to continue.".getBytes());
					 break;*/

				case "set" : 
					 t.setSymbol(str[1],Integer.parseInt(str[2]),Integer.parseInt(str[3]));
					 sender.send("Set the symbol");
					 break;

				case "?" : 	
					 mexToCln = "Current table : \n" + t.getTable();
					 sender.send(mexToCln);
					 break;

				default:
					mexToCln = "Use 'set sym x y' to set your symbol on table or '?' to get the table";
					sender.send(mexToCln);				
					break;

				}	
				
				if(t.isWinner()){
					//chiudi tutto e notifica per risvegliare l' altro thread
				}else{
					table.startRound();
					 table.endRound();	
				}
			}

			toCln.close();
			System.out.println("Client socket closed");
			
		}catch(CloseSocketChannelException e ){
			System.out.println("SERVER: il client ha interrotto la comunicazione");
			//chiudi la socket e sveglia gli altri
		}
		catch(Exception e){
			System.out.println("Exception handled");
			e.printStackTrace();

		}finally{
			try{
				toCln.close();
			}catch(Exception e){
				System.out.println("Exception handled");
				e.printStackTrace();
			}
		}
	}
}
