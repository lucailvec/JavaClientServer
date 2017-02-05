import java.io.*;
import java.net.*;

class ServiceThread extends Thread{

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
				if(t.getStato()==Tris.Stato.EXIT_PLAYER){
					sender.send("Hai vinto a tavolino, complimenti un par di balle!");
					throw new AltroGiocatoreSeNeEAndatoException();
				}
			
				if(t.isWinner()){//deve essere prima della read se no ciao
					if(player==1 && t.getWinner().equals("o")){
						sender.send("Hai vinto bravohhhhh");
						sender.send( "Current table : \n" + t.getTable());
					}
					else{
						sender.send("Hai PERSO bravohhhhh");
						sender.send( "Current table : \n" + t.getTable());
					}
					//risveglio gli altri prima di chiudermi
					t.startRound();
					break;
				}
				
				str=sender.receive().split(" ");


				switch (str[0]){

					case "set" : 
						try{
						 
						 t.setSymbol(str[1],Integer.parseInt(str[2]) + 1,Integer.parseInt(str[3]) +1);
						
						} catch(MossaNonConsentitaException e){
							
							sender.send("Mossa non consentita");
						}catch(ArrayIndexOutOfBoundsException e ){
							
							sender.send("Mossa non compresa, riprova o digita ?");
						}
						 sender.send("Set the symbol");
						//finito il turno
						t.startRound();
						t.endRound();	
						 break;

					case "?" : 	
						 mexToCln = "Current table : \n" + t.getTable();
						 sender.send(mexToCln);
						 break;
					case "exit":
						sender.send("hai deciso di perdere a  tavolino ciao");
						t.setMeNeVado();
						break;
					default:
						sender.send(Tris.HELP);				
						break;

				}	
				
				

			}

			toCln.close();
			System.out.println("Client socket closed");
			
		}catch(AltroGiocatoreSeNeEAndatoException e){
			t.startRound();
			try{
				sender.send("addio");
			}catch(CloseSocketChannelException ep ){
				System.out.println("SERVER: un giocatore ha interrotto la comunicazione");
			}
		}catch(CloseSocketChannelException e ){
			System.out.println("SERVER: il client ha interrotto la comunicazione");
			t.startRound();
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
