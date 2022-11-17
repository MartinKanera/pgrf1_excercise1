package raster;

import model.Line;
import model.Point;

import java.awt.*;

public abstract class LineRasterizer {
    protected final RasterBufferedImage raster;
    protected final int color;

    public LineRasterizer(final RasterBufferedImage raster, final int color) {
        this.raster = raster;
        this.color = color;
    }

    public LineRasterizer(final RasterBufferedImage raster) {
        this.raster = raster;
        this.color = Color.WHITE.getRGB();
    }

    public void rasterize(Line line) {
        drawLine(line.getStart(), line.getEnd());
    }

    protected void drawLine(Point start, Point end) {}
}
