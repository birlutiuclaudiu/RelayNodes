
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

    public static void main(String[] args) {
        List<String> destinationAddresses = new ArrayList<String>(List.of(new String[]
                {"127.0.0.2",  "127.0.0.3"
                }));

        RelayNode relayNode = new RelayNode("127.0.0.2", 5001, "127.0.0.3");
        RelayNode relayNode2 = new RelayNode("127.0.0.3", 5002, "127.0.0.4");
        Sender sender = new Sender("127.0.0.15", "127.0.0.2");
        try {
            sender.sendMessage(destinationAddresses, 6);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.closeSocket();
        try {
            relayNode2.close();
            relayNode.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
