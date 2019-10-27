//============================================================================
// Name        : MyServer.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================
#include <iostream>
#include<string.h>
#include<unistd.h>
#include<fstream>
#include<sys/socket.h>
#include<arpa/inet.h>
#define PORT 9899

using namespace std;

void die(char *s)
{
	cout<<s<<endl;
	exit(1);
}
int main() {

	sockaddr_in serveradd,clientadd;

	int sock=socket(AF_INET, SOCK_DGRAM, 0);

	if(sock==-1){die("Error in socket form");}

	serveradd.sin_family=AF_INET;
	serveradd.sin_port=htons(PORT);
	serveradd.sin_addr.s_addr=INADDR_ANY;

	int binder=bind(sock,(struct sockaddr*)&serveradd,sizeof(serveradd));
	if(binder==-1){die("Error in binding");}
	cout<<"Connected";

	char buffer[1000];



	int slen=sizeof(clientadd);
	socklen_t cli=sizeof(clientadd);
	int msglen;
	msglen=recvfrom(sock, buffer, 1000, 0, (struct sockaddr*)&clientadd, &cli);

	if(msglen<0){die("Error in receiving msg");}
	cout<<"Msg from client is : "<<buffer<<endl;
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
				cout<<"Msg from Client is :";
				msglen=recvfrom(sock, buffer, 1000, 0, (struct sockaddr *)&clientadd,&cli);
				if(msglen<0){die("error in receiving msg");}

				cout<<buffer<<endl;
				bzero((char *)&buffer,sizeof(buffer));
				cout<<"Msg to client is:";
				cin.getline(buffer,sizeof(buffer));

				msglen=sendto(sock, buffer, sizeof(buffer), 0, (struct sockaddr *)&clientadd, cli);

				if(msglen<0){die("Error in sending");}
				if(strcmp(buffer,"bye")==0)
									{
										break;
									}
				bzero((char *)&buffer,sizeof(buffer));
			}

		}
			break;

		case 3:
		{
				char filename[100];
				bzero((char *)&filename,sizeof(filename));
				//recv filename
				msglen=recvfrom(sock,filename, 99,
						0, (struct sockaddr *)&clientadd,&cli);
				if(msglen<0){die("Error in recv filename");}
				cout<<filename<<endl;
				
				
				int filesize;
				msglen=recvfrom(sock,(void *)&filesize, sizeof(filesize),
						0, (struct sockaddr *)&clientadd,&cli);
				if(msglen<0){die("Error in recv filesize");}
				cout<<filesize<<endl;
				
				char *filemsg=new char[filesize];
				//bzero((char *)&filemsg,sizeof(filemsg));
							
				msglen=recvfrom(sock,filemsg,filesize,
						0, (struct sockaddr *)&clientadd,&cli);
				if(msglen==-1){die("Error in recv content");
						}
				
				ofstream fout;
				fout.open(filename,ios::out|ios::binary);
				if(!fout)
				{
					die("Error in creating file");
				}
				else
				{
					fout.write(filemsg,filesize);
					fout.close();
					cout<<"Transfer Successful";
				}


		}
			break;
			
			case 2:
			{
			
				char filename1[100];
				cout<<"Enter filename \n";
				cin>>filename1;
				fstream fout1;
				fout1.open(filename1,ios::in|ios::out|ios::binary);
				fout1.seekg(0,ios::end);

				int filesize1=fout1.tellg();	//get size;
				cout<<filesize1<<endl;

				char *filemsg1=new char[filesize1];
				

				fout1.seekg(0, ios::beg);
				fout1.read(filemsg1, filesize1);

				//send filename;
				msglen=sendto(sock, filename1,strlen(filename1), 0, (struct sockaddr *)&clientadd, slen);
				if(msglen<0){die("Error in sending filename");}

				//send filesize;
				msglen=sendto(sock,(void *) &filesize1,sizeof(filesize1), 0, (struct
					sockaddr *)&clientadd, slen);
				if(msglen<0){die("Error in sending filesize");}

				//send content;

				msglen=sendto(sock,filemsg1,filesize1, 0, (struct
					sockaddr *)&clientadd, slen);
				if(msglen<0){die("Error in sending content");}
				else
				{
					cout<<"Sent successfully\n";
				}
				fout1.close();
			
			
			}

		}

	}while(ch!=0);

}

