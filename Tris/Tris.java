import java.util.*;


class AltroGiocatoreSeNeEAndatoException extends Exception{}
class MossaNonConsentitaException extends Exception{}
public class Tris{
	public enum Stato {
		OK,EXIT_PLAYER
	}
	public static final String HELP = "Use 'set sym x y' to set your symbol on table or '?' to get the table";
	private String winner =null;
	String [][] table;
	private Stato stato = Stato.OK;

	public Tris(){
		table = new String [3][3];
		
		for (String[] row: table)
    			Arrays.fill(row, "-");
	} 

	public void setSymbol(String sym, int x, int y) throws MossaNonConsentitaException{
		
		if(table[x][y].equals("-") && (x>=0 && x<=2 && y>=0 && y<=2))
			table[x][y]=sym;
		else
			throw new MossaNonConsentitaException();
	}

	public String getTable(){
		
		return (table[0][0] + " | " + table[1][0] + " | " + table[2][0] + "\n" +
			table[0][1] + " | " + table[1][1] + " | " + table[2][1] + "\n" +
			table[0][2] + " | " + table[1][2] + " | " + table[2][2]
		);
	}

	public synchronized void startRound(){

			notify();	
			
		
	}
	public String getWinner(){
		return this.winner;
	}
	public	void setMeNeVado() throws AltroGiocatoreSeNeEAndatoException{
		if(stato == Tris.Stato.EXIT_PLAYER)
			throw new AltroGiocatoreSeNeEAndatoException();
		else
			stato = Tris.Stato.EXIT_PLAYER;
	}
	public Stato getStato(){
		return stato;
	}
	public boolean isWinner() {
		//player 1 ha la oh o
		//player 2 ha la ics x
		//è la prima istruzione ... è brutto fare il controllo qua se l' altro giocatore se ne è andato
		for(int x =0; x<2;x++){
			if(table[x][0].equals(table[x][1]) && table[x][1].equals(table[x][2]) && ! table[x][1].equals("-")){//non è inizializzato
				winner = table[x][0];
				return true;
			
			}
		}
		for(int x=0;x<2;x++){
			if(table[0][x].equals(table[1][x]) && table[1][x].equals(table[2][x]) && ! table[1][x].equals("-")){//non è inizializzato
				winner = table[0][x];
				return true;
			
			}
		}
		
		if(table[0][0].equals(table[1][1]) && table[1][1].equals(table[2][2]) && ! table[1][1].equals("-")){
			winner = table[0][0];
			return true;
		}
		if(table[2][0].equals(table[1][1]) && table[1][1].equals(table[0][2]) && ! table[1][1].equals("-")){
			winner = table[2][0];
			return true;
		}
		return false;
	}
	public synchronized void endRound(){

		try{
			wait();	
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
