package control;

import filler.Filler;
import filler.ScanLineFiller;
import filler.SeedFiller;
import filler.SeedFillerBackground;
import helpers.FillMode;
import helpers.SizeValidator;
import model.*;
import model.Point;
import model.Polygon;
import raster.*;
import clipper.PolygonClipper;
import helpers.DrawMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller {
    private final JPanel panel;
    private DrawMode drawMode;
    private FillMode fillMode;
    private final RasterBufferedImage raster;
    private final JTextField drawModeTextField;
    private final JTextField fillModeTextField;
    private final LineRasterizer lineRasterizer;
    private final LineRasterizer dottedLineRasterizer;
    private final ArrayList<Line> lines;
    private Point startPoint;
    private Polygon polygon;
    private final PolygonRasterizer polygonRasterizer;
    private final Triangle triangle;
    private final SizeValidator sizeValidator;
    private final Polygon clipPolygon;
    private final PolygonRasterizer clipPolygonRasterizer;
    private final int defaultColor = Color.white.getRGB();
    private final int clipPolygonColor = Color.blue.getRGB();

    public Controller(JPanel panel, RasterBufferedImage raster, JTextField drawModeTextField, JTextField fillModeTextField) {
        this.panel = panel;
        this.raster = raster;
        this.drawModeTextField = drawModeTextField;
        this.fillModeTextField = fillModeTextField;
        initListeners();

        lineRasterizer = new FilledLineRasterizer(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster);
        lines = new ArrayList<>();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        sizeValidator = new SizeValidator(raster.getWidth(), raster.getHeight());
        triangle = new Triangle();
        polygon = new Polygon();
        clipPolygon = new Polygon();
        clipPolygonRasterizer = new PolygonRasterizer(new FilledLineRasterizer(raster, clipPolygonColor));

        changeDrawMode(DrawMode.POLYGON);
        changeFillMode(FillMode.BACKGROUND_SEED_FILL);
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (drawMode) {
                    case POLYGON -> handlePolygonMouseClick(e);
                    case TRIANGLE -> handleTriangleMouseClick(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (drawMode) {
                    case LINE -> handleLineMouseReleased(e);
                    case POLYGON -> handlePolygonMouseReleased(e);
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                switch (drawMode) {
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
                    case KeyEvent.VK_RIGHT -> changeDrawMode(drawMode.next());
                    case KeyEvent.VK_LEFT -> changeDrawMode(drawMode.previous());
                    case KeyEvent.VK_T -> changeDrawMode(DrawMode.TRIANGLE);
                    case KeyEvent.VK_ENTER -> clip();
                    case KeyEvent.VK_UP -> changeFillMode(fillMode.next());
                    case KeyEvent.VK_DOWN -> changeFillMode(fillMode.previous());
                }
            }
        });
    }

    private void changeDrawMode(DrawMode mode) {
        drawModeTextField.setText(mode.toString().toLowerCase());
        drawMode = mode;

        switch (mode) {
            case LINE -> rerenderLines();
            case POLYGON -> rerenderPolygons();
            case TRIANGLE -> rerenderTriangle();
        }
        drawModeTextField.repaint();
    }

    private void changeFillMode(FillMode mode) {
        fillModeTextField.setText(mode.toString());
        fillMode = mode;
    }

    private void rerenderLines() {
        raster.clear();
        lines.forEach(lineRasterizer::rasterize);
        panel.repaint();
    }

    private void rerenderPolygons() {
        raster.clear();
        clipPolygonRasterizer.rasterize(clipPolygon);
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
        clipPolygon.clearPoints();
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
        if (e.isControlDown() && !SwingUtilities.isMiddleMouseButton(e)) {
            Point currentPoint = new Point(e.getX(), e.getY());
            Filler filler;

            if (fillMode == FillMode.BACKGROUND_SEED_FILL) {
                filler = new SeedFillerBackground(currentPoint, raster, Color.green.getRGB(), Color.black.getRGB(), sizeValidator);
            } else {
                boolean useClipPolygonColor = SwingUtilities.isRightMouseButton(e);
                // boundaries checking color
                int lineColor = useClipPolygonColor ? clipPolygonColor : defaultColor;
                filler = new SeedFiller(currentPoint, raster, lineColor, Color.green.getRGB(), sizeValidator);
            }

            filler.fill();
            panel.repaint();
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            polygon.addPoint(sizeValidator.validateEventCoordinates(e));
        } else if (SwingUtilities.isRightMouseButton(e)) {
            clipPolygon.addPoint(sizeValidator.validateEventCoordinates(e));
        }

        rerenderPolygons();
    }

    private void handlePolygonMouseDragged(MouseEvent e) {
        boolean isClipPolygon = SwingUtilities.isRightMouseButton(e);

        if ((polygon.getCount() < 2 && !isClipPolygon) || (clipPolygon.getCount() < 2 && isClipPolygon)) return;

        if (startPoint == null) {
            startPoint = new Point(e.getX(), e.getY());

            if (!isClipPolygon) {
                polygon.addPoint(new Point(e.getX(), e.getY()));
            } else {
                clipPolygon.addPoint(new Point(e.getX(), e.getY()));
            }
        }

        Point currentPoint = sizeValidator.validateEventCoordinates(e);

        if (isClipPolygon) {
            clipPolygon.removeLastPoint();
            clipPolygon.addPoint(currentPoint);

            clipPolygonRasterizer.rasterize(clipPolygon);
        } else {
            polygon.removeLastPoint();
            polygon.addPoint(currentPoint);

            polygonRasterizer.rasterize(polygon);
        }


        rerenderPolygons();
    }

    private void handlePolygonMouseReleased(MouseEvent e) {
        if (polygon.getCount() < 1 || e.isControlDown()) return;

        if (SwingUtilities.isLeftMouseButton(e)) {
            polygon.addPoint(sizeValidator.validateEventCoordinates(e));
        } else if (SwingUtilities.isRightMouseButton(e)) {
            clipPolygon.addPoint(sizeValidator.validateEventCoordinates(e));
        }
        rerenderPolygons();
    }

    private void clip() {
        polygon = PolygonClipper.clip(polygon, clipPolygon);

        rerenderPolygons();

        LineRasterizer scanLineRasterizer = new FilledLineRasterizer(raster, Color.pink.getRGB());
        PolygonRasterizer scanLinePolygonRasterizer = new PolygonRasterizer(new FilledLineRasterizer(raster, Color.red.getRGB()));

        ScanLineFiller scanLineFiller = new ScanLineFiller(scanLineRasterizer, scanLinePolygonRasterizer, polygon);
        scanLineFiller.fill();
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
