import java.net.*;
import java.io.*;
public class Server {
	
	private ServerSocket serv = null;      		//server side socket
	private Socket sock = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	
	public Server(){
		try {
			
			serv = new ServerSocket(25100);		//create server socket, give port number
			System.out.println("!!!.....Server Started.....!!!");

			System.out.println("!!!.....Waiting For Client.....!!!");
			
			sock = serv.accept();				//accept the client if no error
			
			System.out.println("!!!.....Client Connected.....!!!");
			
			dis = new DataInputStream(sock.getInputStream());			//take input from socket
			dos = new DataOutputStream(sock.getOutputStream());			//send output to socket
		}
		catch(Exception e) {
			System.out.println("Error Caught In Constructor");
			System.err.println(e);
		}
	}
	
	public void dataTransfer() {
		try {
			boolean f = dis.readBoolean();
			int i=0, frames, rec;
			frames = dis.readInt();		//read frames from socket
			int[] data = new int[frames];
			while(i<frames) {
				rec = dis.readInt();	//received frame
				if(rec == i || f) {
					System.out.println(rec + " recieved");
					data[rec] = rec;
					dos.writeInt(rec);
					System.out.println("Acknowledgement sent\n\n");
					i++;
				}
			}			
		}
		catch(Exception e) {
			System.out.println("Exception caught in data transfer");
			System.err.println(e);
		}
		
	}
	
	public void closeAll() {
		try {
			dos.close();
			dis.close();
			sock.close();
			serv.close();
		}
		catch(Exception e) {
			System.out.println("Error Caught In Close All");
			System.err.println(e);
		}
	}
	
	public static void main(String args[]) {
		Server obj = new Server();			//call constructor, setup connection
		obj.dataTransfer();					//perfom data transfer
		obj.closeAll();						//close objects
	}
}
