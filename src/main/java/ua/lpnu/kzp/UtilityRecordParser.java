package ua.lpnu.kzp;

/** Перетворює рядки CSV на перевірені записи варіанта. */
public final class UtilityRecordParser {

    private UtilityRecordParser() {
    }

    /**
     * Розбирає рядок у форматі {@code meter;date;value;tariff}.
     *
     * @param line рядок вхідного файла
     * @return перевірений запис
     * @throws IllegalArgumentException якщо рядок має помилковий формат
     */
    public static UtilityRecord parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("очікується 4 поля, отримано %d".formatted(fields.length));
        }
        String meter = fields[0].trim();
        String date = fields[1].trim();
        if (meter.isEmpty()) {
            throw new IllegalArgumentException("порожня назва лічильника");
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("дата має бути у форматі YYYY-MM-DD");
        }
        double value = parseNonNegative(fields[2], "споживання");
        double tariff = parseNonNegative(fields[3], "тариф");
        return new UtilityRecord(meter, date, value, tariff);
    }

    private static double parseNonNegative(String text, String fieldName) {
        try {
            double number = Double.parseDouble(text.trim());
            if (!Double.isFinite(number) || number < 0) {
                throw new IllegalArgumentException(fieldName + " має бути невід'ємним скінченним числом");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " має помилковий формат");
        }
    }
}