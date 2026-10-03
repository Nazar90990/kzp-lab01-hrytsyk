package ua.lpnu.kzp;

import java.util.List;
import java.util.Locale;

/** Форматує агреговані показники та помилки у текст звіту. */
public final class ReportFormatter {

    private ReportFormatter() {
    }

    /**
     * Створює звіт із фіксованою точністю та локаллю ROOT.
     *
     * @param summary обчислені показники
     * @param errors причини пропущених рядків
     * @return текст звіту
     */
    public static String format(UtilityReportCalculator.Summary summary, List<String> errors) {
        StringBuilder report = new StringBuilder();
        report.append("=== ЗВІТ: КОМУНАЛЬНІ ПОКАЗНИКИ ===").append(System.lineSeparator());
        report.append(String.format(Locale.ROOT, "Коректних записів: %d%n", summary.validCount()));
        report.append(String.format(Locale.ROOT, "Сумарне споживання: %.2f%n", summary.totalConsumption()));
        report.append(String.format(Locale.ROOT, "Загальна вартість: %.2f грн%n", summary.totalCost()));
        report.append(String.format(Locale.ROOT, "Найбільше споживання: %.2f%n", summary.maximumConsumption()));
        report.append(String.format(Locale.ROOT, "Середнє споживання: %.2f%n", summary.averageConsumption()));
        report.append(String.format(Locale.ROOT, "Помилок: %d%n", errors.size()));
        errors.forEach(error -> report.append(error).append(System.lineSeparator()));
        return report.toString();
    }
}