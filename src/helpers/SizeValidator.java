package helpers;

import model.Point;

import java.awt.event.MouseEvent;

public class SizeValidator {
    private final int width;
    private final int height;

    public SizeValidator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Point validateEventCoordinates(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        return validateEventCoordinates(x, y);
    }

    public Point validateEventCoordinates(int x, int y) {
        if (x < 0) x = 0;
        else if (x >= width - 1) x = width - 1;
        else if (y < 0) y = 0;
        else if (y >= height - 1) y = height - 1;

        return new Point(x, y);
    }
}
