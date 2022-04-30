import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

    public static void main(String[] args) {
        List<String> destinationAddresses = new ArrayList<String>(List.of(new String[]
                {"127.0.0.2", "127.0.0.3", "127.0.0.1"
                }));

        RelayNode destination1 = new RelayNode("127.0.0.1", 5001, "127.0.0.2");
        RelayNode destination2 = new RelayNode("127.0.0.2", 5002, "127.0.0.3");
        RelayNode destination3 = new RelayNode("127.0.0.3", 5003, null);
        Sender sender = new Sender("127.0.0.15", "127.0.0.1");


        try {
            for (int i = 0; i < 10; i++) {
                sender.sendMessage(destinationAddresses, i);
                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        sender.closeSocket();
        try {
            destination3.close();
            destination2.close();
            destination1.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
