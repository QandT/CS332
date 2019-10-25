import select
from socket import*
import sys
import argparse

parser = argparse.ArgumentParser(description="A prattle client")

parser.add_argument("-n", "--name", dest="name", default=gethostname(), help="name to be prepended in messages (default: machine name)")
parser.add_argument("-s", "--server", dest="server", default="127.0.0.1",
                    help="server hostname or IP address (default: 127.0.0.1)")
parser.add_argument("-p", "--port", dest="port", type=int, default=12345,
                    help="TCP port the server is listening on (default 12345)")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose",
                    help="turn verbose output on")
args = parser.parse_args()

print(args)
name = args.name
HOST = args.server
PORT = args.port
isVerbose = args.verbose

nameToPrint = name + ': '

s = socket(AF_INET, SOCK_STREAM)
s.connect((HOST, PORT))

s.sendall((nameToPrint + 'connected').encode())

while True:
    # print(s)
    try:
        if s.getpeername() is None:
            print("Error Quitting from server crash, its about to be ugly\n")
    except OSError:
        print("Server Closed")
        break
    # print(s.getpeername())
        
    socket_list = [sys.stdin, s]
    read_sockets, write_sockets, error_sockets = select.select(socket_list, [], [])
    
    for sock in read_sockets:
        if sock == s:
            data = sock.recv(1024)
            if not data:
                break
            else:
                print(data.decode())
        else:
            msg = sys.stdin.readline()
            msg = nameToPrint + msg
            s.sendall(msg.strip().encode())
            
# <socket.socket fd=3, family=AddressFamily.AF_INET, type=SocketKind.SOCK_STREAM, proto=0, laddr=('153.106.116.62', 57226), raddr=('153.106.116.62', 65432)>
# <socket.socket fd=3, family=AddressFamily.AF_INET, type=SocketKind.SOCK_STREAM, proto=0, laddr=('153.106.116.62', 57222), raddr=('153.106.116.62', 65432)>
# <socket.socket fd=3, family=AddressFamily.AF_INET, type=SocketKind.SOCK_STREAM, proto=0, laddr=('153.106.116.62', 57256)>

            
            
            

# # while True:sock
# #     socket_list = [sys.stdin, s]

# #     # Get the list sockets which are readable
# #     read_sockets, write_sockets, error_sockets = select.select(
# #         socket_list, [], [])

# #     for sock in read_sockets:
# #         #incoming message from remote server
# #         if sock == s:
# #             data = sock.recv(1024)
# #             if not data:
# #                 print('\nDisconnected from server')
# #                 break
# #             else:
# #                 #print data
# #                 # sys.stdout.write(data)
# #                 # prints the message received
# #                 print("New message: " + repr(data))
# #             #user entered a message
# #         else:
# #             msg = sys.stdin.readline()
# #             s.send(msg)

# # close()

# import select, socket, sys
# server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# server.setblocking(0)
# server.bind(('153.106.116.93', 50000))
# server.listen(5)
# inputs = [server]
# outputs = []
# message_queues = {}

# while inputs:
#     readable, writable, exceptional = select.select(
#         inputs, outputs, inputs)
#     for s in readable:
#         if s is server:
#             connection, client_address = s.accept()
#             connection.setblocking(0)
#             inputs.append(connection)
#             message_queues[connection] = Queue.Queue()
#         else:
#             data = s.recv(1024)
#             if data:
#                 message_queues[s].put(data)
#                 if s not in outputs:
#                     outputs.append(s)
#             else:
#                 if s in outputs:
#                     outputs.remove(s)
#                 inputs.remove(s)
#                 s.close()
#                 del message_queues[s]

#     for s in writable:
#         try:
#             next_msg = message_queues[s].get_nowait()
#         except Queue.Empty:
#             outputs.remove(s)
#         else:
#             s.send(next_msg)

#     for s in exceptional:
#         inputs.remove(s)
#         if s in outputs:
#             outputs.remove(s)
#         s.close()
#         del message_queues[s]