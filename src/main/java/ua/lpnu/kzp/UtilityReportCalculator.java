package ua.lpnu.kzp;

import java.util.List;

/** Обчислює показники для набору перевірених записів. */
public final class UtilityReportCalculator {

    private UtilityReportCalculator() {
    }

    /**
     * Обчислює сумарне споживання, вартість і максимум.
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
        return new Summary(records.size(), totalConsumption, totalCost, maximumConsumption);
    }

    /** Результат обчислення показників. */
    public record Summary(int validCount, double totalConsumption, double totalCost,
                          double maximumConsumption) {
    }
}