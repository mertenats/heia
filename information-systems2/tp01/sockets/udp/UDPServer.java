/**
 * File   : UDPServer.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 16.09.2015
 * 
 * Description - a simple UDP server template
 * 
 */
package sockets.udp;

import java.io.IOException;
import java.net.*;

public class UDPServer {

  static final int      MAX_SIZE    = 100;
  static final int      SERVER_PORT = 1200; // port to use
  static DatagramSocket ds;

  public static void main(String[] args) {
    byte[] buffer = new byte[MAX_SIZE];
    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
    int counter = 1;

    System.out.println("Serving UDP port " + SERVER_PORT + "...");

    try {
      ds = new DatagramSocket(SERVER_PORT);
      while (true) {
        dp.setLength(buffer.length);
        ds.receive(dp);

        String msg = new String(buffer, 0, dp.getLength());
        System.out.println("Message " + counter++ + " from : " + dp.getAddress()
            + ":" + dp.getPort() + " -> " + msg);
      }
    } catch (SocketException e) {
      e.printStackTrace();
      ds.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
