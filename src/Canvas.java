import model.Line;
import model.Point;
import raster.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Canvas {
    private final JFrame frame;
    private final JPanel panel;
    private final RasterBufferedImage raster;
    private final LineRasterizer lineRasterizer;

    public Canvas(int width, int height) {
        frame = new JFrame();
        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new BresenhamLineRasterizer(raster);

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
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                raster.clear();
                Line line = new Line(new Point(width / 2, height / 2), new Point(e.getX(), e.getY()));
                lineRasterizer.rasterize(line);
                panel.repaint();
            }
        });
    }
}
