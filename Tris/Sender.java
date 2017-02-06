import java.io.*;
import java.net.*;

class CloseSocketChannelException extends SocketException {}
public class Sender{
	private InputStream is;
	private OutputStream os;

	private final int DIM = 100;
	public Sender(Socket s ) throws CloseSocketChannelException{
		try{
			is=s.getInputStream();
			os=s.getOutputStream();
		}
		catch(IOException e ) {
			e.printStackTrace();
			throw new CloseSocketChannelException();
		}
	}
	
	public void send(String s )  throws CloseSocketChannelException{
		byte[] arr = s.getBytes();
		try{
			os.write(arr,0,arr.length);
		}catch(IOException e){
			throw new CloseSocketChannelException();
		}
	}
	public String receive() throws CloseSocketChannelException{
		byte[] buffer = new byte [DIM];
		int n = -1;
		try{
			n=is.read(buffer);
		}catch(IOException e){
			throw new CloseSocketChannelException();
		}
		if(n==-1)
			throw new CloseSocketChannelException();
		else if(n==0)
			return "";
		else
			return new String(buffer,0,n);
	}

}
