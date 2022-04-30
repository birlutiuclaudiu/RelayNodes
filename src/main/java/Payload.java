/**
 * This class represents the object sent as payload from client ot server
 */
public class Payload {
    private String ipAddress;
    private int value;

    public Payload(String ipAddress, int value) {
        this.ipAddress = ipAddress;
        this.value = value;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getValue() {
        return value;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
