//E/17/398

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class InitTCPconnection extends Thread{
    private Monitor monitorObject;
    private Socket clientSocket;
    private BufferedReader in;
    public InitTCPconnection(Monitor monitorObject){
        this.monitorObject = monitorObject;
    }
    public void run(){
        try {
            clientSocket = new Socket(this.monitorObject.getIp(), this.monitorObject.getPort());
            while(true){
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String str = in.readLine();
                System.out.println(str);    
            }
        }
        catch (Exception e) {
            System.out.println("Connection closed!");
        } 
    }
}
