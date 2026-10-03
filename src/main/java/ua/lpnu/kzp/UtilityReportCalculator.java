package ua.lpnu.kzp;

import java.util.List;

/** Обчислює показники для набору перевірених записів. */
public final class UtilityReportCalculator {

    private UtilityReportCalculator() {
    }

    /**
     * Обчислює сумарне споживання, вартість, максимум і середнє.
     *
     * @param records перевірені записи
     * @return агреговані показники
     */
    public static Summary calculate(List<UtilityRecord> records) {
        double totalConsumption = 0.0;
        double totalCost = 0.0;
        double maximumConsumption = 0.0;
        for (UtilityRecord record : records) {
            totalConsumption += record.value();
            totalCost += record.value() * record.tariff();
            maximumConsumption = Math.max(maximumConsumption, record.value());
        }
        double averageConsumption = records.isEmpty() ? 0.0 : totalConsumption / records.size();
        return new Summary(records.size(), totalConsumption, totalCost, maximumConsumption, averageConsumption);
    }

    /** Результат обчислення показників. */
    public record Summary(int validCount, double totalConsumption, double totalCost,
                          double maximumConsumption, double averageConsumption) {
    }
}