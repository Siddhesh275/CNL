import java.io.*;
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress; 
import java.util.Scanner; 

public class client 
{ 
	public static void main(String args[]) throws IOException 
	{ 
		Scanner sc = new Scanner(System.in); 
		DatagramSocket ds = new DatagramSocket();
		DatagramSocket ds1 = new DatagramSocket(6666);
		InetAddress ip = InetAddress.getByName("localhost"); 
		byte buf[] = null; 
		byte[] receive = new byte[65535]; 
		DatagramPacket DpReceive = null; 
		while (true) 
		{ 
			System.out.print("Client: ");
			String inp = sc.nextLine(); 
			buf = inp.getBytes(); 
			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 1234); 
			ds.send(DpSend); 
			if (inp.equals("bye")) 
				break;
			DpReceive = new DatagramPacket(receive, receive.length); 
			ds1.receive(DpReceive); 
			System.out.println("Server: " + data(receive)); 
			if (data(receive).toString().equals("bye")) 
			{ 
				System.out.println("Server sent bye.....EXITING"); 
				break; 
			} 
			receive = new byte[65535];			
		}
		FileInputStream f = new FileInputStream("hi.txt");
		File f1 = new File("hi.txt");
		int fs = (int) f1.length();
		byte[] buffer = new byte[fs];
		f.read(buffer, 0, buffer.length);
		DatagramPacket dpsend = new DatagramPacket(buffer, buffer.length, ip, 1234);
		ds.send(dpsend);
		sc.close();
		ds.close();
		ds1.close();
		f.close();
	} 
	public static StringBuilder data(byte[] a) 
	{ 
		if (a == null) 
			return null; 
		StringBuilder ret = new StringBuilder(); 
		int i = 0; 
		while (a[i] != 0) 
		{ 
			ret.append((char) a[i]); 
			i++; 
		} 
		return ret; 
	}
} 
