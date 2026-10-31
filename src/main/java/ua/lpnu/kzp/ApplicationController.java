package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Керує читанням даних, обробкою записів і створенням звіту. */
final class ApplicationController {

    int run(Path input, Path output) {
        List<String> lines;
        try {
            lines = FileReport.readLines(input);
        } catch (IOException exception) {
            System.err.printf("Помилка читання файлу %s: %s%n", input, exception.getMessage());
            return 1;
        }

        List<UtilityRecord> records = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            try {
                records.add(UtilityRecord.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок %d: %s".formatted(index + 1, exception.getMessage()));
            }
        }

        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(records);
        String report = ReportFormatter.format(summary, errors);
        System.out.print(report);

        try {
            FileReport.writeReport(output, report);
            System.out.printf("Звіт успішно записано у файл: %s%n", output);
        } catch (IOException exception) {
            System.err.printf("Помилка запису файлу звіту: %s%n", exception.getMessage());
            return 1;
        }
        return 0;
    }
}
