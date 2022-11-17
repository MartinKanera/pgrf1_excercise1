package helpers;

public enum FillMode {
    BACKGROUND_SEED_FILL,
    LINE_SEED_FILL;

    static private final FillMode[] values = values();

    public FillMode previous() {
        return values[(ordinal() - 1 + values.length) % values.length];
    }

    public FillMode next() {
        return values[(ordinal() + 1 + values.length) % values.length];
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace('_', ' ');
    }
}
