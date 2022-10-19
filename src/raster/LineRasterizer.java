package raster;

import model.Line;
import model.Point;

public abstract class LineRasterizer {
    protected final RasterBufferedImage raster;

    public LineRasterizer(final RasterBufferedImage raster) {
        this.raster = raster;
    }

    public void rasterize(Line line) {
        drawLine(line.getStart(), line.getEnd());
    }

    protected void drawLine(Point start, Point end) {}
}
