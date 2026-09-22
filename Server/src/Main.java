import entrypoint.gui.ServidorFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServidorFrame frame = new ServidorFrame();
            frame.setVisible(true);
        });
    }
}
