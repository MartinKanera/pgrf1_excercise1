package raster;

import model.Line;
import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    protected final LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        if (polygon.getCount() < 2) return;

        for(int i = 0; i < polygon.getCount() - 1; i++) {
            Point point1 = polygon.getPoint(i);
            Point point2 = polygon.getPoint(i + 1);
            Line line = new Line(point1, point2);
            lineRasterizer.rasterize(line);
        }

        Point endPoint = polygon.getPoint(polygon.getCount() - 1);
        Point startPoint = polygon.getPoint(0);
        Line line = new Line(endPoint, startPoint);
        lineRasterizer.rasterize(line);
    }
}
