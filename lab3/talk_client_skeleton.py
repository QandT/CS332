import select
from socket import*
import sys
import argparse

HOST = '153.106.116.93'
PORT = 65432
name = gethostname() + ': '
sendQueue = []
outputsNum = []

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

sendQueue.append((name + 'connected').encode())
outputsNum.append((len(sendQueue) - 1))

while True:
    socket_list = [sys.stdin, s]
    read_sockets, write_sockets, error_sockets = select.select(socket_list, outputsNum, [])
    
    for sock in read_sockets:
        if sock == s:
            data = sock.recv(1024)
            if not data:
                break
            else:
                print(data.decode())
        else:
            msg = sys.stdin.readline()
            msg = name + msg
            sendQueue.append(msg.strip().encode())
            outputsNum.append((len(sendQueue) - 1))
            
            
    for place in write_sockets:
        s.sendall(sendQueue[place])
        outputsNum.remove(place)
            
            

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