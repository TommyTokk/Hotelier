package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Properties;
import java.io.FileInputStream;




public class ClientMain {

    private static String configFile = "./src/Client/client.properties";
    private static String serverAddress;
    private static int tcpPort;
    private static int udpPort;
    private static String udpAddress;

    private static MulticastSocket mcSocket;

    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    public static volatile boolean closed = true;
    private static boolean socketClosed = false;

    private static Thread udpListenerThread;

    public static void main(String[] args) {
        try {
            readConfig();
            socket = new Socket(serverAddress, tcpPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            CliClientHandler cliHandler = new CliClientHandler(out);
            String serverMessage;
            
            UDPHandler udpHandler = new UDPHandler(mcSocket, udpPort, udpAddress, socketClosed);

            do {
                serverMessage = in.readLine();
                int status = cliHandler.eval(serverMessage);
                if (status == 1) {
                    break;
                }
                if (status == 2) {
                    udpListenerThread = new Thread(udpHandler);
                    udpListenerThread.start();
                }
                if (status == 3) {
                    udpListenerThread.interrupt();
                }
            } while (!serverMessage.equals("exit"));
            System.out.printf("\033[H\033[2J");
            System.out.flush();
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
            System.err.printf("[CLIENT] Error: %s\n", e.getMessage());
            System.exit(1);
        }
    }

    private static void readConfig() {
        try {
            FileInputStream input = new FileInputStream(configFile);
            Properties prop = new Properties();
            prop.load(input);
            tcpPort = Integer.parseInt(prop.getProperty("tcpPort"));
            udpPort = Integer.parseInt(prop.getProperty("udpPort"));
            udpAddress = prop.getProperty("udpAddress");
            serverAddress = prop.getProperty("serverAddress");
            input.close();
        } catch (Exception e) {
            System.err.printf("[CLIENT] Error: %s\n", e.getMessage());
            System.exit(1);
        }
    }
}