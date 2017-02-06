import java.util.*;
import java.net.*;
import java.io.*;


class ChannelClosedException extends Exception{}

class Client{
	public static void main(String[] args){
		Socket s=null;
		Scanner in = new Scanner(System.in);
		Sender sender;		
		try{
			s=new Socket();
			s.connect(new InetSocketAddress("localhost",Integer.parseInt(args[0])));
			sender=new Sender(s);
			String str;
			while(true){
				str=in.nextLine();
				if(str.length()>0){
					sender.send(str);
					str=sender.receive();
					System.out.println("CLIENT: the server reply-> " + str);
				}else
					System.out.println("CLIENT: you must enter a string of length >= 1");
			}
		
		}catch(IOException e ){
			e.printStackTrace();
		
		}catch(ChannelClosedException e ){
			System.out.println("CLIENT: error on the channel");
		}finally{
			try{
				System.out.println("I'm tring to close the socket...");
				s.close();
			}catch(IOException e ) {
				e.printStackTrace();
			}
		}
	}
}
class Server{
	public static void main(String [] args){
		ServerSocket ss=null;
		Socket s=null;
		Store store = new Store(10);
		int numThread=1;
		try{
			ss=new ServerSocket(0);
			System.out.println("SERVER: bind at :" + ss.getLocalPort());
			
			while(true){
				s=ss.accept();
				System.out.println("SERVER: connection incoming from: " + s.getInetAddress());
				
				Thread t = new ServerThread(s,store,numThread);
				t.start();
				numThread++;
				
			}
		}catch(IOException e ){
			e.printStackTrace();
		
		}finally{
			try{
				s.close();
			}catch(IOException e ){
				e.printStackTrace();
			}
		}
	}
}

class ServerThread extends Thread{
	private final Socket s;
	private final Sender sender;
	private final Store store;
	private final int numThread;
	
	public ServerThread(Socket sa,Store storea,int num){
		s=sa;
		store=storea;
		numThread=num;
		sender= new Sender(s);
	}
	
	public void run(){
		String str;
		int past;
		try{
			while(true){
				str=sender.receive();
				System.out.println("Server-THREAD-"+ numThread +": the client send me: " + str);
				
				switch(str){
				
					case "+":
							past = store.getNumItems();	
							try{
								store.produce();
								sender.send("You produce 1 unit, we get from " + past + " to "+ store.getNumItems() + " items"); 
							}catch(FullStoreException e){
								sender.send("You can't produce unit, we are full: "+ store.getNumItems() + " items"); 
							}
							break;
					case "-":
							 past = store.getNumItems();	
							try{
								store.consume();
								sender.send("You consume 1 unit, we get from " + past + " to "+ store.getNumItems() + " items"); 
							}catch(EmptyStoreException e){
								sender.send("You can't produce unit, we are full: "+ store.getNumItems() + " items"); 
							}
							break;
					case "?":
							sender.send("We have: "+ store.getNumItems() + " items"); 
							break;
							
					default: sender.send("I don't understand!!");
				}
			
			}
		}catch(ChannelClosedException e ) {
		
		}finally{
			try{
				System.out.println("Server-THREAD-"+ numThread +": i'm closing the socket");
				s.close();
			}catch(IOException e ){
				e.printStackTrace();
			}
			
		}
	}
}

class FullStoreException extends Exception{}
class EmptyStoreException extends Exception{}

class Store{
	private final int MAX;
	private int num=0;
	
	public Store(int n){
		MAX=n;
	}
	public int getNumItems(){
		return num;
	}
	public void produce() throws FullStoreException{
		if(num<MAX){
			num++;
		}else
			throw new FullStoreException();
	}
	public void consume() throws EmptyStoreException{
		if(num>0){
			num--;
		}else
			throw new EmptyStoreException();
	}
}
class Sender{
	 InputStream is;
	 OutputStream os;
	
	public Sender(Socket s){
		try{
			is=s.getInputStream();
			os=s.getOutputStream();
		}catch(IOException e ){
			e.printStackTrace();
		} 		
	}
	public void send(String s) throws ChannelClosedException {
		byte [] buffer = s.getBytes();
		try{
			os.write(buffer,0,buffer.length);
		}catch(IOException e){
			throw new ChannelClosedException();
		}
	}
	public String receive() throws ChannelClosedException{
		final int DIM = 100;
		byte [] buffer = new byte[DIM];
		
		int n=-1;
		try{
			n=is.read(buffer,0,DIM);
			if(n==-1)
				throw new ChannelClosedException();
			else
				return new String(buffer,0,n);
		}catch(IOException e){
			throw new ChannelClosedException();
		}
	}
}
