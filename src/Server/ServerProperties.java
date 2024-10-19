package Server;

public class ServerProperties {
    private int tcpPort;
    private int udpPort;
    private String udpAddress;
    private String usersPath;
    private String hotelsPath;

        public ServerProperties(int tcpPort, int udpPort, String udpAddress, String usersPath, String hotelsPath) {
            this.tcpPort = tcpPort;
            this.udpPort = udpPort;
            this.udpAddress = udpAddress;
            this.usersPath = usersPath;
            this.hotelsPath = hotelsPath;
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

        public String getUsersPath() {
            return usersPath;
        }

        public void setUsersPath(String usersPath) {
            this.usersPath = usersPath;
        }

        public String getHotelsPath() {
            return hotelsPath;
        }

        public void setHotelsPath(String hotelsPath) {
            this.hotelsPath = hotelsPath;
        }
}
