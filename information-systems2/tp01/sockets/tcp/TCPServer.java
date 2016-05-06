/**
 * File   : TCPServer.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 16.09.2015
 * 
 * Description - a simple TCP server template
 *
 */
package sockets.tcp;

import java.net.*;
import java.io.*;

public class TCPServer {

  static final int    SERVER_PORT = 8765; // server port to use
  static int          id          = 0;       // client id
  static ServerSocket serverSocket;

  public static void main(String[] args) {
    try {
      serverSocket = new ServerSocket(SERVER_PORT);
      serverSocket.setReceiveBufferSize(512);
      while (true) {
        Socket s = serverSocket.accept();
        System.out.println("Listening on TCP port " + s.getLocalPort() + "...");
        ServerThread serverThread = new ServerThread(s, id++);
        serverThread.start();
      }
    } catch (Exception e) {
    } finally {
      try {
        serverSocket.close();
      } catch (IOException e) {
      }
    }
  }
}
