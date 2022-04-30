import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestClass {

    public static void main(String[] args) {
        RelayNode relayNode = new RelayNode("127.0.0.2", 5000);
        Socket socket;
        {
            try {
                socket = new Socket();
                socket.bind(new InetSocketAddress("127.0.0.5",3000));
                socket.connect(new InetSocketAddress("127.0.0.2", 5000));
                DataOutputStream out    = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("127.0.0.1/" + "11213");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
