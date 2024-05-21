import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ScreenCaptureReceiver
{

    private static final int TOTAL_QUADRANTS = 20;
    private static final int ROWS = 4;
    private static final int COLS = 5;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            int port = 5025;
            socket = new DatagramSocket(port);

            JFrame frame = new JFrame("Imagem em Tempo Real");
            JLabel imageLabel = new JLabel();
            frame.setUndecorated(true);
            frame.getContentPane().add(imageLabel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.addMouseMotionListener( new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {

                }

                @Override
                public void mouseMoved( MouseEvent e ) {
                    Singleton.getInstance().setCoordinates(e.getX(), e.getY());
                }
            });

            frame.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println(e.getButton());
                    Singleton.getInstance().sendClick(e.getButton());
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            while (true) {
                BufferedImage[] receivedQuadrants = new BufferedImage[TOTAL_QUADRANTS];

                for (int i = 0; i < TOTAL_QUADRANTS; i++) {
                    byte[] buffer = new byte[65508];

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    byte[] imageData = packet.getData();
                    if(imageData[0] == 1){
                        i=0;
                    }
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                    BufferedImage quadrantImage = ImageIO.read(bais);

                    Singleton.getInstance().sendCordinates(socket, packet);

                    receivedQuadrants[i] = quadrantImage;
                }

                BufferedImage completeImage = joinQuadrants(receivedQuadrants);

                if (completeImage != null) {
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(new ImageIcon(completeImage));
                        frame.pack();
                    });
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private static BufferedImage joinQuadrants(BufferedImage[] quadrants) {
        if (quadrants == null || quadrants.length != TOTAL_QUADRANTS) {
            return null;
        }

        int quadrantWidth = quadrants[0].getWidth();
        int quadrantHeight = quadrants[0].getHeight();
        int width = quadrantWidth * COLS;
        int height = quadrantHeight * ROWS;

        BufferedImage completeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                BufferedImage quadrantImage = quadrants[row * COLS + col];
                int x = col * quadrantWidth;
                int y = row * quadrantHeight;
                completeImage.createGraphics().drawImage(quadrantImage, x, y, null);
            }
        }

        return completeImage;
    }
}