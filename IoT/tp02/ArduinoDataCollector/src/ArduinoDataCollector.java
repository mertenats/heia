/**
 * Arduino Data Collector
 * Samuel M. - 11.03.2015
 * 
 * The needed Arduino code ... for parsing purpose
 * float h = 45;  // humidity
 * float t = 22;  // temperatur;
 * float l = 900;  // light
 * 
 * void setup() {
 *      Serial.begin(9600);
 * }
 * void loop() {
 *      h += 0.1;
 *      t += 0.1;
 *      l += 1;
 *      Serial.print("START#T1"); 
 *      Serial.print(t);
 *      Serial.print("#H1");
 *      Serial.print(h); 
 *      Serial.print("#L1");
 *      Serial.print(l);
 *      Serial.println("END");
 *      delay(10000);
 * }
 * 
 * Output data --> Data.csv --> Eclipse workspace > Project name > 
 */

import java.io.FileWriter;
import java.io.IOException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import com.opencsv.CSVWriter;

public class ArduinoDataCollector {
  // Mac OS X...
  // Arduino MEGA: "/dev/tty.usbmodem402421"
  // Arduino UNO: "/dev/tty.usbmodem1411"
  private static String         SERIAL_PORT     = "/dev/tty.usbmodem402411";
  private static SerialPort     serialPort;
  private static final String   csv             = "Data.csv";
  private static CSVWriter      writer;

  private static float          t;                                           // temperature
  private static float          h;                                           // humidity
  private static int            l;                                           // light
  private static int            nb;                                          // counter
  private static int            index;

  private static long           startTime       = System.currentTimeMillis();
  private static long           endTime;
  private static long           duration;                                    // duration
                                                                              // from
                                                                              // the
                                                                              // execution
  private static int            measureDuration = 3600;                      // [s]

  // constants used to separate the data sent from the Arduino
  private static final String[] MEASURES_ID     = { "#T", "#H", "#L" };

  public static void main(String[] args) {
    // gets the params...
    if (args.length == 2) {
      SERIAL_PORT = args[0];
      measureDuration = Integer.parseInt(args[1]);
    }

    // creates a csv file
    try {
      writer = new CSVWriter(new FileWriter(csv));
      System.out.println("CSV file created successfully " + csv);
    } catch (IOException e1) {
      System.out.println("Error : can't create the CSV file " + csv);
      e1.printStackTrace();
    }
    String header = "#,Time,Temperature,Humidity,Light level";
    writer.writeNext(header.split(","));

    // connects to the serialport
    serialPort = new SerialPort(SERIAL_PORT);
    try {
      serialPort.openPort();
      serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
      serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
      System.out.println("Connection established to " + SERIAL_PORT);
    } catch (SerialPortException e) {
      System.out.println("ERROR : can't connect to " + SERIAL_PORT);
      e.printStackTrace();
    }
  }

  private static class PortReader implements SerialPortEventListener {
    StringBuffer sb = new StringBuffer();

    public void serialEvent(SerialPortEvent event) {
      if (event.isRXCHAR() && event.getEventValue() > 0) {
        try {
          // adds received bytes to StringBuffer
          sb.append(serialPort.readString(event.getEventValue()));
        } catch (SerialPortException e) {
          System.out.println("ERROR : can't read bytes from " + SERIAL_PORT);
          e.printStackTrace();
        }
      }
      // waits until the end of each measures
      if (sb.indexOf("END") != -1) {
        write(sb.toString());
        sb.setLength(0);
      }
    }
  }

  private static void write(String data) {
//    t = Float.parseFloat(data.substring(data.indexOf(MEASURES_ID[0] + 3),
//        data.indexOf(MEASURES_ID[1])));
//    System.out.println(data.substring(data.indexOf(MEASURES_ID[0] + 3),
//        data.indexOf(MEASURES_ID[1])));
//    h = Float.parseFloat(data.substring(data.indexOf(MEASURES_ID[1] + 3),
//        data.indexOf(MEASURES_ID[2])));
//    System.out.println(data.substring(data.indexOf(MEASURES_ID[1] + 3),
//        data.indexOf(MEASURES_ID[2])));
//    l = Integer.parseInt((data.substring(data.indexOf(MEASURES_ID[2] + 3),
//        data.indexOf("END") - 1)));
//    System.out.println((data.substring(data.indexOf(MEASURES_ID[2] + 3),
//        data.indexOf("END") - 1)));

    for (int i = 0; i < MEASURES_ID.length; i++) {
      index = data.indexOf(MEASURES_ID[i]);
      if (index != -1 && data.indexOf("END") != -1) {
        if (i == 0) {
          t = Float.parseFloat(data.substring(index + 3,
              data.indexOf(MEASURES_ID[i + 1])));
        } else if (i == 1) {
          h = Float.parseFloat(data.substring(index + 3,
              data.indexOf(MEASURES_ID[i + 1])));
        } else if (i == 2) {
          l = Integer.parseInt(data.substring(index + 3, data.indexOf("END")));
        }
      } else {
        System.out.println("ERROR : can't decode " + MEASURES_ID[i]);
      }
    }
    endTime = System.currentTimeMillis();
    duration = (endTime - startTime) / 1000; // duration in s

    String measure = nb++ + "," + duration + "," + t + "," + h + "," + l;
    writer.writeNext(measure.split(","));
    System.out.println(nb + " : Time : " + duration + " Temperature : " + t
        + " Humidity : " + h + " Light level: " + l);

    if (duration >= measureDuration) {
      try {
        writer.close();
      } catch (IOException e) {
        System.out.println("ERROR : can't close the file " + csv);
        e.printStackTrace();
      }
      System.out.println("INFO : the measures are done!");
      System.exit(0); // closes the program
    }
  }
}