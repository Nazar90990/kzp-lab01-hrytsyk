package ua.lpnu.kzp;

import java.util.Objects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Спільний незмінний тип показника комунальної послуги.
 *
 * <p>Зберігає перевірені спільні поля та визначає поліморфну операцію вартості.</p>
 */
public abstract class UtilityReading {

    private final String meter;
    private final String date;
    private final double value;
    private final double tariff;
    private final UtilityKind kind;

    /**
     * Створює показник із перевіреними спільними полями.
     *
     * @param meter назва або ідентифікатор лічильника
     * @param date дата у форматі YYYY-MM-DD
     * @param value обсяг споживання
     * @param tariff тариф за одиницю споживання
     * @param kind категорія послуги
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW",
            justification = "Construction must reject invalid shared state to preserve the value-object invariants.")
    protected UtilityReading(String meter, String date, double value, double tariff, UtilityKind kind) {
        this.meter = requireText(meter, "назва лічильника");
        this.date = requireDate(date);
        this.value = requireNonNegative(value, "споживання");
        this.tariff = requireNonNegative(tariff, "тариф");
        this.kind = Objects.requireNonNull(kind, "категорія не може бути null");
        if (kind != UtilityKind.OTHER && UtilityKind.fromLabel(this.meter) != kind) {
            throw new IllegalArgumentException("назва лічильника не відповідає категорії " + kind.label());
        }
    }

    /**
     * Створює відповідний підтип із рядка формату {@code meter;date;value;tariff}.
     *
     * <p>Невідомі типи створюються як сумісний {@link UtilityRecord}.</p>
     *
     * @param line рядок вхідного файла
     * @return перевірений показник відповідного підтипу
     * @throws IllegalArgumentException якщо структура або значення рядка помилкові
     */
    public static UtilityReading fromCsv(String line) {
        CsvFields fields = parseCsvFields(line);
        return switch (UtilityKind.fromLabel(fields.meter())) {
            case ELECTRICITY -> new ElectricityReading(fields.meter(), fields.date(), fields.value(), fields.tariff());
            case WATER -> new WaterReading(fields.meter(), fields.date(), fields.value(), fields.tariff());
            case OTHER -> new UtilityRecord(fields.meter(), fields.date(), fields.value(), fields.tariff());
        };
    }

    /** @return назва або ідентифікатор лічильника */
    public final String meter() {
        return meter;
    }

    /** @return дата зняття показника */
    public final String date() {
        return date;
    }

    /** @return обсяг споживання */
    public final double value() {
        return value;
    }

    /** @return тариф за одиницю споживання */
    public final double tariff() {
        return tariff;
    }

    /** @return категорія комунальної послуги */
    public final UtilityKind kind() {
        return kind;
    }

    /** @return вартість споживання цього показника */
    public abstract double consumptionCost();

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        UtilityReading reading = (UtilityReading) other;
        return Double.compare(value, reading.value) == 0
                && Double.compare(tariff, reading.tariff) == 0
                && meter.equals(reading.meter)
                && date.equals(reading.date)
                && kind == reading.kind;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), meter, date, value, tariff, kind);
    }

    static CsvFields parseCsvFields(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("очікується 4 поля, отримано %d".formatted(fields.length));
        }
        double value = parseNumber(fields[2], "споживання");
        double tariff = parseNumber(fields[3], "тариф");
        String meter = requireText(fields[0].trim(), "назва лічильника");
        String date = requireDate(fields[1].trim());
        requireNonNegative(value, "споживання");
        requireNonNegative(tariff, "тариф");
        return new CsvFields(meter, date, value, tariff);
    }

    private static String requireText(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("порожня " + fieldName);
        }
        return text.trim();
    }

    private static String requireDate(String date) {
        String normalized = requireText(date, "дата");
        if (!normalized.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("дата має бути у форматі YYYY-MM-DD");
        }
        return normalized;
    }

    private static double parseNumber(String text, String fieldName) {
        try {
            return requireNonNegative(Double.parseDouble(text.trim()), fieldName);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " має помилковий формат");
        }
    }

    private static double requireNonNegative(double number, String fieldName) {
        if (!Double.isFinite(number) || number < 0) {
            throw new IllegalArgumentException(fieldName + " має бути невід'ємним скінченним числом");
        }
        return number;
    }

    record CsvFields(String meter, String date, double value, double tariff) {
    }
}
