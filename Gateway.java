//E/17/398

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Gateway{
    
    public static ArrayList<String> monitorIds = new ArrayList<String>();

    // buffer to hold data
    private byte[] data = new byte[1024];

    public void initConnection() throws ClassNotFoundException{
        try{
            DatagramSocket datagramsocket = new DatagramSocket(1000);
            
            try{
                while(true){
                    //receive udp broadcast
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    datagramsocket.receive(packet);
                    Monitor monitorObject = convertToMonitor(data);
                    
                    InetAddress monitorObjectIp = monitorObject.getIp();
                    int monitorObjectPort = monitorObject.getPort();
                    String monitorObjectId = monitorObject.getMonitorID();

                    //create a tcp connection
                    if(monitorIds.contains(monitorObjectId) == false){
                        InitTCPconnection clientHandler =  new InitTCPconnection(monitorObject);
                        Thread client =  new Thread(clientHandler);
                        client.start();
                        synchronized(monitorIds){
                            monitorIds.add(monitorObjectId);
                        }
                        System.out.println("Connection established to Monitor IP:"+monitorObjectIp+" From Port:"+monitorObjectPort+" and Id:"+monitorObjectId);
                }
                }
            }
            catch (Exception e){
                datagramsocket.close();
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        } 
    }

    //convert byte array to monitor object
    private static Monitor convertToMonitor(byte[] data) {
         Monitor monitor = null;

        try {
            ByteArrayInputStream bytein = new ByteArrayInputStream(data);
            ObjectInputStream objectin = new ObjectInputStream(bytein);
            monitor = (Monitor) objectin.readObject();
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return monitor;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Gateway gateway = new Gateway();
        gateway.initConnection();
    }
}