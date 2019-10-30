import socket
import sys
import threading

clientSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def recvMessage(sock):
	while True:
		message, address = sock.recvfrom(1024)
		sender, _ = sock.recvfrom(1024)
		print(sender.decode() + ": " + message.decode())
		print("You: ")


t1 = threading.Thread(target = recvMessage, args = (clientSocket, ))
t1.start()

message = input("You: ")

while True:
	if message != "":
		clientSocket.sendto(message.encode(), ('127.0.0.1',1238))
		message = input("You: ")
