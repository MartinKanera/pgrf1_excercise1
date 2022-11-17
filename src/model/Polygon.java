package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Polygon {
    private final List<Point> points;

    public Polygon() {
        points = new ArrayList<>();
    }

    public Polygon(List<Point> points) {
        this.points = points;
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

    public List<Point> getPoints() { return points; }

    private List<Edge> getEdges(List<Point> points, boolean includeHorizontal, boolean orientateDown) {
        List<Edge> edges = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            int nextIndex = (i + 1) % points.size();
            Point point1 = points.get(i);
            Point point2 = points.get(nextIndex);

            Edge edge = new Edge(point1, point2);

            if (!includeHorizontal && edge.isHorizontal()) continue;
            if (orientateDown) edge.orientate();
            edges.add(edge);
        }

        return edges;
    }

    public List<Edge> getNonHorizontalEdges() {
        return getEdges(points,false, true);
    }

    public List<Edge> getSortedEdges() { return getEdges(getSortedPoints(), true, false); }

    public void removeLastPoint() {
        if (points.isEmpty()) return;
        points.remove(getCount() - 1);
    }

    public Point findCentroid() {
        int x = 0;
        int y = 0;

        for (Point p : points) {
            x += p.getX();
            y += p.getY();
        }

        // C = (A1 + A2 + ... + An) / n
        return new Point(x / points.size(), y / points.size());
    }

    private List<Point> getSortedPoints() {
        Point center = findCentroid();

        List<Point> sortedPoints = new ArrayList<>(points);

        // Seřazení bodů n-úhelníku counterclockwise
        // atan2 - převod souřadnic na radiány - definováno pomocí kružnice (atan2(1, 1) = Pi/4)
        // využití úhlu na seřazení
        sortedPoints.sort((a, b) -> {
            double a1 = (Math.toDegrees(Math.atan2(a.getX() - center.getX(), a.getY() - center.getY())) + 360) % 360;
            double a2 = (Math.toDegrees(Math.atan2(b.getX() - center.getX(), b.getY() - center.getY())) + 360) % 360;
            return (int) (a1 - a2);
        });

        return sortedPoints;
    }
}
