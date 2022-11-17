package helpers;

public enum DrawMode {
    LINE,
    POLYGON,
    TRIANGLE;

    static private final DrawMode[] values = values();

    public DrawMode previous() {
        return values[(ordinal() - 1 + values.length) % values.length];
    }

    public DrawMode next() {
        return values[(ordinal() + 1 + values.length) % values.length];
    }
}
