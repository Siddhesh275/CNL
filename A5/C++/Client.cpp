//============================================================================
// Name        : MyClient.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include<string.h>
#include<unistd.h>
#include<sys/socket.h>
#include<arpa/inet.h>
#include<fstream>
using namespace std;
#define PORT 9899
#define SERVER_ADDRESS "127.0.0.1"


void die(char *s)
{
	perror(s);
	exit(1);
}
int main() {


	sockaddr_in serveradd;
	int sock;

	sock=socket(AF_INET, SOCK_DGRAM, 0);

	if(sock==-1)
	{
		die("Error in socket creation");
	}

	serveradd.sin_family=AF_INET;
	serveradd.sin_port=htons(PORT);
	serveradd.sin_addr.s_addr=inet_addr(SERVER_ADDRESS);

	char buffer[1000];
	bzero((char *)&buffer,sizeof(buffer));
	cout<<"Enter msg to connect server\n";
	cin.getline(buffer, 999);

	int slen=sizeof(serveradd);
	socklen_t cli=sizeof(serveradd);
	int msglen;
	msglen=sendto(sock,buffer, sizeof(buffer), 0, (struct sockaddr *)&serveradd,slen);


	int ch;
		do
		{
			cout<<"1.Chat\n2.Send\n3.Receive";
			cin>>ch;

			switch(ch)
			{
			case 1:
			{
				while(1)
				{
					bzero((char *)&buffer,sizeof(buffer));
					cout<<"Msg to server is:";
					cin.getline(buffer,sizeof(buffer));

					msglen=sendto(sock, buffer, sizeof(buffer), 0, (struct sockaddr *)&serveradd, cli);

					bzero((char *)&buffer,sizeof(buffer));
					cout<<"Msg from server is :";
					msglen=recvfrom(sock, buffer, 1000, 0, (struct sockaddr *)&serveradd,&cli);
					if(msglen<0){die("error in receiving msg");}

					cout<<buffer<<endl;

					if(strcmp(buffer,"bye")==0)
					{
						break;
					}

					if(msglen<0){die("Error in receiving");}
					bzero((char *)&buffer,sizeof(buffer));
				}

			}
				break;

			case 2:
				//send
			{
				char filename[100];
				cout<<"Enter filename \n";
				cin>>filename;
				fstream fout;
				fout.open(filename,ios::in|ios::out|ios::binary);
				fout.seekg(0,ios::end);

				int filesize=fout.tellg();	//get size;
				cout<<filesize<<endl;

				char *filemsg=new char[filesize];
				

				fout.seekg(0, ios::beg);
				fout.read(filemsg, filesize);

				//send filename;
				msglen=sendto(sock, filename,strlen(filename), 0, (struct
						sockaddr *)&serveradd, slen);
				if(msglen<0){die("Error in sending filename");}

				//send filesize;
				msglen=sendto(sock,(void *) &filesize,sizeof(filesize), 0, (struct
					sockaddr *)&serveradd, slen);
				if(msglen<0){die("Error in sending filesize");}

				//send content;

				msglen=sendto(sock,filemsg,filesize, 0, (struct
					sockaddr *)&serveradd, slen);
				if(msglen<0){die("Error in sending content");}
				else
				{
					cout<<"Sent successfully\n";
				}
				fout.close();
			
			}
				break;
			
			case 3:
			{
				
				char filename1[100];
				bzero((char *)&filename1,sizeof(filename1));
				//recv filename
				msglen=recvfrom(sock,filename1, 99,
						0, (struct sockaddr *)&serveradd,&cli);
				if(msglen<0){die("Error in recv filename");}
				cout<<filename1<<endl;
				
				
				int filesize1;
				msglen=recvfrom(sock,(void *)&filesize1, sizeof(filesize1),
						0, (struct sockaddr *)&serveradd,&cli);
				if(msglen<0){die("Error in recv filesize");}
				cout<<filesize1<<endl;
				
				char *filemsg1=new char[filesize1];
				//bzero((char *)&filemsg,sizeof(filemsg));
							
				msglen=recvfrom(sock,filemsg1,filesize1,
						0, (struct sockaddr *)&serveradd,&cli);
				if(msglen==-1){die("Error in recv content");
						}
				
				ofstream fout1;
				fout1.open(filename1,ios::out|ios::binary);
				if(!fout1)
				{
					die("Error in creating file");
				}
				else
				{
					fout1.write(filemsg1,filesize1);
					fout1.close();
					cout<<"Transfer Successful";
				}
			
			
			}			
				break;
			
			
						
			}
		}while(ch!=0);

	return 0;
}

