import control.Controller;

import raster.*;

import javax.swing.*;
import java.awt.*;

public class Canvas {
    private final JPanel panel;
    private final RasterBufferedImage raster;

    public Canvas(int width, int height) {
        JFrame frame = new JFrame();
        raster = new RasterBufferedImage(width, height);

        frame.setLayout(new BorderLayout());
        frame.setTitle("Úloha 1");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(raster.getImg(), 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel, BorderLayout.CENTER);

        JTextField text = new JTextField();
        text.setHorizontalAlignment(JTextField.CENTER);
        frame.add(text, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        new Controller(panel, raster, text);
    }
}
