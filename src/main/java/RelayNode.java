import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelayNode {

    private String ipAddress;
    private int port;
    private Socket clientSocket;
    private ServerSocket serverSocket;

    private String nextHopAddress;
    private int portNumber;
    static final Logger logger = Logger.getLogger(String.valueOf(RelayNode.class));

    public RelayNode(String ipAddress, int portNumber, String nextHopAddress){
       this.ipAddress = ipAddress;
       this.portNumber = portNumber;
       this.nextHopAddress = nextHopAddress;
        try {
            logger.log(Level.INFO, String.format("Create server with ipAddrees %s and port %d",ipAddress,portNumber));
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(this.ipAddress, this.portNumber));
            new Thread(this::createThreadForServer).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createThreadForServer(){
        while (true){
            try {
                Socket socket = this.serverSocket.accept();
                System.out.println(socket.getInetAddress());
                DataInputStream in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                new Thread(() -> onReceiveFromClient(socket)).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
    private void onReceiveFromClient(Socket socket){
        try {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            String payload = in.readUTF();
            String splitCharacter = "/";
            String[] parts = payload.split(splitCharacter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }








}
