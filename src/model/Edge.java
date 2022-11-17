package model;

public class Edge {
    private int x1, y1, x2, y2;

    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Edge(Point point1, Point point2) {
        x1 = point1.getX();
        y1 = point1.getY();
        x2 = point2.getX();
        y2 = point2.getY();
    }

    public Point getStart() {
        return new Point(x1, y1);
    }

    public Point getEnd() {
        return new Point(x2, y2);
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public void orientate() {
        if (y2 > y1) return;

        int temp = x1;
        x1 = x2;
        x2 = temp;

        temp = y1;
        y1 = y2;
        y2 = temp;
    }

    public boolean isIntersection(int y) {
        return y >= y1 && y < y2;
    }

    public int getIntersection(int y) {
        int dy = y2 - y1;
        int dx = x2 - x1;

        float k = dy / (float) dx;
        float q = y1 - k * x1;

        float x = (y - q) / k;
        return Math.round(x);
    }

    private boolean isVertical() {
        return x2 - x1 == 0;
    }

    private float getSlope() {
        return (y2 - y1) / (float)(x2 - x1);
    }

    private float getOffset() {
        return y1 - getSlope() * x1;
    }

    public Point getIntersection(Edge edge) {
        int x, y;
        float k1, k2, q1, q2;

        if (isVertical()) {
            x = x1;

            k2 = edge.getSlope();
            q2 = edge.getOffset();

            y = Math.round(k2 * x + q2);
        } else if (edge.isVertical()) {
            x =  edge.getStart().getX();

            k1 = getSlope();
            q1 = getOffset();

            y = Math.round(k1 * x + q1);
        } else {
            k1 = getSlope();
            q1 = getOffset();
            k2 = edge.getSlope();
            q2 = edge.getOffset();

            x = Math.round((q2 - q1) / (k1 - k2));
            y = Math.round(k1 * x + q1);
        }

        return new Point(x, y);
    }

    public boolean isInside(Point point) {
        int x = point.getX();
        int y = point.getY();

        float r = (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1);

        return r <= 0;
    }
}
