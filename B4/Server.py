import socket
import sys

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serverSocket.bind(('127.0.0.1', 1238))

Clients = []

while True:
	message, address = serverSocket.recvfrom(1024)
	if (address not in Clients):
		Clients.append(address)
		print(str(address) + " has joined!")
		print(str(address) + ": " + message.decode())

	print(str(address) + ": " + message.decode())

	for addr in Clients:
		if(addr != address):
			serverSocket.sendto(message, addr)
			serverSocket.sendto(str(address).encode(), addr)
