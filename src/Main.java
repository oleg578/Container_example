import java.util.List;

public class Main {
    void main() {
        List<Container<?>> containers = List.of(
                new Container<String>(null),
                new Container<>(""),
                new Container<>("hello"),
                new Container<>("Infinity"),
                new Container<>("-Infinity"),
                new Container<>(0),
                new Container<>(42),
                new Container<>(1.5f),
                new Container<>(Float.NaN),
                new Container<>(3.14),
                new Container<>(1.0 / 0.0),
                new Container<>(-1.0 / 0.0),
                new Container<>(true),
                new Container<>(false)
        );

        IO.println("Value                 | Type     | isEmpty");
        IO.println("----------------------+----------+--------");
        for (Container<?> container : containers) {
            printRow(container);
        }

        IO.println("");
        IO.println("setValue demo:");
        Container<Double> container = new Container<>(2.5);
        IO.println("  initial 2.5         -> isEmpty = " + container.isEmpty());
        container.setValue(Double.NaN);
        IO.println("  after setValue(NaN) -> isEmpty = " + container.isEmpty());
        container.setValue(7.0);
        IO.println("  after setValue(7.0) -> isEmpty = " + container.isEmpty());
    }

    private static void printRow(Container<?> container) {
        Object value = container.getValue();
        String shownValue = value instanceof String s ? "\"" + s + "\"" : String.valueOf(value);
        String typeName = value == null ? "-" : value.getClass().getSimpleName();
        IO.println(String.format("%-21s | %-8s | %s", shownValue, typeName, container.isEmpty()));
    }
}
