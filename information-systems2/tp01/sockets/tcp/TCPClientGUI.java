/**
 * File    : TCPClientGUI.java
 * Author  : R. Scheurer (HEIA-FR)
 * Version : v1.6.2 / 16.09.2015
 * 
 * Description - a very simple GUI for a simple TCP client
 *
 * DO NOT MODIFY THIS FILE ! (Exception: extension task)
 */
package sockets.tcp;

import java.awt.*;
import java.awt.event.*;
import java.net.*;

@SuppressWarnings("serial")
public class TCPClientGUI extends Frame implements ActionListener {

  final static String VERSION      = "v1.6.2";
  final static int    DEFAULT_PORT = 8765;
  final static String DEFAULT_HOST = "127.0.0.1";
  final static String EXIT         = "Exit";
  final static String CONNECT      = "Connect";
  final static String DISCONNECT   = "Disconnect";
  final static String SEND         = "Send";

  InetSocketAddress isa;
  TCPClient         client;
  TextField         hostField;
  Label             hostLabel;
  TextField         portField;
  Label             portLabel;
  Button            hostButton;
  TextField         msgField;
  Label             msgLabel;
  Button            sndButton;
  Label             statusLabel;
  Button            exitButton;

  // -----------------------------------------------------------------
  // Constructor (includes GUI setup)
  // -----------------------------------------------------------------
  public TCPClientGUI() {
    this.setLayout(null);

    // Host information
    hostField = new TextField(15);
    hostField.setText(DEFAULT_HOST);
    add(hostField);
    hostField.setBounds(80, 50, 200, 30);

    hostLabel = new Label("Host :");
    add(hostLabel);
    hostLabel.setBounds(10, 50, 40, 30);

    // Port information
    portLabel = new Label("Port :");
    add(portLabel);
    portLabel.setBounds(300, 50, 40, 30);

    portField = new TextField(5);
    portField.setText("" + DEFAULT_PORT);
    add(portField);
    portField.setBounds(340, 50, 50, 30);

    hostButton = new Button(CONNECT);
    add(hostButton);
    hostButton.addActionListener(this);
    hostButton.setBounds(400, 50, 80, 30);

    // Message information
    msgField = new TextField(30);
    add(msgField);
    msgField.setBounds(80, 100, 310, 30);
    msgField.setEditable(false);

    msgLabel = new Label("Message :");
    add(msgLabel);
    msgLabel.setBounds(10, 100, 90, 30);

    sndButton = new Button(SEND);
    add(sndButton);
    sndButton.addActionListener(this);
    sndButton.setBounds(400, 100, 80, 30);
    sndButton.setEnabled(false);

    // Exit button
    exitButton = new Button(EXIT);
    add(exitButton);
    exitButton.addActionListener(this);
    exitButton.setBounds(400, 150, 40, 30);

    // Status line
    statusLabel = new Label(
        "Please enter destination of TCP connection (host/port) ...");
    add(statusLabel);
    statusLabel.setBounds(0, 200, 500, 20);
    statusLabel.setBackground(Color.lightGray);

    setTitle("Sockets Lab - TCPClientGUI (" + VERSION + ")");

    // call exitGUI() when closing frame
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        exitGUI();
      }
    });
  }

  // -----------------------------------------------------------------
  // Handling GUI actions
  // -----------------------------------------------------------------
  public void actionPerformed(ActionEvent ae) {

    String cmd = ae.getActionCommand();
    setStatus("...");

    // --- command EXIT ----------------------------------------------
    if (cmd.equals(EXIT)) {
      exitGUI();
    }
    // --- command CONNECT -------------------------------------------
    else if (cmd.equals(CONNECT)) { // Set Command
      setStatus("Connecting ...");
      try {
        InetAddress ia = InetAddress.getByName(hostField.getText());
        int port = Integer.parseInt(portField.getText());
        isa = new InetSocketAddress(ia, port);
        client = new TCPClient(isa);
        setGUIconnected(true);
        setStatus("Socket creation OK.");
      } catch (UnknownHostException e) {
        setStatus("ERROR: Invalid host address!");
      } catch (NumberFormatException e) {
        setStatus("ERROR: Invalid port number!");
      } catch (Exception e) {
        setStatus("ERROR: " + e);
      }
    }
    // --- command DISCONNECT ----------------------------------------
    else if (cmd.equals(DISCONNECT)) {
      try {
        setGUIconnected(false);
        client.closeSocket();
        // hostField.setText("");
        msgField.setText("");
        setStatus("Socket closed.");
      } catch (Exception e) {
        e.printStackTrace();
        setStatus("Closing of socket FAILED!");
      }
    }
    // --- command SEND ----------------------------------------------
    else if (cmd.equals(SEND)) {
      try {
        String msg = msgField.getText();
        client.sendMsg(msg);
        // client.sendObject(new MyMessage(msgCount++, msg));
        msgField.setText("");
        setStatus("Message sent.");
      } catch (Exception e) {
        System.out.println(e);
        setStatus("Error sending message! Closing socket ...");
        try {
          client.closeSocket();
          setStatus("Error sending message! Closing socket ... Done");
        } catch (Exception ei) {
          e.printStackTrace();
          setStatus("Error sending message AND error on closing socket ... !");
        }
        setGUIconnected(false);
      }
    }
    // --- command ?? ------------------------------------------------
    else {
      setStatus("WARNING: unknown command.");
    }
  }

  // -----------------------------------------------------------------
  // Internal utility methods
  // -----------------------------------------------------------------
  void exitGUI() {
    try {
      if (client != null)
        client.closeSocket();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.exit(0);
  }

  void setStatus(String msg) {
    statusLabel.setText(" " + msg);
  }

  void setGUIconnected(boolean connected) {
    hostButton.setLabel(connected ? DISCONNECT : CONNECT);
    hostField.setEditable(!connected);
    portField.setEditable(!connected);
    sndButton.setEnabled(connected);
    msgField.setEditable(connected);
    msgField.setText("");
  }

  // -----------------------------------------------------------------
  // MAIN
  // -----------------------------------------------------------------
  public static void main(String[] args) {
    TCPClientGUI gui = new TCPClientGUI();
    gui.setSize(500, 220);
    gui.setResizable(false);
    gui.setVisible(true);
    gui.repaint();
  }
}
