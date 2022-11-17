package clipper;

import model.Edge;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonClipper {
    static public Polygon clip(Polygon polygon, Polygon clipPolygon) {
        List<Point> in = polygon.getPoints();
        List<Point> out = new ArrayList<>();

        for (Edge edge : clipPolygon.getSortedEdges()) {
            out.clear();

            Point v1 = in.get(in.size() - 1);

            for (Point v2 : in) {
                if (edge.isInside(v2)) {
                    if (!edge.isInside(v1)) {
                        out.add(edge.getIntersection(new Edge(v1, v2)));
                    }
                    out.add(v2);
                } else {
                    if (edge.isInside(v1)) {
                        out.add(edge.getIntersection(new Edge(v1, v2)));
                    }
                }
                v1 = v2;
            }
            in = new ArrayList<>(out);
        }

        return new Polygon(out);
    }
}
