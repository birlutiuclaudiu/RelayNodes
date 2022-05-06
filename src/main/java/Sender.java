import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents the sender, which is in fact a simple client
 */
public class Sender {
    static final Logger logger = Logger.getLogger(String.valueOf(RelayNode.class));
    private int port;
    private Socket socket = null;
    private DataInputStream input = null;
    private String ipAddress;
    private String nextHop;

    public Sender(String ipAddress, Integer port, String nextHop) {
        this.ipAddress = ipAddress;
        this.nextHop = nextHop;
        this.port = port;
        try {
            this.socket = new Socket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(this.ipAddress, port));
            logger.log(Level.INFO, String.format("Created sender with ipAddress %s and port %d", this.ipAddress, port));
            this.socket.connect(new InetSocketAddress(nextHop, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(List<String> ipAddresses, int value) throws IOException {
        //randomly select an avaible address from list
        String ipAddress = ipAddresses.get(new Random().nextInt(ipAddresses.size()));
        logger.log(Level.INFO, String.format("------------------------------------------------------>" +
                        "SEND THE PAYLOAD %s to %s FROM %s", ipAddress + "/" + value, this.nextHop,
                this.ipAddress));
        DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
        //the package has the following format destination_address/value
        out.writeUTF(ipAddress + "/" + value);
        out.flush();
    }

    public void closeSocket() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
