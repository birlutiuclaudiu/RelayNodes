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
    private Socket socket         = null;
    private DataInputStream input = null;
    private DataOutputStream out  = null;
    private String ipAddress;
    private String nextHop;
    private final int PORT = 5000;

    static final Logger logger = Logger.getLogger(String.valueOf(RelayNode.class));

    public Sender(String ipAddress, String nextHop) {
        this.ipAddress = ipAddress;
        this.nextHop = nextHop;
        try
        {
            this.socket = new Socket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(this.ipAddress, PORT));
            logger.log(Level.INFO, String.format("Created sender with ipAddress %s and port %d",this.ipAddress, PORT));
            this.socket.connect(new InetSocketAddress(nextHop, PORT+1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(List<String> ipAddresses, int value) throws IOException {
        //randomly select an avaible address from list
        String ipAddress = ipAddresses.get(new Random().nextInt(ipAddresses.size()));
        DataOutputStream out    = new DataOutputStream(this.socket.getOutputStream());
        out.writeUTF(ipAddress + "/" + value);
    }
    public void closeSocket(){
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
