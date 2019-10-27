import java.io.*; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner; 

public class server 
{ 
	public static void main(String[] args) throws IOException 
	{ 
		Scanner sc = new Scanner(System.in);
		DatagramSocket ds = new DatagramSocket(1234); 
		DatagramSocket ds1 = new DatagramSocket();
		byte[] receive = new byte[65535]; 
		DatagramPacket DpReceive = null; 
		InetAddress ip = InetAddress.getByName("localhost"); 
		byte buf[] = null;
		while (true) 
		{ 
			DpReceive = new DatagramPacket(receive, receive.length); 
			ds.receive(DpReceive); 
			System.out.println("Client: " + data(receive)); 
			if (data(receive).toString().equals("bye")) 
			{ 
				System.out.println("Client sent bye.....EXITING"); 
				break; 
			} 
			receive = new byte[65535];
			System.out.print("Server: ");
			String inp = sc.nextLine(); 
			buf = inp.getBytes(); 
			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 6666); 
			ds1.send(DpSend); 
			if (inp.equals("bye")) 
				break;
		}
		FileOutputStream f = new FileOutputStream("yooooo.txt");
		byte[] b1 = new byte[1000];
		DatagramPacket dprecv = new DatagramPacket(b1, b1.length);
		ds.receive(dprecv);
		f.write(b1, 0, b1.length);
		ds.close();
		sc.close();
		ds1.close();
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
