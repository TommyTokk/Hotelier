package Client;

public class ClientProperties {
    private int tcpPort;
    private int udpPort;
    private String udpAddress;


    public ClientProperties(int tcpPort, int udpPort, String udpAddress) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.udpAddress = udpAddress;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public String getUdpAddress() {
        return udpAddress;
    }

    public void setUdpAddress(String udpAddress) {
        this.udpAddress = udpAddress;
    }
}
