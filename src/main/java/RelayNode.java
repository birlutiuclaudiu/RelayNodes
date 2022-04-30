import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelayNode {

    static final Logger logger = Logger.getLogger(String.valueOf(RelayNode.class));
    private String ipAddress;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private String nextHopAddress;
    private int portNumber;
    private MyThread onListeningThread;

    public RelayNode(String ipAddress, int portNumber, String nextHopAddress) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.nextHopAddress = nextHopAddress;
        try {
            logger.log(Level.INFO, String.format("Create server with ipAddrees %s and port %d", ipAddress, portNumber));
            this.serverSocket = new ServerSocket();
            this.serverSocket.setReuseAddress(true);
            this.serverSocket.bind(new InetSocketAddress(this.ipAddress, this.portNumber));
            onListeningThread = new MyThread();
            onListeningThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onReceiveFromClient(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            String payload = in.readUTF();
            String splitCharacter = "/";
            String[] parts = payload.split(splitCharacter);
            if (parts[0].equals(this.ipAddress)) {
                System.out.println("daaa");
                System.out.println(this.serverSocket.toString());
            } else {
                clientSendMessageToNextHop(payload);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void clientSendMessageToNextHop(String payload) {

        if (this.clientSocket == null) {
            clientSocket = new Socket();
            try {
                clientSocket.bind(new InetSocketAddress(this.ipAddress, this.portNumber+1));
                clientSocket.connect(new InetSocketAddress(nextHopAddress, 5002));
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF(payload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() throws IOException {
        try {
            onListeningThread.setClosed(true);
            logger.log(Level.INFO, "Closing threads");
            logger.log(Level.INFO, "Closed threads");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.serverSocket.close();
        if (clientSocket != null)
            this.clientSocket.close();

    }

    private class MyThread extends Thread {
        private boolean closed = false;
        @Override
        public void run() {
            while (!closed) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(socket.getInetAddress());
                    DataInputStream in = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));
                    new Thread(() -> onReceiveFromClient(socket)).start();
                } catch (IOException e) {
                    logger.log(Level.INFO, "Server not exist, it is closed");
                }
            }
        }
        public void setClosed(Boolean closed) {
            this.closed = closed;
        }
    }
}
