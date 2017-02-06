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
			sender.send("Benvenuto giocatore "+ t.getSymbol(player) + " METTITI AD ASPETTARE L' ALTRO GIOCATORE");
		 	t.endRound();	
		 }else{
			 sender.send("Benvenuto giocatore "+ t.getSymbol(player) + " Tocca a te:");	
		 }
			
		
	
			
			while(true){
				if(t.getStato()==Tris.Stato.EXIT_PLAYER){
					sender.send("Hai vinto a tavolino, complimenti un par di balle!");
					throw new AltroGiocatoreSeNeEAndatoException();
				}
				if(t.isWinner()){//deve essere prima della read se no ciao
					if(player==1 && t.getWinner().equals("o")){
						sender.send("Hai vinto bravohhhhh" + "\n Current table : \n" + t.getTable());
					}
					else{
						sender.send("Hai PERSO bravohhhhh" + "\n Current table : \n" + t.getTable());
					}
					//risveglio gli altri prima di chiudermi
					t.startRound();
					break;
				}
				
				str=sender.receive().split(" ");


				switch (str[0]){

					case "set" : 
						try{
						 
						 t.setSymbol(player,Integer.parseInt(str[1]) - 1,Integer.parseInt(str[2]) - 1);
						 //finito il turno
							t.startRound();
							sender.send("Ti invio le cose se no ti imbamboli" + "Current table :  " + t.getTable() + "  Ora è il turno dell' altro, ti addormenti...");
							t.endRound();
							
						
						} catch(MossaNonConsentitaException e){
							
							sender.send("Mossa non consentita (MossaNonConsentitaException)");
						}catch(ArrayIndexOutOfBoundsException e ){
							
							sender.send("Mossa non compresa, riprova o digita ? (ArrayIndexOutOfBoundsException)");
						}catch(java.lang.NumberFormatException e ){
							
							sender.send("Errore deve essere: set x y");
						}
						break;
						

					case "?" : 	
						 sender.send("Current table : \n" + t.getTable());
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
