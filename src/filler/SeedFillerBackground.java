package filler;

import helpers.SizeValidator;
import model.Point;
import raster.Raster;

public class SeedFillerBackground implements Filler{
    private final int bgColor;
    private final Point point;
    private final int fillColor;
    private final Raster raster;
    private final SizeValidator sizeValidator;

    public SeedFillerBackground(Point point, Raster raster, int fillColor, int bgColor, SizeValidator sizeValidator) {
        this.point = point;
        this.fillColor = fillColor;
        this.raster = raster;
        this.sizeValidator = sizeValidator;
        this.bgColor = bgColor;
    }

    public void fill() {
        seedFill(point.getX(), point.getY());
    }

    private void seedFill(int x, int y) {
        int color = raster.getPixel(x, y);

        if (color != bgColor) return;

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
