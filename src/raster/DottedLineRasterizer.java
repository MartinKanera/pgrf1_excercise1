package raster;

import model.Point;

public class DottedLineRasterizer extends LineRasterizer {
    public DottedLineRasterizer(RasterBufferedImage raster) {
        super(raster);
    }

    private void lineLow(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int yi = 1;
        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int p = 2 * dy - dx;
        int y = y1;


        for (int x = x1; x < x2; x += 2) {
            raster.setPixel(x, y, 0xFFFFFF);
            if (p > 0) {
                y = y + 2 * yi;
                p += 2 * (dy - dx);
            } else {
                p += 2 * dy;
            }
        }
    }

    private void lineHigh(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int p = 2 * dx - dy;
        int x = x1;


        for (int y = y1; y < y2; y += 2) {
            raster.setPixel(x, y, 0xFFFFFF);
            if (p > 0) {
                x = x + 2 * xi;
                p += 2 * (dx - dy);
            } else {
                p += 2 * dx;
            }
        }
    }

    @Override
    protected void drawLine(Point start, Point end) {
        int x1 = start.getX();
        int y1 = start.getY();
        int x2 = end.getX();
        int y2 = end.getY();

        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {
            if (x1 > x2) {
                lineLow(x2, y2, x1, y1);
            } else {
                lineLow(x1, y1, x2, y2);
            }
        } else {
            if (y1 > y2) {
                lineHigh(x2, y2, x1, y1);
            } else {
                lineHigh(x1, y1, x2, y2);
            }
        }
    }
}
