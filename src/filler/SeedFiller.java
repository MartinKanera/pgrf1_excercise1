package filler;

import helpers.SizeValidator;
import model.Point;
import raster.Raster;

public class SeedFiller implements Filler {
    private final Point point;
    private final int fillColor;
    private final int lineColor;
    private final Raster raster;
    private final SizeValidator sizeValidator;

    public SeedFiller(Point point, Raster raster, int lineColor, int fillColor, SizeValidator sizeValidator) {
        this.point = point;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.raster = raster;
        this.sizeValidator = sizeValidator;
    }

    @Override
    public void fill() {
        seedFill(point.getX(), point.getY());
    }

    private void seedFill(int x, int y) {
        int color = raster.getPixel(x, y);

        if (color == lineColor || color == fillColor) return;

        raster.setPixel(x, y, fillColor);

        if (sizeValidator.isValid(x, y - 1))
            seedFill(x, y - 1);
        if (sizeValidator.isValid(x + 1, y))
            seedFill(x + 1, y);
        if (sizeValidator.isValid(x, y + 1))
            seedFill(x, y + 1);
        if (sizeValidator.isValid(x - 1, y))
            seedFill(x - 1, y);
    }
}
