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
            logger.log(Level.INFO, String.format("Creating server with ipAddrees %s and port %d...", ipAddress, portNumber));
            this.serverSocket = new ServerSocket();
            this.serverSocket.setReuseAddress(true);
            this.serverSocket.bind(new InetSocketAddress(this.ipAddress, this.portNumber));
            logger.log(Level.INFO, String.format("Created server with ipAddress %s and port %d", ipAddress, portNumber));

            //created thread for server
            onListeningThread = new MyThread();
            onListeningThread.start();

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
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
                logger.log(Level.INFO, String.format("-------------------------------------" +
                                "----------------->MESSAGE FOR ME (%s) from %s : %s",
                        serverSocket.getInetAddress().getHostAddress(),
                        socket.getInetAddress().getHostAddress(),
                        parts[1]));
            } else {
                clientSendMessageToNextHop(payload);
            }
        } catch (IOException e) {

        }

    }

    private void clientSendMessageToNextHop(String payload) {
        try {
            //create the client socket and connect to the next hop if it not exists
            if (clientSocket == null) {
                clientSocket = new Socket();
                clientSocket.setReuseAddress(true);
                clientSocket.bind(new InetSocketAddress(this.ipAddress, this.portNumber + 1));
                if (nextHopAddress != null)
                    clientSocket.connect(new InetSocketAddress(nextHopAddress, this.portNumber + 1));
            }
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeUTF(payload);
            out.flush();
            logger.log(Level.INFO, String.format("-------------------------------------" +
                            "----------------->HOP FROM ME (%s) to %s FOR PAYLOAD: %s",
                    this.ipAddress,
                    this.nextHopAddress,
                    payload));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            try {
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO, String.format("Connected with client %s",
                        socket.getInetAddress().getHostAddress()));
                while (!closed) {
                    onReceiveFromClient(socket);
                }

            } catch (IOException e) {
                logger.log(Level.INFO, "Server not exist, it is closed");
            }

        }

        public void setClosed(Boolean closed) {
            this.closed = closed;
        }
    }


}
