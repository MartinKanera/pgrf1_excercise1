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
        frame.setTitle("Úloha 2");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(raster.getImg(), 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(width, 500));
        frame.add(panel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
        textPanel.setBackground(Color.white);
        frame.add(textPanel, BorderLayout.SOUTH);

        JTextField drawModeText = new JTextField();
        drawModeText.setHorizontalAlignment(JTextField.CENTER);
        drawModeText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        drawModeText.setPreferredSize(new Dimension(75, 20));
        textPanel.add(drawModeText);

        JTextField fillModeText = new JTextField();
        fillModeText.setHorizontalAlignment(JTextField.CENTER);
        fillModeText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        fillModeText.setPreferredSize(new Dimension(150, 20));
        textPanel.add(fillModeText);

        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        new Controller(panel, raster, drawModeText, fillModeText);
    }
}
