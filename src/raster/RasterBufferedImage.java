package raster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {
    private final BufferedImage img;

    public BufferedImage getImg() {
        return img;
    }

    public RasterBufferedImage(final int width, final int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public void setPixel(int x, int y, int color) {
        img.setRGB(x, y, color);
    }

    @Override
    public int getPixel(int x, int y) {
        return img.getRGB(x, y);
    }

    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(new Color(0x000));
        g.fillRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
    }
}
