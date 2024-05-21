import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.ByteBuffer;

public class ScreenCaptureSender
{
    public static void main(String[] args) {
        try
        {
            while ( true )
            {
                InetAddress ipAddress = InetAddress.getByName("192.168.0.141");
                int port = 5025;

                DatagramSocket socket = new DatagramSocket();

                Robot robot = new Robot();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                int rows = 4;
                int cols = 5;
                
                int quadrantWidth = screenSize.width / cols;
                int quadrantHeight = screenSize.height / rows;

                int c = 1;
                for (int row = 0; row < rows; row++)
                {
                    for (int col = 0; col < cols; col++)
                    {
                        int x = col * quadrantWidth;
                        int y = row * quadrantHeight;

                        Rectangle quadrantRect = new Rectangle(x, y, quadrantWidth, quadrantHeight);
                        BufferedImage quadrantImage = robot.createScreenCapture(quadrantRect);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(quadrantImage, "jpg", baos);
                        byte[] imageData = baos.toByteArray();

                        byte[] bytes = new byte [ imageData.length + 1];

                        bytes[0] = (byte) c;
                        c = 0;
                        for (int i = 1; i < imageData.length; i++)
                        {
                            bytes[ i ] = imageData[ i - 1 ];
                        }

                        DatagramPacket packet = new DatagramPacket(imageData, imageData.length, ipAddress, port);

                        Thread.sleep(1);
                        socket.send( packet );

                        byte cursorPointer[] = new byte[12];
                        DatagramPacket receivePacket = new DatagramPacket( cursorPointer, cursorPointer.length );
                        socket.receive( receivePacket );

                        int[] pointer = bytesToInts( cursorPointer );

                        System.out.println( pointer[0] + ":" + pointer[1]+":"+pointer[2] );
                        robot.mouseMove( pointer[0], pointer[1] );

                        switch ( pointer[2] )
                        {
                            case 1:
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                                break;

                            case 2:
                                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                                break;

                            case 3:
                                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                                break;
                        }


                        System.out.println("Quadrante (" + row + "," + col + ") enviado com sucesso.");
                    }
                }

                Thread.sleep(1);
                socket.close();
            }
        }

        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static int[] bytesToInts(byte[] bytes) {
        if (bytes.length != 12) {
            throw new IllegalArgumentException("O array de bytes deve ter comprimento 12 para representar dois inteiros.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int value1 = buffer.getInt();
        int value2 = buffer.getInt();
        int value3 = buffer.getInt();
        return new int[] {value1, value2, value3};
    }

}
