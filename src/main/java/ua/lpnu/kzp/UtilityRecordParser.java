package ua.lpnu.kzp;

/** Сумісний фасад для перетворення рядків CSV на записи варіанта. */
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
        return UtilityRecord.fromCsv(line);
    }
}