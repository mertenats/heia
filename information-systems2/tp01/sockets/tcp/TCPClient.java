/**
 * File   : TCPClient.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 16.09.2015
 * 
 * Description - a simple TCP client
 *
 */
package sockets.tcp;

import java.net.*;
import java.io.*;

public class TCPClient {
  private Socket             s;
  private ObjectOutputStream oos;

  public TCPClient(InetSocketAddress isa) {
    try {
      s = new Socket(isa.getAddress(), isa.getPort());
      oos = new ObjectOutputStream(s.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeSocket() {
    try {
      s.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendMsg(String msg) {
    try {
      oos.writeObject(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
