package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Polygon {
    private final List<Point> points;

    public Polygon() {
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public int getCount() {
        return points.size();
    }

    public void clearPoints() {
        points.clear();
    }

    public Optional<Point> getLastPoint() {
        if (points.isEmpty()) return Optional.empty();
        return Optional.of(points.get(getCount() - 1));
    }

    public void removeLastPoint() {
        if (points.isEmpty()) return;
        points.remove(getCount() - 1);
    }
}
