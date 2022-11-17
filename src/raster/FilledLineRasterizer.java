package raster;

import model.Point;

import java.awt.*;

public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(RasterBufferedImage raster) {
        super(raster);
    }
    public FilledLineRasterizer(RasterBufferedImage raster, int color) {
        super(raster, color);
    }

    @Override
    protected void drawLine(Point start, Point end) {
        Graphics g = raster.getImg().getGraphics();
        g.setColor(new Color(color));
        g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    }
}
