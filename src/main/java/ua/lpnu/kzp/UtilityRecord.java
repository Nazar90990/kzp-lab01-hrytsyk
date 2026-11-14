package ua.lpnu.kzp;

import java.util.Locale;

/**
 * Сумісний конкретний тип для попереднього API та невідомих видів послуг.
 *
 * <p>Зберігає вихідний конструктор, фабрику CSV, accessor-и й формат {@code toString}.</p>
 */
public final class UtilityRecord extends UtilityReading {

    /**
     * Створює сумісний запис комунального показника.
     *
     * @param meter назва або ідентифікатор лічильника
     * @param date дата у форматі YYYY-MM-DD
     * @param value обсяг споживання
     * @param tariff тариф за одиницю споживання
     */
    public UtilityRecord(String meter, String date, double value, double tariff) {
        super(meter, date, value, tariff, UtilityKind.OTHER);
    }

    /**
     * Створює сумісний запис із рядка формату {@code meter;date;value;tariff}.
     *
     * @param line рядок вхідного файла
     * @return перевірений запис
     * @throws IllegalArgumentException якщо структура або значення рядка помилкові
     */
    public static UtilityRecord fromCsv(String line) {
        CsvFields fields = parseCsvFields(line);
        return new UtilityRecord(fields.meter(), fields.date(), fields.value(), fields.tariff());
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

    /** @return вартість за незмінною формулою value * tariff */
    @Override
    public double consumptionCost() {
        return value() * tariff();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "UtilityRecord{meter='%s', date='%s', value=%.2f, tariff=%.2f}",
                meter(), date(), value(), tariff());
    }
}
