import model.Line;
import model.Point;
import model.Polygon;
import raster.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

enum Mode {
    LINE,
    POLYGON,
    TRIANGLE;

    static private final Mode[] values = values();

    public Mode previous() {
        return values[(ordinal() - 1 + values.length) % values.length];
    }

    public Mode next() {
        return values[(ordinal() + 1 + values.length) % values.length];
    }
}

public class Canvas {

    private final JFrame frame;
    private final JPanel panel;
    private final RasterBufferedImage raster;
    private final LineRasterizer lineRasterizer;
    private final LineRasterizer dottedLineRasterizer;
    private final ArrayList<Line> lines;
    private Point startPoint;
    private Mode currentMode;
    private final Polygon polygon;
    private final PolygonRasterizer polygonRasterizer;
    private final JTextField text;

    public Canvas(int width, int height) {
        frame = new JFrame();
        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new FilledLineRasterizer(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster);
        lines = new ArrayList<>();
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

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

        text = new JTextField();
        text.setHorizontalAlignment(JTextField.CENTER);
        frame.add(text, BorderLayout.SOUTH);
        changeMode(Mode.TRIANGLE);

        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentMode != Mode.POLYGON) return;
                raster.clear();

                polygon.addPoint(new Point(e.getX(), e.getY()));
                polygonRasterizer.rasterize(polygon);
                panel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentMode == Mode.LINE) {
                    lines.add(new Line(startPoint, new Point(e.getX(), e.getY())));
                    rerenderLines();
                    startPoint = null;
                    return;
                } else if (polygon.getCount() < 1) return;

                raster.clear();
                polygon.addPoint(new Point(e.getX(), e.getY()));
                polygonRasterizer.rasterize(polygon);
                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                switch (currentMode) {
                    case POLYGON -> handlePolygonMouseDrag(e);
                    case LINE -> handleLineMouseDrag(e);
                    case TRIANGLE -> System.out.println("test");
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C -> clearAll();
                    case KeyEvent.VK_RIGHT -> changeMode(currentMode.next());
                    case KeyEvent.VK_LEFT -> changeMode(currentMode.previous());
                    case KeyEvent.VK_T -> changeMode(Mode.TRIANGLE);
                }
            }
        });
    }

    private void changeMode(Mode mode) {
        text.setText(mode.toString().toLowerCase());

        currentMode = mode;
        if (mode == Mode.LINE) rerenderLines();
        else if (mode == Mode.POLYGON) rerenderPolygon();
    }

    private void rerenderLines() {
        raster.clear();
        lines.forEach(lineRasterizer::rasterize);
        panel.repaint();
    }

    private void rerenderPolygon() {
        raster.clear();
        polygonRasterizer.rasterize(polygon);
        panel.repaint();
    }

    private void clearAll() {
        startPoint = null;
        lines.clear();
        polygon.clearPoints();
        raster.clear();
        panel.repaint();
    }

    private void handlePolygonMouseDrag(MouseEvent e) {
        if (polygon.getCount() < 2) return;
        raster.clear();

        if (startPoint == null && polygon.getCount() == 0) {
            startPoint = new Point(e.getX(), e.getY());
            polygon.addPoint(new Point(e.getX(), e.getY()));
        }

        Point currentPoint = new Point(e.getX(), e.getY());
        polygon.removeLastPoint();
        polygon.addPoint(currentPoint);

        polygonRasterizer.rasterize(polygon);
        panel.repaint();
    }

    private void handleLineMouseDrag(MouseEvent e) {
        if (startPoint == null) {
            startPoint = new Point(e.getX(), e.getY());
        }

        rerenderLines();

        Point currentPoint = new Point(e.getX(), e.getY());
        Line line = new Line(startPoint, currentPoint);
        dottedLineRasterizer.rasterize(line);
        panel.repaint();
    }
}
