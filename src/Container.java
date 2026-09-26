public class Container<T> {
    private static final String NAN = "NaN";
    private static final String POSITIVE_INFINITY = "Infinity";
    private static final String NEGATIVE_INFINITY = "-Infinity";
    private T value;

    public Container(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Returns {@code true} if the value is null, an empty string,
     * or a floating-point NaN / ±Infinity.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/25/language/pattern-matching.html">Pattern Matching</a>
     */
    public boolean isEmpty() {
        if (value == null) {
            return true;
        }
        if (value instanceof String s) {
            return s.isEmpty();
        }
        return switch (value.toString()) {
            case "", NAN, POSITIVE_INFINITY, NEGATIVE_INFINITY -> true;
            default -> false;
        };
    }
}