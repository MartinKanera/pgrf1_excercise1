package model;

public class Line {
    private final Point start, end;

    public Line(final Point start, final Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
