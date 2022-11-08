package filler;

import model.Point;
import raster.Raster;

import java.awt.*;

public class SeedFiller implements Filler {
    private final Point point;
    private final int fillColor;
    private final int lineColor;
    private final int bgColor;
    private final Raster raster;
    private final boolean useBackground;

    public SeedFiller(Point point, int bgColor, int lineColor, Raster raster) {
        this.point = point;
        this.fillColor = Color.PINK.getRGB();
        this.lineColor = lineColor;
        this.bgColor = bgColor;
        this.raster = raster;
        this.useBackground = true;
    }

    public SeedFiller(Point point, int bgColor, int lineColor, Raster raster, boolean useBackground) {
        this.point = point;
        this.fillColor = Color.PINK.getRGB();
        this.lineColor = lineColor;
        this.bgColor = bgColor;
        this.raster = raster;
        this.useBackground = useBackground;
    }

    @Override
    public void fill() {
        seedFill(point.getX(), point.getY());
    }

    private void seedFill(int x, int y) {
        int color = raster.getPixel(x, y);

        if ((useBackground && color != bgColor) || (!useBackground && color == lineColor || color == fillColor)) return;

        raster.setPixel(x, y, fillColor);
        seedFill(x, y - 1);
        seedFill(x + 1, y);
        seedFill(x, y + 1);
        seedFill(x - 1, y);
    }
}
