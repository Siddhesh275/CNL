
import java.net.*;          //Establish network connection
import java.io.*;			//For iostream
import java.util.*;

public class Client {

	private Socket sock = null;					//Initialise objects required
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Scanner cin = null;

	public Client() {
		String add = InetAddress.getLoopbackAddress().getHostAddress();		//get own IP address
		try {
			sock = new Socket(add, 25100);			//create socket, allocate ip and port number

			System.out.println("!!!.....Connection Established.....!!!");

			dis = new DataInputStream(sock.getInputStream());		//take input from socket	
			dos = new DataOutputStream(sock.getOutputStream());		//send output to socket

			cin = new Scanner(System.in);

			int ch;										//Directly call the GoBackN or SelectiveRepeat method instead of this
			do {
				System.out.println("\nSelect\n1. Go Back N\n2. Selective Repeat\nEnter Choice");
				ch = cin.nextInt();
			}while(ch!=1 && ch!=2);

			if(ch == 1) {
				dos.writeBoolean(false);
				this.dataTransferWithGBN();
			}
			else {
				dos.writeBoolean(true);
				this.dataTransferWithSelectiveRepeat();
			}
		}
		catch(Exception e) {
			System.out.println("Exception caught in constructor");
			System.err.println(e);
		}
	}

	public void dataTransferWithSelectiveRepeat() {
		try {
			boolean err = false;			//initialise err to false
			int frames, window, i=0, temp = 0;
			do {							
				System.out.println("\n1. With Error\n2. Without Error\nEnter choice");
				i = cin.nextInt();
			}while(i!=1 && i!=2);
			if(i == 1) {					//if user wants error, set error = 1
				err = true;
			}
			System.out.print("\nEnter number of frames to transfer : ");
			frames = cin.nextInt();
			dos.writeInt(frames);
			System.out.print("\nEnter size of window : ");
			window = cin.nextInt();
			i=0;
			boolean[] f = new boolean[frames];
			while(i<frames) {			//initialise f array with false
				f[i] = false;
				i++;
			}
			i=0;
			while(i<frames) {						//For all frames
				boolean inc = true;
				for(int j=0; j<window && i+j<frames; j++) {			//for each window send packet & choose if it is lost
					temp = 0;										//i+j<frames is for the last window 
					if(!f[i+j]) {
						System.out.println("\n\n" + (i+j) + " Sent");
						if(err) {
							System.out.println("Enter 1 to mark packet as lost");
							temp = cin.nextInt();
						}
						if(temp!=1) {
							dos.writeInt(i+j);						//i+j packet has no error
						}
					}
				}
				System.out.println("\n\n");
				for(int j=0; j<window && i+j<frames; j++) {			//for each window state if ack recieved or not
					sock.setSoTimeout(2000);
					try {
						int t = dis.readInt();

						System.out.println("\nAcknowledgement for " + t + " recieved");
						f[t] = true;								//set f[t] = true if successfully sent
					}
					catch(Exception e) {
						int t = 0;
						while(i+t<frames && f[t+i] ) {			//move to the packet which wasn't sent
							t++;
						}			
						if(t<window) {
							System.out.println("\nAcknowledgement for " + t + " not recieved");
							System.out.println("Resending\n");
							j = window;
							inc = false;
						}
					}
				}
				while(i<frames && inc) {			//move to next packet only if all packets of the current window are sent
					if(f[i]) {
						i++;
					}
					else {
						break;
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("Exception caught in data transfer");
			System.err.println(e);
		}
	}

	public void dataTransferWithGBN() {
		try {
			boolean err = false;
			int frames, window, i=0, temp = 0;
			do {
				System.out.println("\n1. With Error\n2. Without Error\nEnter choice");
				i = cin.nextInt();
			}while(i!=1 && i!=2);
			if(i == 1) {
				err = true;
			}
			System.out.print("\nEnter number of frames to transfer : ");
			frames = cin.nextInt();
			dos.writeInt(frames);
			System.out.print("\nEnter size of window : ");
			window = cin.nextInt();
			i=0;
			while(i<frames) {					//for all frames
				for(int j=0; j<window && i+j<frames; j++) {		//for each window, send all packets & chose which is not sent
					temp = 0;
					System.out.println("\n\n" + (i+j) + " Sent");
					if(err) {
						System.out.println("Enter 1 to mark packet as lost");

						temp = cin.nextInt();
					}
					if(temp!=1) {
						dos.writeInt(i+j);
					}
				}
				System.out.println("\n\n");
				int cnt = 0;
				for(int j=0; j<window && i+j<frames; j++) {		//for each window, state if ack recieved or not
					sock.setSoTimeout(3000);
					try {
						if(dis.readInt() == i+j) {
							cnt++;
							System.out.println("Acknowledgement for " + (i+j) + " recieved");
						}
						else {
							Thread.sleep(500);
						}
					}
					catch(Exception e) {
						if(i+cnt < frames) {					//resend the packet that was not sent properly
							System.out.println("\nAcknowledgement for " + (i+cnt) + " not recieved");
							System.out.println("Resending\n");
						}
						j = window;
					}
				}
				dos.flush();
				i += cnt;
			}
		}
		catch(Exception e) {
			System.out.println("Exception caught in data transfer");
			System.err.println(e);
		}
	}

	public void closeAll() {			//close dos, dis and sock objects
		try {
			dos.close();
			dis.close();
			sock.close();
		}
		catch(Exception e) {
			System.out.println("Error Caught In Close All");
			System.err.println(e);
		}
	}

	public static void main(String args[]) {
		Client obj = new Client();				//call constructor.. all action occurs in constructor
		obj.closeAll();							//call closeAll() to close all objects
	}
}
