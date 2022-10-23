package model;

public class Triangle extends Polygon {
    public void calculateAndSetTop(int topPointY) {
        Point basePoint1 = getPoint(0);
        Point basePoint2 = getPoint(1);

        int x1 = basePoint1.getX();
        int y1 = basePoint1.getY();
        int x2 = basePoint2.getX();
        int y2 = basePoint2.getY();

        float originalK = (y2 - y1) / (float)(x2 - x1);

        int mX = (x2 + x1) / 2;
        int mY = (y2 + y1) / 2;
        float k = -1 / originalK;
        float q = mY - k * mX;

        float topPointX = (topPointY - q) / k;

        if (getCount() > 2) removeLastPoint();
        addPoint(new Point(Math.round(topPointX), topPointY));
    }
}
