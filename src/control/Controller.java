package control;

import filler.Filler;
import filler.SeedFiller;
import helpers.SizeValidator;
import model.*;
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

public class Controller {
    private final JPanel panel;
    private Mode currentMode;
    private final Raster raster;
    private final JTextField modeTextField;
    private final LineRasterizer lineRasterizer;
    private final LineRasterizer dottedLineRasterizer;
    private final ArrayList<Line> lines;
    private Point startPoint;
    private final Polygon polygon;
    private final PolygonRasterizer polygonRasterizer;
    private final Triangle triangle;
    private final SizeValidator sizeValidator;

    public Controller(JPanel panel, RasterBufferedImage raster, JTextField modeTextField) {
        this.panel = panel;
        this.raster = raster;
        this.modeTextField = modeTextField;
        initListeners();

        lineRasterizer = new FilledLineRasterizer(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster);
        lines = new ArrayList<>();
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        sizeValidator = new SizeValidator(raster.getWidth(), raster.getHeight());
        triangle = new Triangle();

        changeMode(Mode.POLYGON);
    }

    private void initListeners() {
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
        modeTextField.setText(mode.toString().toLowerCase());
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

    // -- Line handlers

    private void handleLineMouseDragged(MouseEvent e) {
        if (startPoint == null) {
            startPoint = new Point(e.getX(), e.getY());
        }

        rerenderLines();

        Point currentPoint = sizeValidator.validateEventCoordinates(e);
        Line line = new Line(startPoint, currentPoint);
        dottedLineRasterizer.rasterize(line);
        panel.repaint();
    }

    private void handleLineMouseReleased(MouseEvent e) {
        if (startPoint == null) return;

        lines.add(new Line(startPoint, sizeValidator.validateEventCoordinates(e)));
        rerenderLines();
        startPoint = null;
    }

    // -- Polygon handlers

    private void handlePolygonMouseClick(MouseEvent e) {
        if (e.isControlDown() && SwingUtilities.isLeftMouseButton(e)) {
            Point currentPoint = new Point(e.getX(), e.getY());
            Filler filler = new SeedFiller(currentPoint, Color.black.getRGB(), Color.WHITE.getRGB(), raster);
            filler.fill();
            panel.repaint();

            return;
        }

        polygon.addPoint(sizeValidator.validateEventCoordinates(e));
        polygonRasterizer.rasterize(polygon);
        rerenderPolygon();
    }

    private void handlePolygonMouseDragged(MouseEvent e) {
        if (polygon.getCount() < 2) return;

        if (startPoint == null && polygon.getCount() == 0) {
            startPoint = new Point(e.getX(), e.getY());
            polygon.addPoint(new Point(e.getX(), e.getY()));
        }

        Point currentPoint = sizeValidator.validateEventCoordinates(e);
        polygon.removeLastPoint();
        polygon.addPoint(currentPoint);

        polygonRasterizer.rasterize(polygon);
        rerenderPolygon();
    }

    private void handlePolygonMouseReleased(MouseEvent e) {
        if (polygon.getCount() < 1 || e.isControlDown()) return;

        polygon.addPoint(sizeValidator.validateEventCoordinates(e));
        rerenderPolygon();
    }

    // -- Triangle handlers

    private void handleTriangleMouseClick(MouseEvent e) {
        if (triangle.getCount() >= 2) triangle.clearPoints();

        triangle.addPoint(sizeValidator.validateEventCoordinates(e));
        polygonRasterizer.rasterize(triangle);
        rerenderTriangle();
    }

    private void handleTriangleMouseDragged(MouseEvent e) {
        if (triangle.getCount() < 2) return;

        Point topPoint = sizeValidator.validateEventCoordinates(e);

        triangle.calculateAndSetTop(topPoint.getY());
        polygonRasterizer.rasterize(triangle);
        rerenderTriangle();
    }
}
