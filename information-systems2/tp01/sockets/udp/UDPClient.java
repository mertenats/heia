/**
 * File   : UDPClient.java
 * Author : R. Scheurer (HEIA-FR)
 * Date   : 16.09.2015
 * 
 * Description - a simple UDP client
 * 
 */
package sockets.udp;

import java.net.*;

public class UDPClient {

  DatagramSocket ds;
  DatagramPacket dp;
  byte[]         buffer;

  public UDPClient(int localport) throws SocketException {
    ds = new DatagramSocket(localport);
  }

  public void sendMsg(InetSocketAddress isaDest, String msg) throws Exception {
    dp = new DatagramPacket(msg.getBytes(), msg.length(), isaDest.getAddress(),
        isaDest.getPort());
    ds.send(dp);
  }

  public void closeSocket() {
    ds.close();
  }

  public int getPort() {
    return ds.getLocalPort();
  }

}
