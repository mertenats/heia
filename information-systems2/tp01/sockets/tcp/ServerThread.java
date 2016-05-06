package sockets.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerThread extends Thread {

  private Socket socket;
  private int    id;

  public ServerThread(Socket s, int id) {
    this.socket = s;
    this.id = id;
  }

  public void run() {
    System.out.println(">> " + id + " : New connection request from "
        + socket.getInetAddress() + ":" + socket.getPort());
    try {
      ObjectInputStream data = new ObjectInputStream(socket.getInputStream());
      while (true) {
        String msg = (String) data.readObject();
        System.out.println("@@ " + id + " : " + msg);
      }
    } catch (IOException | ClassNotFoundException e1) {
      try {
        socket.close();
        System.out.println("## " + id + " : connection closed");
      } catch (Exception e2) {
      }
    }
  }

}
