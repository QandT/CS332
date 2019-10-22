import select
from socket import*
import sys
import argparse

HOST = '153.106.116.78'
PORT = 65432

parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-n", "--name", dest="name", help="name to be prepended in messages (default: machine name)")
parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="TCP port the server is listening on (default 12345)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

s = socket(AF_INET, SOCK_STREAM)
s.connect((HOST, PORT))
# s.sendall(b'Hello, world')
# data = s.recv(1024)

# print('Received', repr(data))

s.sendall(b'Connected')

while True:
    socket_list = [sys.stdin, s]
    read_sockets, write_sockets, error_sockets = select.select(socket_list, [], [])
    
    for sock in read_sockets:
        if sock == s:
            data = sock.recv(1024)
            if not data:
                break
            else:
                print(repr(data))
        else:
            msg = sys.stdin.readline()
            s.send(bytes(msg, 'utf-8'))
            

# while True:
#     socket_list = [sys.stdin, s]

#     # Get the list sockets which are readable
#     read_sockets, write_sockets, error_sockets = select.select(
#         socket_list, [], [])

#     for sock in read_sockets:
#         #incoming message from remote server
#         if sock == s:
#             data = sock.recv(1024)
#             if not data:
#                 print('\nDisconnected from server')
#                 break
#             else:
#                 #print data
#                 # sys.stdout.write(data)
#                 # prints the message received
#                 print("New message: " + repr(data))
#             #user entered a message
#         else:
#             msg = sys.stdin.readline()
#             s.send(msg)

# close()