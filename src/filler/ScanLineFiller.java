package filler;

import model.Edge;
import model.Line;
import model.Polygon;
import raster.LineRasterizer;
import raster.PolygonRasterizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLineFiller implements Filler {
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final Polygon polygon;

    public ScanLineFiller(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer, Polygon polygon) {
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
        this.polygon = polygon;
    }

    @Override
    public void fill() {
        scanLine();
    }

    private void scanLine() {
        List<Edge> edges = polygon.getNonHorizontalEdges();

        int yMin = polygon.getPoint(0).getY();
        int yMax = yMin;

        for (int i = 1; i < polygon.getCount(); i++) {
            int y = polygon.getPoint(i).getY();

            if (y < yMin) yMin = y;
            else if (y > yMax) yMax = y;
        }

        for (int y = yMin; y < yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            for (Edge edge : edges) {
                if (!edge.isIntersection(y)) continue;

                intersections.add(edge.getIntersection(y));
            }

            Collections.sort(intersections);

            for (int i = 1; i < intersections.size(); i += 2) {
                int x1 = intersections.get(i - 1);
                int x2 = intersections.get(i);
                Line line = new Line(x1, y, x2, y);
                lineRasterizer.rasterize(line);
            }
        }
        polygonRasterizer.rasterize(polygon);
    }
}
