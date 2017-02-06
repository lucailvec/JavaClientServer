import java.io.*;
import java.net.*;
import java.util.*;

class FullMagazzinoException extends Exception{}
class EmptyMagazzinoException extends Exception{}

class Magazzino{
	private final int max;
	private int num;
	
	public Magazzino(int m){
		max=m;
	}
	
	public void consume () throws EmptyMagazzinoException{
		if(num>0){
			num--;
		}else
			throw new EmptyMagazzinoException();
	}
	public void produce () throws FullMagazzinoException{
		if(num<max){
			num++;
		}else
			throw new FullMagazzinoException();
	}
	public String toString(){
		return "This mag have: " + num ;
	}
}	

class Sender{
	private final int DIM = 100;
	private SocketAddress sa = null;
	
	public Sender(){
	}
	public void send(String str,SocketAddress sa,DatagramSocket ds) throws IOException {
		final byte[] buffer = str.getBytes();
		final DatagramPacket dp ;
		if(sa==null)
			dp=new DatagramPacket(buffer,0,buffer.length);
		else
			dp=new DatagramPacket(buffer,0,buffer.length,sa);
		
		ds.send(dp);
	}
	public String receive(DatagramSocket ds) throws IOException {
		final byte[] buffer = new byte[DIM];
		final DatagramPacket dp = new DatagramPacket(buffer,DIM);
		ds.receive(dp);
		this.sa=dp.getSocketAddress();
		return new String (dp.getData(),0,dp.getLength());
	}
	public SocketAddress getSocketAddress(){
		return sa;
	}	
}
class Client{
	public static void main(String[] args){
		DatagramSocket ds = null;
		Scanner in = new Scanner(System.in);
		String str ;
		Sender sender = new Sender();
		try{
			ds= new DatagramSocket();
			ds.connect(new InetSocketAddress("localhost",Integer.parseInt(args[0])));
		
			while((str=in.nextLine()).compareTo(".")!=0){
				sender.send(str,null,ds);
				str=sender.receive(ds);
				System.out.println("CLIENT: the server reply-> " + str );
			}
		}catch(ArrayIndexOutOfBoundsException e ){
			System.out.println("Insert the port number");
		}catch(SocketException e ){
			e.printStackTrace();
		}catch(IOException e ){
			e.printStackTrace();
		}finally{
			ds.close();
		}
	}
}
class Server{
	public static void main(String[] args){
		final int DIM = 10;
		DatagramSocket ds = null;
		String str;
		SocketAddress sa=null;
		Magazzino mag = new Magazzino(DIM);

		
		try{
			ds= new DatagramSocket(0);
			System.out.println("SERVER: bound at port :" + ds.getLocalPort());
			Sender sender= new Sender();		
		
			while(true){
				str=sender.receive(ds);
			
				if(sender.getSocketAddress()==null){
					System.out.println("ERROR");
					break;
				}
			
				System.out.println("SERVER: msg from : " + sender.getSocketAddress() + " -> " + str);
			
				switch(str){
					case "+": try{
									mag.produce();
									sender.send(mag.toString(),sender.getSocketAddress(),ds);
								}catch( FullMagazzinoException e ){
									sender.send("Full mag",sender.getSocketAddress(),ds);
								}
							break;
					case "-":try{
									mag.consume();
									sender.send(mag.toString(),sender.getSocketAddress(),ds);
								}catch( EmptyMagazzinoException e ){
									sender.send("Empty mag",sender.getSocketAddress(),ds);
								}
							break;
				
					default:	sender.send("Don't understand!!",sender.getSocketAddress(),ds);
				}
			}
		}catch(SocketException e ){
			e.printStackTrace();
		}catch(IOException e ){
			e.printStackTrace();
		}finally{
			ds.close();
		}
	}
}
