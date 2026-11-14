package ua.lpnu.kzp;

import java.util.List;

/** Обчислює показники для різнорідного набору перевірених показників. */
public final class UtilityReportCalculator {

    private UtilityReportCalculator() {
    }

    /**
     * Обчислює сумарне споживання, вартість і максимум.
     *
     * @param records перевірені показники будь-яких реалізацій базового типу
     * @return агреговані показники
     */
    public static Summary calculate(List<? extends UtilityReading> records) {
        double totalConsumption = 0.0;
        double totalCost = 0.0;
        double maximumConsumption = 0.0;
        for (UtilityReading record : records) {
            totalConsumption += record.value();
            totalCost += record.consumptionCost();
            maximumConsumption = Math.max(maximumConsumption, record.value());
        }
        return new Summary(records.size(), totalConsumption, totalCost, maximumConsumption);
    }

    /** Результат обчислення показників. */
    public record Summary(int validCount, double totalConsumption, double totalCost,
                          double maximumConsumption) {
    }
}