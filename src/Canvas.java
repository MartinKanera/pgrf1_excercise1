import model.Line;
import model.Point;
import model.Polygon;
import model.Triangle;
import raster.*;
import model.Mode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Canvas {
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
    private final Triangle triangle;

    public Canvas(int width, int height) {
        JFrame frame = new JFrame();
        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new FilledLineRasterizer(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster);
        lines = new ArrayList<>();
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        triangle = new Triangle();

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
        changeMode(Mode.LINE);

        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (currentMode) {
                    case POLYGON -> handlePolygonMouseClick(e);
                    case TRIANGLE -> handleTriangleMouseClick(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (currentMode) {
                    case LINE -> handleLineMouseReleased(e);
                    case POLYGON -> handlePolygonMouseReleased(e);
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                switch (currentMode) {
                    case POLYGON -> handlePolygonMouseDragged(e);
                    case LINE -> handleLineMouseDragged(e);
                    case TRIANGLE -> handleTriangleMouseDragged(e);
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

        switch (mode) {
            case LINE -> rerenderLines();
            case POLYGON -> rerenderPolygon();
            case TRIANGLE -> rerenderTriangle();
        }
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

    private void rerenderTriangle() {
        raster.clear();
        polygonRasterizer.rasterize(triangle);
        panel.repaint();
    }

    private void clearAll() {
        startPoint = null;
        lines.clear();
        polygon.clearPoints();
        triangle.clearPoints();
        raster.clear();
        panel.repaint();
    }

    private Point validateEventCoordinates(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int width = raster.getImg().getWidth();
        int height = raster.getImg().getHeight();

        if (x < 0) x = 0;
        if (x >= width - 1) x = width - 1;
        if (y < 0) y = 0;
        if (y >= height - 1) y = height - 1;

        return new Point(x, y);
    }

    // -- Line handlers

    private void handleLineMouseDragged(MouseEvent e) {
        if (startPoint == null) {
            startPoint = new Point(e.getX(), e.getY());
        }

        rerenderLines();

        Point currentPoint = validateEventCoordinates(e);
        Line line = new Line(startPoint, currentPoint);
        dottedLineRasterizer.rasterize(line);
        panel.repaint();
    }

    private void handleLineMouseReleased(MouseEvent e) {
        if (startPoint == null) return;

        lines.add(new Line(startPoint, validateEventCoordinates(e)));
        rerenderLines();
        startPoint = null;
    }

    // -- Polygon handlers

    private void handlePolygonMouseClick(MouseEvent e) {
        polygon.addPoint(validateEventCoordinates(e));
        polygonRasterizer.rasterize(polygon);
        rerenderPolygon();
    }

    private void handlePolygonMouseDragged(MouseEvent e) {
        if (polygon.getCount() < 2) return;

        if (startPoint == null && polygon.getCount() == 0) {
            startPoint = new Point(e.getX(), e.getY());
            polygon.addPoint(new Point(e.getX(), e.getY()));
        }

        Point currentPoint = validateEventCoordinates(e);
        polygon.removeLastPoint();
        polygon.addPoint(currentPoint);

        polygonRasterizer.rasterize(polygon);
        rerenderPolygon();
    }

    private void handlePolygonMouseReleased(MouseEvent e) {
        if (polygon.getCount() < 1) return;

        polygon.addPoint(validateEventCoordinates(e));
        rerenderPolygon();
    }

    // -- Triangle handlers

    private void handleTriangleMouseClick(MouseEvent e) {
        if (triangle.getCount() >= 2) triangle.clearPoints();

        triangle.addPoint(validateEventCoordinates(e));
        polygonRasterizer.rasterize(triangle);
        rerenderTriangle();
    }

    private void handleTriangleMouseDragged(MouseEvent e) {
        if (triangle.getCount() < 2) return;

        triangle.calculateAndSetTop(e.getY());
        polygonRasterizer.rasterize(triangle);
        rerenderTriangle();
    }
}
