package Server;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * ServerMain
 */

public class ServerMain {
    private static MulticastSocket mcSocket;
    private static final String configFile = "./src/Server/server.properties";
    private static int tcpPort;
    private static int udpPort;
    private static String udpAddress;
    private static String usersPath;
    private static String hotelsPath;
    private static int maxDelay = 1000;



    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ServerSocket serverSocket;
    
    public static void main(String[] args) {
        try {
            readConfig();
            serverSocket = new ServerSocket(tcpPort);
            //Schedule a thread every 60s to save users to file
            scheduler.scheduleAtFixedRate(new SaverThread(), 10, 60, TimeUnit.SECONDS);
            startUDPServer();

            /*
             * Parte che si occuperà della gestione della terminazione del server
             * Da implementare in una classe a parte
             */
            Runtime.getRuntime().addShutdownHook(
                new TerminationHandler(serverSocket, pool, maxDelay)
            );
            System.out.println("[Server] Server started on port " + tcpPort);
            while(true){
                //Accetto le nuove connessioni
                Socket s = null;
                try{s = serverSocket.accept();}
                catch(IOException e){break;}
                //Creo un nuovo thread per gestire la connessione
                pool.execute(new ConnectionsHandler(s));
            }
        } catch (Exception e) {
            closeUDPServer();
            System.err.printf("[Server] Error: %s Line: %d\n", e.getMessage(), e.getStackTrace()[0].getLineNumber());
            System.exit(1);
        }
    }

    public static String getUsersPath() {
        return usersPath;
    }

    public static String getHotelsPath() {
        return hotelsPath;
    }

    private static void readConfig() {
        try{
            InputStream in = new FileInputStream(configFile);
            Properties prop = new Properties();
            prop.load(in);
            tcpPort = Integer.parseInt(prop.getProperty("tcpPort"));
            udpPort = Integer.parseInt(prop.getProperty("udpPort"));
            udpAddress = prop.getProperty("udpAddress");
            usersPath = prop.getProperty("usersPath");
            hotelsPath = prop.getProperty("hotelsPath");
            in.close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static int getUdpPort() {
        return udpPort;
    }

    public static String getUdpAddress() {
        return udpAddress;
    }

    public static void startUDPServer(){
        try{
            mcSocket = new MulticastSocket(udpPort);
            
            mcSocket.setReuseAddress(true);
            mcSocket.setInterface(InetAddress.getLocalHost());
            System.out.printf("[Server] UDP Server started on port %d\n", udpPort);

            mcSocket.joinGroup(InetAddress.getByName(udpAddress));

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void sendUDPMessage(String message){
        try{
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(udpAddress), udpPort);
            mcSocket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void closeUDPServer(){
        try{
            mcSocket.leaveGroup(new InetSocketAddress(udpAddress, udpPort), null);
            mcSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
