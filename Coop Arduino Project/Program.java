package serialtest;

import java.util.*;
import java.io.BufferedReader;
import java.sql.*;
import javax.sql.*;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;
import org.json.*;

public class SerialTest implements SerialPortEventListener {
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		static final String DB_URL = "jdbc:sqlanywhere:shame";

		//  Database credentials
		static final String USER = "user";
		static final String PASS  = "password";

                SerialPort serialPort;
        /** The port we're normally going to use. */
                private static final String PORT_NAMES[] = { 
                                                "/dev/tty.usbserial-A9007UX1", // Mac OS X
                        "/dev/ttyACM0", // Raspberry Pi
                                                "/dev/ttyUSB0", // Linux
                                                "COM4", // Windows
                };
                /**
                * A BufferedReader which will be fed by a InputStreamReader 
                * converting the bytes into characters 
                * making the displayed results codepage independent
                */
                private BufferedReader input;
                /** The output stream to the port */
                private OutputStream output;
                /** Milliseconds to block while waiting for port open */
                private static final int TIME_OUT = 2000;
                /** Default bits per second for COM port. */
                private static final int DATA_RATE = 9600;

                public void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

                                CommPortIdentifier portId = null;
                                Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

                                //First, Find an instance of serial port as set in PORT_NAMES.
                                while (portEnum.hasMoreElements()) {
                                                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                        
                        if (currPortId.getName().equals("COM4")) {
                            portId = currPortId;
                        }
//                                            for (String portName : PORT_NAMES) {
//                                                            if (currPortId.getName().equals(portName)) {
//                                                                            portId = currPortId;
//                                                                            break;
//                                                            }
//                                            }
                }
                                if (portId == null) {
                                                System.out.println("Could not find COM port.");
                                                return;
                                }

                                try {
                                                // open serial port, and use class name for the appName.
                                                serialPort = (SerialPort) portId.open(this.getClass().getName(),
                                                                                TIME_OUT);

                                                // set port parameters
                                                serialPort.setSerialPortParams(DATA_RATE,
                                                                                SerialPort.DATABITS_8,
                                                                                SerialPort.STOPBITS_1,
                                                                                SerialPort.PARITY_NONE);

                                                // open the streams
                                                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                                                output = serialPort.getOutputStream();

                                                // add event listeners
                                                serialPort.addEventListener(this);
                                                serialPort.notifyOnDataAvailable(true);
                                } catch (Exception e) {
                                                System.err.println(e.toString());
                                }
                }

                /**
                * This should be called when you stop using the port.
                * This will prevent port locking on platforms like Linux.
                */
                public synchronized void close() {
                                if (serialPort != null) {
                                                serialPort.removeEventListener();
                                                serialPort.close();
                                }
                }

                /**
                * Handle an event on the serial port. Read the data and print it.
                */
                public synchronized void serialEvent(SerialPortEvent oEvent) {
                                if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                                try {
                                                                String inputLine=input.readLine();
                                                                System.out.println(inputLine);
                                                                JSONObject obj = new JSONObject(inputLine);
                                                                String resisStr = obj.getString("resistance");
                                                                String distanceStr = obj.getString("distance");
                                                                System.out.println(resisStr);
                                                                
                                                                int distanNum = Integer.parseInt(distanceStr);
                                                                
                                                                Connection conn = DriverManager.getConnection(
                                                                        "jdbc:sqlanywhere:shame",USER,PASS);
                                                                output.write(distanNum);
                                                                try {
                                                                    Statement stmt = conn.createStatement();
                                                             
                                                                    String sql = "INSERT INTO NUMFROMBOARD(NUM,RESISTANCE) VALUES(" + distanNum+",'" +resisStr+"'"  + ")";
                                                                    
                                                                   // String sql2 = "INSERT INTO NUMFROMBOARD(RESISTANCE) VALUES(\"" + resisStr + "\")";
                                                                    System.out.println(sql);
                                                                    String sql3 = "COMMIT";
                                                                    //if(signal < 100){
                                                                    //    counter++;
                                                                    //    if(counter >= delay){
                                                                    //        counter = 0;                                            
                                                                    //        System.out.println("It is dark!");
                                                                            stmt.executeQuery(sql);
                                                               //             stmt.executeQuery(sql2);
                                                                            stmt.executeQuery(sql3);
                                                                    //    }
                                                                    //}else{
                                                                    //    counter = 0;
                                                                    //}
                                                               } finally {
                                                                   //It's important to close the connection when you are done with it
                                                                   try { conn.close(); } catch (Throwable e) { /* Propagate the original exception
                                                               instead of this one that you want just logged */ //logger.warn("Could not close JDBC Connection",e); }
                                                                   }
                                                               }
                                                                //}
                                                } catch (Exception e) {
                                                                System.err.println(e.toString());
                                                }
                                }
                                // Ignore all the other eventTypes, but you should consider the other ones.
                }

                public static void main(String[] args) throws Exception {
                                SerialTest main = new SerialTest();
                                main.initialize();
                                Thread t=new Thread() {
                                                public void run() {
                                                                //the following line will keep this app alive for 1000 seconds,
                                                                //waiting for events to occur and responding to them (printing incoming messages to console).
                                                                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
                                                }
                                };
                                t.start();
                                System.out.println("Started");
                }
}
