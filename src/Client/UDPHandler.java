package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.channels.AsynchronousCloseException;


public class UDPHandler implements Runnable{
    private  MulticastSocket mcSocket;
    private String udpAddress;
    private int udpPort;

    private boolean socketClosed = false;


    public UDPHandler(MulticastSocket mcSocket, int udpPort, String udpAddress, boolean socketClosed) {
        this.mcSocket = mcSocket;
        this.udpAddress = udpAddress;
        this.udpPort = udpPort;
    }
    public void run() {
        try {
            subscribeUDP();
            receiveMessage();
            unsubscribeUDP();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mcSocket != null) {
                mcSocket.close();
            }
        }
    }

    public void subscribeUDP(){
        try {
            mcSocket = new MulticastSocket(udpPort);
            mcSocket.setSoTimeout(1000);
            InetAddress group = InetAddress.getByName(udpAddress);
            mcSocket.joinGroup(group);
        } catch (AsynchronousCloseException e) {
            System.out.println("Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(){
        while (!Thread.currentThread().isInterrupted()){
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                mcSocket.receive(packet);
                synchronized (System.out) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(received);
                }
            } catch (AsynchronousCloseException e) {
                break;
            } catch (IOException e) {
            }finally{
                if(socketClosed){
                    break;
                }
            }
        }
    }

    public void unsubscribeUDP() {
        try {
            mcSocket.leaveGroup(InetAddress.getByName(udpAddress));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mcSocket.close();
        socketClosed = true;
    }
}
