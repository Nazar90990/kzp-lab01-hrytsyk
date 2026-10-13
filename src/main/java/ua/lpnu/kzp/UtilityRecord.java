package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Незмінний клас-сутність одного комунального показника.
 *
 * <p>Клас зберігає тільки перевірені значення. Усі правила коректності
 * застосовуються і конструктором, і фабрикою {@link #fromCsv(String)}.</p>
 */
public final class UtilityRecord {

    private final String meter;
    private final String date;
    private final double value;
    private final double tariff;

    /**
     * Створює коректний запис комунального показника.
     *
     * @param meter назва або ідентифікатор лічильника
     * @param date дата у форматі YYYY-MM-DD
     * @param value обсяг споживання
     * @param tariff тариф за одиницю споживання
     * @throws IllegalArgumentException якщо порушено інваріант запису
     */
    public UtilityRecord(String meter, String date, double value, double tariff) {
        this.meter = requireText(meter, "назва лічильника");
        this.date = requireDate(date);
        this.value = requireNonNegative(value, "споживання");
        this.tariff = requireNonNegative(tariff, "тариф");
    }

    /**
     * Створює запис із рядка формату {@code meter;date;value;tariff}.
     *
     * @param line рядок вхідного файла
     * @return перевірений запис
     * @throws IllegalArgumentException якщо структура або значення рядка помилкові
     */
    public static UtilityRecord fromCsv(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("очікується 4 поля, отримано %d".formatted(fields.length));
        }
        double value = parseNumber(fields[2], "споживання");
        double tariff = parseNumber(fields[3], "тариф");
        return new UtilityRecord(fields[0].trim(), fields[1].trim(), value, tariff);
    }

    /** @return назва або ідентифікатор лічильника */
    public String meter() {
        return meter;
    }

    /** @return дата зняття показника */
    public String date() {
        return date;
    }

    /** @return обсяг споживання */
    public double value() {
        return value;
    }

    /** @return тариф за одиницю споживання */
    public double tariff() {
        return tariff;
    }

    /** @return назва або ідентифікатор лічильника */
    public String getMeter() {
        return meter();
    }

    /** @return дата зняття показника */
    public String getDate() {
        return date();
    }

    /** @return обсяг споживання */
    public double getValue() {
        return value();
    }

    /** @return тариф за одиницю споживання */
    public double getTariff() {
        return tariff();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "UtilityRecord{meter='%s', date='%s', value=%.2f, tariff=%.2f}",
                meter, date, value, tariff);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UtilityRecord record)) {
            return false;
        }
        return Double.compare(value, record.value) == 0
                && Double.compare(tariff, record.tariff) == 0
                && meter.equals(record.meter)
                && date.equals(record.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meter, date, value, tariff);
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
}
