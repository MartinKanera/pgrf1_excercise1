package model;
public enum Mode {
    LINE,
    POLYGON,
    TRIANGLE;

    static private final Mode[] values = values();

    public Mode previous() {
        return values[(ordinal() - 1 + values.length) % values.length];
    }

    public Mode next() {
        return values[(ordinal() + 1 + values.length) % values.length];
    }
}
