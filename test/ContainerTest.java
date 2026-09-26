import java.util.List;
import java.util.Objects;

// Minimal stdlib-only test runner: each case is a named Runnable,
// a failed check throws AssertionError, any failure makes the exit code 1.
public class ContainerTest {

    private record TestCase(String name, Runnable body) {
    }

    private static final List<TestCase> TESTS = List.of(
            // --- null (any type) ---
            new TestCase("should be empty when String value is null",
                    () -> assertEmpty(new Container<String>(null))),
            new TestCase("should be empty when Integer value is null",
                    () -> assertEmpty(new Container<Integer>(null))),
            new TestCase("should be empty when Float value is null",
                    () -> assertEmpty(new Container<Float>(null))),
            new TestCase("should be empty when Double value is null",
                    () -> assertEmpty(new Container<Double>(null))),
            new TestCase("should be empty when Boolean value is null",
                    () -> assertEmpty(new Container<Boolean>(null))),

            // --- String ---
            new TestCase("should be empty when String is empty",
                    () -> assertEmpty(new Container<>(""))),
            new TestCase("should not be empty when String has text",
                    () -> assertNotEmpty(new Container<>("hello"))),
            new TestCase("should not be empty when String is blank",
                    () -> assertNotEmpty(new Container<>(" "))),
            new TestCase("should not be empty when String is \"null\"",
                    () -> assertNotEmpty(new Container<>("null"))),
            new TestCase("should not be empty when String is \"true\" or \"false\"", () -> {
                assertNotEmpty(new Container<>("true"));
                assertNotEmpty(new Container<>("false"));
            }),
            new TestCase("should not be empty when String differs from NaN only by case",
                    () -> assertNotEmpty(new Container<>("nan"))),
            // Strings are checked only for emptiness; NaN/Infinity text is regular content.
            new TestCase("should not be empty when String is \"NaN\"",
                    () -> assertNotEmpty(new Container<>("NaN"))),
            new TestCase("should not be empty when String is \"Infinity\"",
                    () -> assertNotEmpty(new Container<>("Infinity"))),
            new TestCase("should not be empty when String is \"-Infinity\"",
                    () -> assertNotEmpty(new Container<>("-Infinity"))),
            new TestCase("should become non-empty when Double NaN is replaced by String \"NaN\"", () -> {
                Container<Object> container = new Container<>(Double.NaN);
                assertEmpty(container);
                container.setValue("NaN");
                assertNotEmpty(container);
            }),

            // --- Integer ---
            new TestCase("should not be empty when Integer is zero",
                    () -> assertNotEmpty(new Container<>(0))),
            new TestCase("should not be empty when Integer is positive or negative", () -> {
                assertNotEmpty(new Container<>(42));
                assertNotEmpty(new Container<>(-42));
            }),
            new TestCase("should not be empty when Integer is at its bounds", () -> {
                assertNotEmpty(new Container<>(Integer.MAX_VALUE));
                assertNotEmpty(new Container<>(Integer.MIN_VALUE));
            }),

            // --- Float ---
            new TestCase("should not be empty when Float is regular number", () -> {
                assertNotEmpty(new Container<>(1.5f));
                assertNotEmpty(new Container<>(-1.5f));
            }),
            new TestCase("should not be empty when Float is positive or negative zero", () -> {
                assertNotEmpty(new Container<>(0.0f));
                assertNotEmpty(new Container<>(-0.0f));
            }),
            new TestCase("should not be empty when Float is at its bounds", () -> {
                assertNotEmpty(new Container<>(Float.MAX_VALUE));
                assertNotEmpty(new Container<>(Float.MIN_VALUE));
            }),
            new TestCase("should be empty when Float is NaN",
                    () -> assertEmpty(new Container<>(Float.NaN))),
            new TestCase("should be empty when Float is positive infinity",
                    () -> assertEmpty(new Container<>(Float.POSITIVE_INFINITY))),
            new TestCase("should be empty when Float is negative infinity",
                    () -> assertEmpty(new Container<>(Float.NEGATIVE_INFINITY))),
            new TestCase("should be empty when Float overflows to infinity",
                    () -> assertEmpty(new Container<>(Float.MAX_VALUE * 2))),

            // --- Double ---
            new TestCase("should not be empty when Double is regular number", () -> {
                assertNotEmpty(new Container<>(3.14));
                assertNotEmpty(new Container<>(-3.14));
            }),
            new TestCase("should not be empty when Double is positive or negative zero", () -> {
                assertNotEmpty(new Container<>(0.0));
                assertNotEmpty(new Container<>(-0.0));
            }),
            new TestCase("should not be empty when Double is at its bounds", () -> {
                assertNotEmpty(new Container<>(Double.MAX_VALUE));
                assertNotEmpty(new Container<>(Double.MIN_VALUE));
            }),
            new TestCase("should be empty when Double is NaN",
                    () -> assertEmpty(new Container<>(Double.NaN))),
            new TestCase("should be empty when Double is result of 0.0 / 0.0",
                    () -> assertEmpty(new Container<>(0.0 / 0.0))),
            new TestCase("should be empty when Double is positive infinity",
                    () -> assertEmpty(new Container<>(Double.POSITIVE_INFINITY))),
            new TestCase("should be empty when Double is negative infinity",
                    () -> assertEmpty(new Container<>(Double.NEGATIVE_INFINITY))),
            new TestCase("should be empty when Double is result of division by zero", () -> {
                assertEmpty(new Container<>(1.0 / 0.0));
                assertEmpty(new Container<>(-1.0 / 0.0));
            }),

            // --- Boolean ---
            new TestCase("should not be empty when Boolean is true",
                    () -> assertNotEmpty(new Container<>(true))),
            new TestCase("should not be empty when Boolean is false",
                    () -> assertNotEmpty(new Container<>(false))),

            // --- getValue / setValue ---
            new TestCase("should return constructor value when getValue is called", () -> {
                assertEquals("hello", new Container<>("hello").getValue());
                assertEquals(42, new Container<>(42).getValue());
                assertEquals(1.5f, new Container<>(1.5f).getValue());
                assertEquals(3.14, new Container<>(3.14).getValue());
                assertEquals(true, new Container<>(true).getValue());
            }),
            new TestCase("should return new value when setValue is called", () -> {
                Container<Integer> container = new Container<>(1);
                container.setValue(2);
                assertEquals(2, container.getValue());
            }),
            new TestCase("should become empty when value is set to null", () -> {
                Container<String> container = new Container<>("hello");
                container.setValue(null);
                assertEmpty(container);
            }),
            new TestCase("should become empty when Double value is set to NaN", () -> {
                Container<Double> container = new Container<>(1.0);
                container.setValue(Double.NaN);
                assertEmpty(container);
            }),
            new TestCase("should become non-empty when empty value is replaced", () -> {
                Container<String> container = new Container<>("");
                container.setValue("filled");
                assertNotEmpty(container);
            })
    );

    public static void main() {
        int failed = 0;
        for (TestCase test : TESTS) {
            try {
                test.body().run();
                System.out.println("PASS  " + test.name());
            } catch (AssertionError | RuntimeException e) {
                failed++;
                System.out.println("FAIL  " + test.name() + " -> " + e);
            }
        }
        System.out.printf("%n%d tests, %d passed, %d failed%n", TESTS.size(), TESTS.size() - failed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertEmpty(Container<?> container) {
        if (!container.isEmpty()) {
            throw new AssertionError("expected empty, value = " + describe(container.getValue()));
        }
    }

    private static void assertNotEmpty(Container<?> container) {
        if (container.isEmpty()) {
            throw new AssertionError("expected not empty, value = " + describe(container.getValue()));
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected " + describe(expected) + ", got " + describe(actual));
        }
    }

    private static String describe(Object value) {
        return value == null ? "null" : value.getClass().getSimpleName() + "(" + value + ")";
    }
}
