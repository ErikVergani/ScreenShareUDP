import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class Singleton {

    private static Singleton instance;
    private int x;
    private int y;
    private int z = 0;
    private Singleton() {
        x=0;
        y=0;
    }

    private DatagramSocket socket;
    private DatagramPacket receivedPacket;

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putInt(this.x);
        buffer.putInt(this.y);
        buffer.putInt(this.z);
        return buffer.array();
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getIntCoordinates(){
        return x + ":" + y;
    }

    public void sendCordinates(DatagramSocket socket, DatagramPacket receivedPacket){

        this.receivedPacket = receivedPacket;
        this.socket = socket;

        byte[] coordinates = getBytes();
        DatagramPacket packet = new DatagramPacket(coordinates, coordinates.length, receivedPacket.getAddress(), receivedPacket.getPort());

        try {
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("DEU COISARAD");;
        }
    }

    public void sendClick(int click){
        this.z=click;
        byte[] coordinates = getBytes();
        DatagramPacket packet = new DatagramPacket(coordinates, coordinates.length, receivedPacket.getAddress(), receivedPacket.getPort());

        try {
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("DEU COISARAD");;
        }
        z=0;
    }
}